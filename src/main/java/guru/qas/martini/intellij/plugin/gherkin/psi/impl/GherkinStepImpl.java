package guru.qas.martini.intellij.plugin.gherkin.psi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.psi.GherkinElementTypes;
import org.jetbrains.plugins.cucumber.psi.GherkinPystring;
import org.jetbrains.plugins.cucumber.psi.GherkinTable;
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiCheckedRenameElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;

import guru.qas.martini.intellij.plugin.MartiniUtil;
import guru.qas.martini.intellij.plugin.OutlineStepSubstitution;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinElementVisitor;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinScenarioOutline;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStep;
import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinStepsHolder;
import guru.qas.martini.intellij.plugin.gherkin.psi.refactoring.GherkinChangeUtil;
import guru.qas.martini.intellij.plugin.steps.AbstractStepDefinition;
import guru.qas.martini.intellij.plugin.steps.reference.MartiniStepReference;

/**
 * @author yole
 */
public class GherkinStepImpl extends GherkinPsiElementBase implements GherkinStep, PsiCheckedRenameElement {

  private static final TokenSet TEXT_FILTER = TokenSet
    .create(GherkinTokenTypes.TEXT, GherkinElementTypes.STEP_PARAMETER, TokenType.WHITE_SPACE, GherkinTokenTypes.STEP_PARAMETER_TEXT,
            GherkinTokenTypes.STEP_PARAMETER_BRACE);

  private static final Pattern PARAMETER_SUBSTITUTION_PATTERN = Pattern.compile("<([^>\n\r]+)>");
  private final Object LOCK = new Object();

  private List<String> mySubstitutions;

  public GherkinStepImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "GherkinStep:" + getStepName();
  }

  @Nullable
  public ASTNode getKeyword() {
    return getNode().findChildByType(GherkinTokenTypes.STEP_KEYWORD);
  }

  @Nullable
  public String getStepName() {
    return getElementText();
  }

  @Override
  @NotNull
  protected String getElementText() {
    final ASTNode node = getNode();
    final ASTNode[] children = node.getChildren(TEXT_FILTER);
    return StringUtil.join(children, astNode -> astNode.getText(), "").trim();
  }

  @Nullable
  public GherkinPystring getPystring() {
    return PsiTreeUtil.findChildOfType(this, GherkinPystring.class);
  }

  @Nullable
  public GherkinTable getTable() {
    final ASTNode tableNode = getNode().findChildByType(GherkinElementTypes.TABLE);
    return tableNode == null ? null : (GherkinTable)tableNode.getPsi();
  }

  @Override
  protected String getPresentableText() {
    final ASTNode keywordNode = getKeyword();
    final String prefix = keywordNode != null ? keywordNode.getText() + ": " : "Step: ";
    return prefix + getStepName();
  }

  @NotNull
  @Override
  public PsiReference[] getReferences() {
    return ReferenceProvidersRegistry.getReferencesFromProviders(this);
  }

  protected void acceptGherkin(GherkinElementVisitor gherkinElementVisitor) {
    gherkinElementVisitor.visitStep(this);
  }

  @NotNull
  public List<String> getParamsSubstitutions() {
    synchronized (LOCK) {
      if (mySubstitutions == null) {
        final ArrayList<String> substitutions = new ArrayList<>();


        // step name
        final String text = getStepName();
        if (StringUtil.isEmpty(text)) {
          return Collections.emptyList();
        }
        addSubstitutionFromText(text, substitutions);

        // pystring
        final GherkinPystring pystring = getPystring();
        String pystringText = pystring != null ? pystring.getText() : null;
        if (!StringUtil.isEmpty(pystringText)) {
          addSubstitutionFromText(pystringText, substitutions);
        }

        // table
        final GherkinTable table = getTable();
        final String tableText = table == null ? null : table.getText();
        if (tableText != null) {
          addSubstitutionFromText(tableText, substitutions);
        }

        mySubstitutions = substitutions.isEmpty() ? Collections.emptyList() : substitutions;
      }
      return mySubstitutions;
    }
  }

  private static void addSubstitutionFromText(String text, ArrayList<String> substitutions) {
    final Matcher matcher = PARAMETER_SUBSTITUTION_PATTERN.matcher(text);
    boolean result = matcher.find();
    if (!result) {
      return;
    }

    do {
      final String substitution = matcher.group(1);
      if (!StringUtil.isEmpty(substitution) && !substitutions.contains(substitution)) {
        substitutions.add(substitution);
      }
      result = matcher.find();
    }
    while (result);
  }

  @Override
  public void subtreeChanged() {
    super.subtreeChanged();
    clearCaches();
  }

  @Nullable
  public GherkinStepsHolder getStepHolder() {
    final PsiElement parent = getParent();
    return parent != null ? (GherkinStepsHolder)parent : null;
  }

  private void clearCaches() {
    synchronized (LOCK) {
      mySubstitutions = null;
    }
  }

  @Nullable
  public String getSubstitutedName() {
    final GherkinStepsHolder holder = getStepHolder();
    if (!(holder instanceof GherkinScenarioOutline)) {
      return getStepName();
    }
    final GherkinScenarioOutline outline = (GherkinScenarioOutline)holder;
    OutlineStepSubstitution substitution = MartiniUtil.substituteTableReferences(getStepName(), outline.getOutlineTableMap());
    return substitution.getSubstitution();
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    GherkinStep newStep = GherkinChangeUtil.createStep(getKeyword().getText() + " " + name, getProject());
    replace(newStep);
    return newStep;
  }

  @Override
  public String getName() {
    final ASTNode keyword = getKeyword();
    final int keywordLength = keyword != null ? keyword.getTextLength() : 0;
    return getText().substring(keywordLength).trim();
  }


  @NotNull
  @Override
  public Collection<AbstractStepDefinition> findDefinitions() {
    final List<AbstractStepDefinition> result = new ArrayList<>();
    for (final PsiReference reference : getReferences()) {
      if (reference instanceof MartiniStepReference) {
        result.addAll(((MartiniStepReference)reference).resolveToDefinitions());
      }
    }
    return result;
  }


  @Override
  public boolean isRenameAllowed(@Nullable final String newName) {
    final Collection<AbstractStepDefinition> definitions = findDefinitions();
    if (definitions.isEmpty()) {
      return false; // No sense to rename step with out of definitions
    }
    for (final AbstractStepDefinition definition : definitions) {
      if (!definition.supportsRename(newName)) {
        return false; //At least one definition does not support renaming
      }
    }
    return true; // Nothing prevents us from renaming
  }

  @Override
  public void checkSetName(final String name) {
    if (!isRenameAllowed(name)) {
      throw new IncorrectOperationException(RENAME_BAD_SYMBOLS_MESSAGE);
    }
  }
}
