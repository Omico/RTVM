/*
 * Copyright 2023 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.omico.rtvm.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.writeTo
import me.omico.elucidator.TypedParameter
import me.omico.elucidator.addClass
import me.omico.elucidator.addInitializerBlock
import me.omico.elucidator.addModifiers
import me.omico.elucidator.addProperty
import me.omico.elucidator.addSuperinterface
import me.omico.elucidator.initializer
import me.omico.elucidator.ktFile
import me.omico.elucidator.modifier
import me.omico.elucidator.superclass

internal fun generateStatefulViewModel(
    initialFunctionAnnotations: List<KSFunctionDeclaration>,
    classDeclaration: KSClassDeclaration,
    codeGenerator: CodeGenerator,
) {
    val statefulViewModelInfo = createRtvmGenerationInfo(classDeclaration)
    val file = ktFile(statefulViewModelInfo.packageName, statefulViewModelInfo.generatedViewModelFileName) {
        addClass(statefulViewModelInfo.generatedViewModelName) {
            statefulViewModelInfo.generatedViewModelParameters.forEach { parameter ->
                addProperty(name = parameter.name, type = parameter.type) {
                    modifier(KModifier.INTERNAL)
                    initializer(parameter.defaultValue)
                }
            }
            superclass(Jetpack.ClassNames.ViewModel)
            addSuperinterface(statefulViewModelInfo.generatedViewModelStatefulInterfaceClassName)
            initialFunctionAnnotations
                .filter { it.extensionReceiver?.toString() == statefulViewModelInfo.generatedViewModelName }
                .joinToString(separator = "\n", postfix = "\n") { "${it.simpleName.asString()}()" }
                .let { CodeBlock.of(it) }
                .let(::addInitializerBlock)
            addProperty("state", statefulViewModelInfo.generatedViewModelStateClassName) {
                addModifiers(KModifier.OVERRIDE)
                initializer(
                    "%M(${statefulViewModelInfo.generatedViewModelParameters.joinToString(transform = TypedParameter::name)}, %L)" +
                        ".%M(%M, %T.WhileSubscribed(), Internal${statefulViewModelInfo.viewStateName})",
                    KotlinxCoroutines.MemberNames.combineMemberName,
                    statefulViewModelInfo.viewStateClassName.constructorReference(),
                    KotlinxCoroutines.MemberNames.stateInMemberName,
                    Jetpack.MemberNames.viewModelScopeMemberName,
                    KotlinxCoroutines.ClassNames.SharingStarted,
                )
            }
        }
        addProperty("Internal${statefulViewModelInfo.viewStateName}", statefulViewModelInfo.viewStateClassName, KModifier.PRIVATE) {
            initializer("${statefulViewModelInfo.viewStateName}()")
        }
    }
    file.writeTo(codeGenerator, aggregating = true)
}
