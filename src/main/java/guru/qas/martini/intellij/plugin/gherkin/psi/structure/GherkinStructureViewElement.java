package guru.qas.martini.intellij.plugin.gherkin.psi.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.cucumber.psi.*;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinFeature;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStep;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStepsHolder;
import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinFeatureHeaderImpl;
import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinTableImpl;
import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinTagImpl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yole
 */
public class GherkinStructureViewElement extends PsiTreeElementBase<PsiElement> {
  protected GherkinStructureViewElement(PsiElement psiElement) {
    super(psiElement);
  }

  @NotNull
  public Collection<StructureViewTreeElement> getChildrenBase() {
    List<StructureViewTreeElement> result = new ArrayList<>();
    for (PsiElement element : getElement().getChildren()) {
      if (element instanceof GherkinPsiElement &&
          !(element instanceof GherkinFeatureHeaderImpl) &&
          !(element instanceof GherkinTableImpl) &&
          !(element instanceof GherkinTagImpl) &&
          !(element instanceof GherkinPystring)) {
        result.add(new GherkinStructureViewElement(element));
      }
    }
    return result;
  }

  @Override
  public Icon getIcon(boolean open) {
    final PsiElement element = getElement();
    if (element instanceof GherkinFeature
        || element instanceof GherkinStepsHolder) {
      return open ? icons.CucumberIcons.Steps_group_opened : icons.CucumberIcons.Steps_group_closed;
    }
    if (element instanceof GherkinStep) {
      return icons.CucumberIcons.Cucumber;
    }
    return null;
  }


  public String getPresentableText() {
    return ((NavigationItem) getElement()).getPresentation().getPresentableText();
  }
}
