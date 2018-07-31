package guru.qas.martini.intellij.plugin.gherkin.psi.refactoring.rename;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.PsiElementRenameHandler;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.util.CommonRefactoringUtil;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStep;

public class GherkinStepRenameHandler extends PsiElementRenameHandler {
  @Override
  public boolean isAvailableOnDataContext(@NotNull DataContext dataContext) {
    PsiElement element = getGherkinStep(dataContext);
    return element != null;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
    final GherkinStep step = getGherkinStep(dataContext);
    if (step == null) {
      return;
    }

    if (!step.isRenameAllowed(null)) {
      CommonRefactoringUtil.showErrorHint(project, editor, GherkinStep.RENAME_DISABLED_MESSAGE, "", null);
      return;
    }


    final MartiniStepRenameDialog dialog = new MartiniStepRenameDialog(project, step, null, editor);
    Disposer.register(project, dialog.getDisposable());
    RenameDialog.showRenameDialog(dataContext, dialog);
  }


  @Override
  public boolean isRenaming(@NotNull DataContext dataContext) {
    PsiElement element = getGherkinStep(dataContext);
    return element != null;
  }

  @Nullable
  public GherkinStep getGherkinStep(@Nullable final DataContext context) {
    PsiElement element = null;
    if (context == null) return null;
    final Editor editor = CommonDataKeys.EDITOR.getData(context);
    if (editor != null) {
      final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(context);
      if (psiFile != null) {
        element = psiFile.findElementAt(editor.getCaretModel().getOffset());
      }
    }
    if (element == null) {
      element = CommonDataKeys.PSI_ELEMENT.getData(context);
    }
    return element instanceof GherkinStep ? (GherkinStep)element : PsiTreeUtil.getParentOfType(element, GherkinStep.class);
  }
}
