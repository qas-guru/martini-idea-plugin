package guru.qas.martini.intellij.plugin.gherkin.psi;

import org.jetbrains.plugins.cucumber.psi.GherkinPsiElement;
import org.jetbrains.plugins.cucumber.psi.GherkinSuppressionHolder;

/**
 * @author yole
 */
public interface GherkinFeature extends GherkinPsiElement, GherkinSuppressionHolder {
  String getFeatureName();
  GherkinStepsHolder[] getScenarios();
}
