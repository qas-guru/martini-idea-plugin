package guru.qas.martini.intellij.plugin.gherkin.psi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.psi.impl.GherkinFileImpl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import guru.qas.martini.intellij.plugin.MartiniUtil;
import guru.qas.martini.intellij.plugin.OutlineStepSubstitution;
import guru.qas.martini.intellij.plugin.steps.AbstractStepDefinition;

/**
 * @author Roman.Chernyatchik
 * @date May 21, 2009
 */
public class GherkinPsiUtil {
  private GherkinPsiUtil() {
  }

  @Nullable
  public static GherkinFileImpl getGherkinFile(@NotNull final PsiElement element) {
    if (!element.isValid()) {
      return null;
    }
    final PsiFile containingFile = element.getContainingFile();
    return containingFile instanceof GherkinFileImpl ? (GherkinFileImpl)containingFile : null;
  }

  @Nullable
  public static List<TextRange> buildParameterRanges(@NotNull GherkinStep step,
                                                     @NotNull AbstractStepDefinition definition,
                                                     final int shiftOffset) {

    OutlineStepSubstitution substitution = convertOutlineStepName(step);

    final List<TextRange> parameterRanges = new ArrayList<>();
    final Pattern pattern = definition.getPattern();
    if (pattern == null) return null;

    Matcher matcher = pattern.matcher(substitution.getSubstitution());
    if (matcher.find()) {
      final int groupCount = matcher.groupCount();
      for (int i = 0; i < groupCount; i++) {
        final int start = matcher.start(i + 1);
        final int end = matcher.end(i + 1);
        if (start >= 0 && end >= 0) {
          int rangeStart = substitution.getOffsetInOutlineStep(start);
          int rangeEnd = substitution.getOffsetInOutlineStep(end);
          parameterRanges.add(new TextRange(rangeStart, rangeEnd).shiftRight(shiftOffset));
        }
      }
    }

    int k = step.getText().indexOf(step.getStepName());
    k += step.getStepName().length();
    if (k < step.getText().length() - 1) {
      String text = step.getText().substring(k + 1);
      boolean inParam = false;
      int paramStart = 0;
      int i = 0;
      while (i < text.length()) {
        if (text.charAt(i) == '<') {
          paramStart = i;
          inParam = true;
        }

        if (text.charAt(i) == '>' && inParam) {
          parameterRanges.add(new TextRange(paramStart, i + 1).shiftRight(shiftOffset + step.getStepName().length() + 1));
          inParam = false;
        }
        i++;
      }
    }

    return parameterRanges;
  }

  public static OutlineStepSubstitution convertOutlineStepName(@NotNull GherkinStep step) {
    if (!(step.getStepHolder() instanceof GherkinScenarioOutline)) {
      return new OutlineStepSubstitution(step.getName());
    }

    Map<String, String> outlineTableMap = ((GherkinScenarioOutline) step.getStepHolder()).getOutlineTableMap();
    return MartiniUtil.substituteTableReferences(step.getStepName(), outlineTableMap);
  }
}
