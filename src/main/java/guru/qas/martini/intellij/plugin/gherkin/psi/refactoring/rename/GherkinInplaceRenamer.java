package guru.qas.martini.intellij.plugin.gherkin.psi.refactoring.rename;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinScenarioOutline;

public class GherkinInplaceRenamer extends VariableInplaceRenamer {
  public GherkinInplaceRenamer(@NotNull PsiNamedElement elementToRename, Editor editor) {
    super(elementToRename, editor);
  }

  @Override
  public void finish(boolean success) {
    super.finish(success);

    if (success) {
      final PsiNamedElement newVariable = getVariable();
      if (newVariable != null) {
        final GherkinScenarioOutline scenario = PsiTreeUtil.getParentOfType(newVariable, GherkinScenarioOutline.class);

        if (scenario != null) {
          final CodeStyleManager csManager = CodeStyleManager.getInstance(newVariable.getProject());
          csManager.reformat(scenario);
        }
      }
    }
  }
}
