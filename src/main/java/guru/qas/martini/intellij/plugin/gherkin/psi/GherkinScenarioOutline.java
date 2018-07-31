package guru.qas.martini.intellij.plugin.gherkin.psi;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.psi.GherkinExamplesBlock;

/**
 * @author yole
 */
public interface GherkinScenarioOutline extends GherkinStepsHolder {
  @NotNull
  List<GherkinExamplesBlock> getExamplesBlocks();

  @Nullable
  Map<String, String> getOutlineTableMap();
}
