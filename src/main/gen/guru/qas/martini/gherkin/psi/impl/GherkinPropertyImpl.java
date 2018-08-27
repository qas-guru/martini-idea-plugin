// This is a generated file. Not intended for manual editing.
package guru.qas.martini.gherkin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static guru.qas.martini.gherkin.psi.GherkinTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import guru.qas.martini.gherkin.psi.*;

public class GherkinPropertyImpl extends ASTWrapperPsiElement implements GherkinProperty {

  public GherkinPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GherkinVisitor visitor) {
    visitor.visitProperty(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GherkinVisitor) accept((GherkinVisitor)visitor);
    else super.accept(visitor);
  }

}
