package guru.qas.martini.intellij.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.LocalTimeCounter;

import org.jetbrains.annotations.NotNull;

import guru.qas.martini.intellij.plugin.gherkin.psi.GherkinFileType;

public class MartiniElementFactory {

	public static PsiElement createTempPsiFile(@NotNull final Project project, @NotNull final String text) {

		GherkinFileType gherkinFileType = GherkinFileType.getInstance();
		long now = LocalTimeCounter.currentTime();
		PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
		String name = "temp." + gherkinFileType.getDefaultExtension();
		return fileFactory.createFileFromText(name, gherkinFileType, text, now, true);
	}
}
