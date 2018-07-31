package guru.qas.martini.intellij.plugin.steps.search;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinFileType;

public class MartiniStepSearchUtil {

	public static SearchScope restrictScopeToGherkinFiles(final Computable<SearchScope> originalScopeComputation) {
		return ReadAction.compute(() -> {
			SearchScope originalScope = originalScopeComputation.compute();
			return GlobalSearchScope.class.isInstance(originalScope) ?
				getRestricted(GlobalSearchScope.class.cast(originalScope)) : originalScope;
		});
	}

	private static SearchScope getRestricted(GlobalSearchScope scope) {
		GherkinFileType gherkinFileType = GherkinFileType.getInstance();
		return GlobalSearchScope.getScopeRestrictedByFileTypes(scope, gherkinFileType);
	}
}
