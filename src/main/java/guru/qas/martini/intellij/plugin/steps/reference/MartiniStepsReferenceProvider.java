package guru.qas.martini.intellij.plugin.steps.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinStepImpl;

import static com.intellij.psi.tree.TokenSet.WHITE_SPACE;
import static org.jetbrains.plugins.cucumber.psi.GherkinElementTypes.STEP_PARAMETER;
import static org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes.*;

public class MartiniStepsReferenceProvider extends PsiReferenceProvider {
  private static final TokenSet TEXT_AND_PARAM_SET = TokenSet.create(TEXT, STEP_PARAMETER_TEXT, STEP_PARAMETER_BRACE, STEP_PARAMETER);
  private static final TokenSet TEXT_PARAM_AND_WHITE_SPACE_SET = TokenSet.orSet(TEXT_AND_PARAM_SET, WHITE_SPACE);

  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (element instanceof GherkinStepImpl) {
      ASTNode textNode = element.getNode().findChildByType(TEXT_AND_PARAM_SET);
      if (textNode != null) {
        int start = textNode.getTextRange().getStartOffset();
        int end = textNode.getTextRange().getEndOffset();
        int endBeforeSpace = end;
        textNode = textNode.getTreeNext();
        while (textNode != null && TEXT_PARAM_AND_WHITE_SPACE_SET.contains(textNode.getElementType())) {
          endBeforeSpace = textNode.getElementType() == TokenType.WHITE_SPACE ? end : textNode.getTextRange().getEndOffset();
          end = textNode.getTextRange().getEndOffset();
          textNode = textNode.getTreeNext();
        }
        TextRange textRange = new TextRange(start, endBeforeSpace);
        MartiniStepReference reference =  new MartiniStepReference(element, textRange.shiftRight(-element.getTextOffset()));
        return new PsiReference[] {reference};
      }
    }
    return PsiReference.EMPTY_ARRAY;
  }
}
