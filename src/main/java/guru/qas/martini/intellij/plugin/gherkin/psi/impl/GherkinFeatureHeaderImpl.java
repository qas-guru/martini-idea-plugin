package guru.qas.martini.intellij.plugin.gherkin.psi.impl;

import org.jetbrains.annotations.NotNull;

import com.intellij.lang.ASTNode;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinElementVisitor;

/**
 * @author yole
 */
public class GherkinFeatureHeaderImpl extends GherkinPsiElementBase {
  public GherkinFeatureHeaderImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitFeatureHeader(this);
  }

  @Override
  public String toString() {
    return "GherkinFeatureHeader";
  }
}
