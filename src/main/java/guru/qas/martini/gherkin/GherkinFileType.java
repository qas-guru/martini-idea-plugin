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

package guru.qas.martini.gherkin;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.fileTypes.LanguageFileType;

public class GherkinFileType extends LanguageFileType {

	private static final GherkinFileType INSTANCE = new GherkinFileType();

	public GherkinFileType() {
		super(GherkinLanguage.getInstance());
	}

	@NotNull
	@Override
	public String getName() {
		return "Gherkin file";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Gherkin language file";
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return "feature";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return GherkinIcons.getFeatureIcon();
	}

	public static GherkinFileType getInstance() {
		return INSTANCE;
	}
}