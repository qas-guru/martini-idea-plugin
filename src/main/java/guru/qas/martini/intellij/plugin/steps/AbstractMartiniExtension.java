package guru.qas.martini.intellij.plugin.steps;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import guru.qas.martini.intellij.plugin.MartiniJvmExtensionPoint;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStep;

public abstract class AbstractMartiniExtension implements MartiniJvmExtensionPoint {
  @Override
  public List<PsiElement> resolveStep(@NotNull final PsiElement element) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(element);
    if (module == null) {
      return Collections.emptyList();
    }

    final String stepVariant = getStepVariant(element);
    if (stepVariant == null) {
      return Collections.emptyList();
    }

    final List<AbstractStepDefinition> stepDefinitions = loadStepsFor(element.getContainingFile(), module);
    final List<PsiElement> result = new ArrayList<>();

    for (final AbstractStepDefinition stepDefinition : stepDefinitions) {
      if (stepDefinition.matches(stepVariant) && stepDefinition.supportsStep(element)) {
        result.add(stepDefinition.getElement());
      }
    }

    return result;
  }

  @Nullable
  protected String getStepVariant(@NotNull final PsiElement element) {
    if (element instanceof GherkinStep) {
      return ((GherkinStep)element).getSubstitutedName();
    }
    return null;
  }

  @Override
  public void flush(@NotNull final Project project) {
  }

  @Override
  public void reset(@NotNull final Project project) {
  }

  @Override
  public Object getDataObject(@NotNull Project project) {
    return null;
  }
}
