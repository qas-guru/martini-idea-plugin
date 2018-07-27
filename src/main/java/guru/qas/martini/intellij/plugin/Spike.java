/*
Copyright 2018 Penny Rohr Curich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package guru.qas.martini.intellij.plugin;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchRequestCollector;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.intellij.psi.PsiModifier.PUBLIC;

/*
intellij-plugins/cucumber-java/src/org/jetbrains/plugins/cucumber/java/steps/search/CucumberJavaMethodUsageSearcher.java
at
https://github.com/JetBrains/intellij-plugins/blob/master/cucumber-java/src/org/jetbrains/plugins/cucumber/java/steps/search/CucumberJavaMethodUsageSearcher.java
 */
public class Spike extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters> {

	private static final Set<String> MARTINI_STEP_ANNOTATIONS = ImmutableSet.of(
		"guru.qas.martini.annotation.Given",
		"guru.qas.martini.annotation.And",
		"guru.qas.martini.annotation.When",
		"guru.qas.martini.annotation.Then"
	);

	public Spike() {
		super(true);
	}

	@Override
	public void processQuery(
		@NotNull MethodReferencesSearch.SearchParameters parameters,
		@NotNull Processor<PsiReference> consumer
	) {
		checkNotNull(parameters, "null MethodReferencesSearch.SearchParameters");
		checkNotNull(consumer, "null Processor");

		SearchScope scope = parameters.getEffectiveSearchScope();
		if (GlobalSearchScope.class.isInstance(scope)) {
			GlobalSearchScope globalSearchScope = GlobalSearchScope.class.cast(scope);
			processQuery(parameters, consumer, globalSearchScope);
		}
	}

	private void processQuery(
		MethodReferencesSearch.SearchParameters parameters,
		Processor<PsiReference> consumer,
		GlobalSearchScope scope
	) {
		PsiMethod method = parameters.getMethod();
		PsiAnnotation annotation = method.hasModifierProperty(PUBLIC) ? getAnnotation(method).orElse(null) : null;
		String regex = null == annotation ? null : getPatternFromStepDefinition(annotation).orElse(null);
		String word = null == regex ? null : RegEx.getTheBiggestWordToSearchByIndex(regex);
		if (null == word || word.isEmpty()) {
			return;
		}

		GlobalSearchScope restrictedScope = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, FeatureFileType.getInstance());
		SearchRequestCollector optimizer = parameters.getOptimizer();
		ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(method, restrictedScope, false, optimizer);
		ReferencesSearch.search(searchParameters).forEach(consumer);
	}

	private static Optional<PsiAnnotation> getAnnotation(PsiMethod method) {
		PsiModifierList modifiers = method.getModifierList();
		return Lists.newArrayList(modifiers.getAnnotations()).stream()
			.filter(Objects::nonNull)
			.filter(Spike::isStepAnnotation)
			.findFirst();
	}

	private static boolean isStepAnnotation(PsiAnnotation annotation) {
		String name = getAnnotationName(annotation).orElse(null);
		return MARTINI_STEP_ANNOTATIONS.contains(name);
	}

	private static Optional<String> getAnnotationName(@NotNull PsiAnnotation annotation) {
		Ref<String> annotationNameReference = new Ref<>();
		Application application = ApplicationManager.getApplication();
		application.runReadAction(() -> {
				String qualifiedName = annotation.getQualifiedName();
				annotationNameReference.set(qualifiedName);
			}
		);
		String annotationName = annotationNameReference.get();
		return Optional.ofNullable(annotationName);
	}

	private static Optional<String> getPatternFromStepDefinition(PsiAnnotation stepAnnotation) {
		PsiAnnotationParameterList parameterList = stepAnnotation.getParameterList();
		PsiNameValuePair[] attributes = parameterList.getAttributes();
		PsiAnnotationMemberValue value = attributes.length > 0 ? attributes[0].getValue() : null;
		String pattern = null == value ? null : getPattern(value);
		return Optional.ofNullable(pattern);
	}

	private static String getPattern(PsiAnnotationMemberValue value) {
		String patternContainer = value.getText();
		String substring = patternContainer.substring(1, patternContainer.length() - 1);
		return substring.replace("\\\\", "\\");
	}
}