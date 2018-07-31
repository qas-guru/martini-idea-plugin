package guru.qas.martini.intellij.plugin.gherkin.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.cucumber.psi.GherkinPsiElement;
import org.jetbrains.plugins.cucumber.psi.GherkinSuppressionHolder;
import org.jetbrains.plugins.cucumber.psi.GherkinTag;

/**
 * @author Roman.Chernyatchik
 * @date Aug 22, 2009
 */
public interface GherkinStepsHolder extends GherkinPsiElement, GherkinSuppressionHolder {
  GherkinStepsHolder[] EMPTY_ARRAY = new GherkinStepsHolder[0];

  String getScenarioName();

  @NotNull
  GherkinStep[] getSteps();

  GherkinTag[] getTags();
}
