package guru.qas.martini.intellij.plugin.steps.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;

import guru.qas.martini.intellij.plugin.gherkin.psi.impl.GherkinStepImpl;

public class MartiniReferenceContributor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
    PsiElementPattern.Capture<GherkinStepImpl> pattern = PlatformPatterns.psiElement(GherkinStepImpl.class);
    MartiniStepsReferenceProvider provider = new MartiniStepsReferenceProvider();
    registrar.registerReferenceProvider(pattern, provider);
  }
}
