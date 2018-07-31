package guru.qas.martini.intellij.plugin.steps;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.inspections.MartiniStepDefinitionCreationContext;
import org.jetbrains.plugins.cucumber.psi.GherkinFile;

import java.util.*;
import java.util.regex.Pattern;

import guru.qas.martini.intellij.plugin.BDDFrameworkType;
import guru.qas.martini.intellij.plugin.MartiniJvmExtensionPoint;
import guru.qas.martini.intellij.plugin.OptionalStepDefinitionExtensionPoint;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStep;

/**
 * @author yole
 */
public class MartiniStepsIndex {
  private static final Logger LOG = Logger.getInstance(MartiniStepsIndex.class.getName());

  private final Map<BDDFrameworkType, MartiniJvmExtensionPoint> myExtensionMap;
  private final Map<MartiniJvmExtensionPoint, Object> myExtensionData;
  private Project myProject;

  public static MartiniStepsIndex getInstance(Project project) {
    MartiniStepsIndex result = ServiceManager.getService(project, MartiniStepsIndex.class);
    result.myProject = project;

    return result;
  }

  public MartiniStepsIndex(final Project project) {
    myExtensionMap = new HashMap<>();
    myExtensionData = new HashMap<>();

    for (MartiniJvmExtensionPoint e : Extensions.getExtensions(MartiniJvmExtensionPoint.EP_NAME)) {
      myExtensionMap.put(e.getStepFileType(), e);
      myExtensionData.put(e, e.getDataObject(project));
    }
  }

  public Object getExtensionDataObject(MartiniJvmExtensionPoint e) {
    return myExtensionData.get(e);
  }

  /**
   * Creates a file that will contain step definitions
   *
   * @param dir                      container for created file
   * @param fileNameWithoutExtension name of the file with out "." and extension
   * @param frameworkType            type of file to create
   */
  public PsiFile createStepDefinitionFile(@NotNull final PsiDirectory dir,
                                          @NotNull final String fileNameWithoutExtension,
                                          @NotNull final BDDFrameworkType frameworkType) {
    final MartiniJvmExtensionPoint ep = myExtensionMap.get(frameworkType);
    if (ep == null) {
      LOG.error(String.format("Unsupported step definition file type %s", frameworkType.toString()));
      return null;
    }

    return ep.getStepDefinitionCreator().createStepDefinitionContainer(dir, fileNameWithoutExtension);
  }

  public boolean validateNewStepDefinitionFileName(@NotNull final PsiDirectory directory,
                                                   @NotNull final String fileName,
                                                   @NotNull final BDDFrameworkType frameworkType) {
    final MartiniJvmExtensionPoint ep = myExtensionMap.get(frameworkType);
    assert ep != null;
    return ep.getStepDefinitionCreator().validateNewStepDefinitionFileName(directory.getProject(), fileName);
  }


  /**
   * Searches for step definition.
   * More info is available in {@link #findStepDefinitions(PsiFile, GherkinStep)} doc
   *
   * @param featureFile file with steps
   * @param step        step itself
   * @return definition or null if not found
   * @see #findStepDefinitions(PsiFile, GherkinStep)
   */
  @Nullable
  public AbstractStepDefinition findStepDefinition(@NotNull final PsiFile featureFile, @NotNull final GherkinStep step) {
    final Collection<AbstractStepDefinition> definitions = findStepDefinitions(featureFile, step);
    return (definitions.isEmpty() ? null : definitions.iterator().next());
  }

  /**
   * Searches for ALL step definitions, groups it by step definition class and sorts by pattern size.
   * For each step definition class it finds the largest pattern.
   *
   * @param featureFile file with steps
   * @param step        step itself
   * @return definitions
   */
  @NotNull
  public Collection<AbstractStepDefinition> findStepDefinitions(@NotNull final PsiFile featureFile, @NotNull final GherkinStep step) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(featureFile);
    if (module == null) {
      return Collections.emptyList();
    }
    String substitutedName = step.getSubstitutedName();
    if (substitutedName == null) {
      return Collections.emptyList();
    }

    Map<Class<? extends AbstractStepDefinition>, AbstractStepDefinition> definitionsByClass =
      new HashMap<>();
    List<AbstractStepDefinition> allSteps = loadStepsFor(featureFile, module);

    for (AbstractStepDefinition stepDefinition : allSteps) {
      if (stepDefinition.matches(substitutedName) && stepDefinition.supportsStep(step)) {
        final Pattern currentLongestPattern = getPatternByDefinition(definitionsByClass.get(stepDefinition.getClass()));
        final Pattern newPattern = getPatternByDefinition(stepDefinition);
        final int newPatternLength = ((newPattern != null) ? newPattern.pattern().length() : -1);
        if ((currentLongestPattern == null) || (currentLongestPattern.pattern().length() < newPatternLength)) {
          definitionsByClass.put(stepDefinition.getClass(), stepDefinition);
        }
      }
    }
    return definitionsByClass.values();
  }

  /**
   * Returns pattern from step definition (if exists)
   *
   * @param definition step definition
   * @return pattern or null if does not exist
   */
  @Nullable
  private static Pattern getPatternByDefinition(@Nullable final AbstractStepDefinition definition) {
    if (definition == null) {
      return null;
    }
    return definition.getPattern();
  }

  // ToDo: use binary search here
  public List<AbstractStepDefinition> findStepDefinitionsByPattern(@NotNull final String pattern, @NotNull final Module module) {
    final List<AbstractStepDefinition> allSteps = loadStepsFor(null, module);
    final List<AbstractStepDefinition> result = new ArrayList<>();
    for (AbstractStepDefinition stepDefinition : allSteps) {
      final String elementText = stepDefinition.getCucumberRegex();
      if (elementText != null && elementText.equals(pattern)) {
        result.add(stepDefinition);
      }
    }
    return result;
  }

  public List<AbstractStepDefinition> getAllStepDefinitions(@NotNull final PsiFile featureFile) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(featureFile);
    if (module == null) return Collections.emptyList();
    return loadStepsFor(featureFile, module);
  }

  @NotNull
  public List<PsiFile> gatherStepDefinitionsFilesFromDirectory(@NotNull final PsiDirectory dir, final boolean writableOnly) {
    final List<PsiFile> result = new ArrayList<>();

    // find step definitions in current folder
    for (PsiFile file : dir.getFiles()) {
      final VirtualFile virtualFile = file.getVirtualFile();
      boolean isStepFile = writableOnly ? isWritableStepLikeFile(file, file.getParent()) : isStepLikeFile(file, file.getParent());
      if (isStepFile && virtualFile != null) {
        result.add(file);
      }
    }
    // process subfolders
    for (PsiDirectory subDir : dir.getSubdirectories()) {
      result.addAll(gatherStepDefinitionsFilesFromDirectory(subDir, writableOnly));
    }

    return result;
  }

  private List<AbstractStepDefinition> loadStepsFor(@Nullable final PsiFile featureFile, @NotNull final Module module) {
    ArrayList<AbstractStepDefinition> result = new ArrayList<>();

    for (MartiniJvmExtensionPoint extension : myExtensionMap.values()) {
      result.addAll(extension.loadStepsFor(featureFile, module));
    }
    return result;
  }

  public Set<MartiniStepDefinitionCreationContext> getStepDefinitionContainers(@NotNull final GherkinFile featureFile) {
    Set<MartiniStepDefinitionCreationContext> result = new HashSet<>();
    for (MartiniJvmExtensionPoint ep : myExtensionMap.values()) {
      // Skip if framework file creation support is optional
      if ((ep instanceof OptionalStepDefinitionExtensionPoint) &&
          !((OptionalStepDefinitionExtensionPoint)ep).participateInStepDefinitionCreation(featureFile)) {
        continue;
      }
      final Collection<? extends PsiFile> psiFiles = ep.getStepDefinitionContainers(featureFile);
      final BDDFrameworkType frameworkType = ep.getStepFileType();
      for (final PsiFile psiFile : psiFiles) {
        result.add(new MartiniStepDefinitionCreationContext(psiFile, frameworkType));
      }
    }
    return result;
  }

  public void reset() {
    for (MartiniJvmExtensionPoint e : myExtensionMap.values()) {
      e.reset(myProject);
    }
  }

  public void flush() {
    for (MartiniJvmExtensionPoint e : myExtensionMap.values()) {
      e.flush(myProject);
    }
  }

  public Map<BDDFrameworkType, MartiniJvmExtensionPoint> getExtensionMap() {
    return myExtensionMap;
  }

  public int getExtensionCount() {
    return myExtensionMap.size();
  }

  private boolean isStepLikeFile(PsiElement child, PsiElement parent) {
    if (child instanceof PsiFile) {
      final PsiFile file = (PsiFile)child;
      MartiniJvmExtensionPoint ep = myExtensionMap.get(new BDDFrameworkType(file.getFileType()));
      return ep != null && ep.isStepLikeFile(file, parent);
    }

    return false;
  }

  private boolean isWritableStepLikeFile(PsiElement child, PsiElement parent) {
    if (child instanceof PsiFile) {
      final PsiFile file = (PsiFile)child;
      MartiniJvmExtensionPoint ep = myExtensionMap.get(new BDDFrameworkType(file.getFileType()));
      return ep != null && ep.isWritableStepLikeFile(file, parent);
    }

    return false;
  }
}
