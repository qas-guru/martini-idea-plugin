package org.jetbrains.plugins.cucumber.psi.impl;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.psi.GherkinTableRow;
import com.intellij.psi.PsiElement;

import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinTableImpl;

/**
 * @author Roman.Chernyatchik
 * @date Sep 10, 2009
 */
public class GherkinTableNavigator {
  private GherkinTableNavigator() {
  }

  @Nullable
  public static GherkinTableImpl getTableByRow(final GherkinTableRow row) {
    final PsiElement element = row.getParent();
    return element instanceof GherkinTableImpl ? (GherkinTableImpl)element : null;
  }
}
