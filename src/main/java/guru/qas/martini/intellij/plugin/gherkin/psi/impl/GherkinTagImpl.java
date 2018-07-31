package guru.qas.martini.intellij.plugin.gherkin.psi.impl;

import org.jetbrains.plugins.cucumber.psi.GherkinTag;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinElementVisitor;

/**
 * @author yole
 */
public class GherkinTagImpl extends GherkinPsiElementBase implements GherkinTag {
  public GherkinTagImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitTag(this);
  }

  @Override
  public String getName() {
    return getText();
  }

  @Override
  public String toString() {
    return "GherkinTag:" + getText();
  }
}
