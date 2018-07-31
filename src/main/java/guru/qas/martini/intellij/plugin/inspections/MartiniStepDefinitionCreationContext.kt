package org.jetbrains.plugins.cucumber.inspections

import com.intellij.psi.PsiFile
import guru.qas.martini.intellij.plugin.BDDFrameworkType

data class MartiniStepDefinitionCreationContext(var psiFile: PsiFile? = null, var frameworkType: BDDFrameworkType? = null)