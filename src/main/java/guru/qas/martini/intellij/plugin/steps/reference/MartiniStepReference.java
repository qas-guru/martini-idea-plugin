package guru.qas.martini.intellij.plugin.steps.reference;

import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import guru.qas.martini.intellij.plugin.MartiniJvmExtensionPoint;
import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinStepImpl;
import guru.qas.martini.intellij.plugin.steps.AbstractStepDefinition;
import guru.qas.martini.intellij.plugin.steps.MartiniStepsIndex;

/**
 * @author yole
 */
public class MartiniStepReference implements PsiPolyVariantReference {

  private final PsiElement myStep;
  private final TextRange myRange;

  public MartiniStepReference(PsiElement step, TextRange range) {
    myStep = step;
    myRange = range;
  }

  @NotNull
  public PsiElement getElement() {
    return myStep;
  }

  @NotNull
  public TextRange getRangeInElement() {
    return myRange;
  }

  public PsiElement resolve() {
    final ResolveResult[] result = multiResolve(true);
    return result.length == 1 ? result[0].getElement() : null;
  }

  @NotNull
  public String getCanonicalText() {
    return myStep.getText();
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return myStep;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return myStep;
  }

  public boolean isReferenceTo(PsiElement element) {
    ResolveResult[] resolvedResults = multiResolve(false);
    for (ResolveResult rr : resolvedResults) {
      if (getElement().getManager().areElementsEquivalent(rr.getElement(), element)) {
        return true;
      }
    }
    return false;
  }

  @NotNull
  public Object[] getVariants() {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  public boolean isSoft() {
    return false;
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    final List<ResolveResult> result = new ArrayList<>();
    final List<PsiElement> resolvedElements = new ArrayList<>();

    final MartiniJvmExtensionPoint[] extensionList = Extensions.getExtensions(MartiniJvmExtensionPoint.EP_NAME);
    for (MartiniJvmExtensionPoint e : extensionList) {
      final List<PsiElement> extensionResult = e.resolveStep(myStep);
      for (final PsiElement element : extensionResult) {
        if (element != null && !resolvedElements.contains(element)) {
          resolvedElements.add(element);
          result.add(new ResolveResult() {
            @Override
            public PsiElement getElement() {
              return element;
            }

            @Override
            public boolean isValidResult() {
              return true;
            }
          });
        }
      }
    }

    return result.toArray(ResolveResult.EMPTY_ARRAY);
  }

  /**
   * @return first definition (if any) or null if no definition found
   * @see #resolveToDefinitions()
   */
  @Nullable
  public AbstractStepDefinition resolveToDefinition() {
    final Collection<AbstractStepDefinition> definitions = resolveToDefinitions();
    return (definitions.isEmpty() ? null : definitions.iterator().next());
  }

  /**
   * @return step definitions
   * @see #resolveToDefinition()
   */
  @NotNull
  public Collection<AbstractStepDefinition> resolveToDefinitions() {
    final MartiniStepsIndex index = MartiniStepsIndex.getInstance(myStep.getProject());
    return index.findStepDefinitions(myStep.getContainingFile(), ((GherkinStepImpl)myStep));
  }
}
