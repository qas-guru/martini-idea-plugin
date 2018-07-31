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

package guru.qas.martini.intellij.plugin.gherkin.psi;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.cucumber.psi.GherkinLanguage;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;

public class GherkinFileType extends LanguageFileType {

	public static final Icon ICON = IconLoader.getIcon("/guru/qas/martini/intellij/plugin/icons/jar-gray.png");
	private static final GherkinFileType INSTANCE = new GherkinFileType();

	private GherkinFileType() {
		super(GherkinLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return "Gherkin";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Gherkin feature file";
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return "feature";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return ICON;
	}

	public static GherkinFileType getInstance() {
		return INSTANCE;
	}
}
