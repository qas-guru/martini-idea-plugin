package guru.qas.martini.intellij.plugin.gherkin.psi;

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.jetbrains.plugins.cucumber.psi.GherkinPsiElement;
import org.jetbrains.plugins.cucumber.psi.GherkinPystring;
import org.jetbrains.plugins.cucumber.psi.GherkinSuppressionHolder;
import org.jetbrains.plugins.cucumber.psi.GherkinTable;

import com.intellij.lang.ASTNode;
import com.intellij.pom.PomTarget;
import com.intellij.psi.PsiNamedElement;

import guru.qas.martini.intellij.plugin.MartiniBundle;
import guru.qas.martini.intellij.plugin.steps.AbstractStepDefinition;

/**
 * @author yole
 */
public interface GherkinStep extends GherkinPsiElement, GherkinSuppressionHolder, PomTarget, PsiNamedElement {
  GherkinStep[] EMPTY_ARRAY = new GherkinStep[0];
  /**
   * Message to display if step can't be renamed. (to be used as result of {@link #isRenameAllowed(String)} with null argument)
   *
   * @see #isRenameAllowed(String)
   */
  String RENAME_DISABLED_MESSAGE = MartiniBundle.message("martini.refactor.rename.disabled");

  /**
   * Message to display if step can't be renamed due to bad symbols. (to be used as result of {@link #isRenameAllowed(String)} with name argument)
   *
   * @see #isRenameAllowed(String)
   */
  String RENAME_BAD_SYMBOLS_MESSAGE = MartiniBundle.message("martini.refactor.rename.bad_symbols");

  ASTNode getKeyword();

  String getStepName();

  @Nullable
  GherkinTable getTable();

  @Nullable
  GherkinPystring getPystring();

  GherkinStepsHolder getStepHolder();

  /**
   * @return List with not empty unique possible substitutions names
   */
  List<String> getParamsSubstitutions();

  @Nullable
  String getSubstitutedName();

  /**
   * @return all step definitions (may be heavy). Works just like {@link guru.qas.martini.intellij.plugin.steps.reference.MartiniStepReference#resolveToDefinition()}
   * @see guru.qas.martini.intellij.plugin.steps.reference.MartiniStepReference#resolveToDefinition()
   */
  @NotNull
  Collection<AbstractStepDefinition> findDefinitions();


  /**
   * Checks if step can be renamed (actually, all definitions are asked).
   * See {@link guru.qas.martini.intellij.plugin.steps.AbstractStepDefinition#supportsRename(String)}.
   * Show {@link #RENAME_DISABLED_MESSAGE} or {@link #RENAME_BAD_SYMBOLS_MESSAGE}
   *
   * @param newName new name (to check if renaming to it is supported) or null to check if step could be renamed at all.
   *                Steps with out of defintiions can't be renamed as well.
   * @return true it could be
   * @see #RENAME_BAD_SYMBOLS_MESSAGE
   * @see #RENAME_DISABLED_MESSAGE
   */
  boolean isRenameAllowed(@Nullable String newName);
}
