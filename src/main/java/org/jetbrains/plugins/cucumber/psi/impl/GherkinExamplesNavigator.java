package org.jetbrains.plugins.cucumber.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinExamplesBlockImpl;
import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinTableImpl;

/**
 * @author Roman.Chernyatchik
 * @date Sep 11, 2009
 */
public class GherkinExamplesNavigator {
  @Nullable
  public static GherkinExamplesBlockImpl getExamplesByTable(final GherkinTableImpl table) {
    final PsiElement element = table.getParent();
    return element instanceof GherkinExamplesBlockImpl ? (GherkinExamplesBlockImpl)element : null;
  }
}
