// This is a generated file. Not intended for manual editing.
package guru.qas.martini.gherkin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import guru.qas.martini.gherkin.psi.impl.*;

public interface GherkinTypes {

  IElementType PROPERTY = new GherkinElementType("PROPERTY");

  IElementType COMMENT = new GherkinTokenType("COMMENT");
  IElementType CRLF = new GherkinTokenType("CRLF");
  IElementType KEY = new GherkinTokenType("KEY");
  IElementType SEPARATOR = new GherkinTokenType("SEPARATOR");
  IElementType VALUE = new GherkinTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == PROPERTY) {
        return new GherkinPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
