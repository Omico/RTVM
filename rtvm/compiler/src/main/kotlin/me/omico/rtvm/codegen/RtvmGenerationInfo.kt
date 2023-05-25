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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import me.omico.elucidator.TypedParameter
import me.omico.elucidator.defaultValue
import me.omico.elucidator.with
import me.omico.rtvm.RtvmState
import me.omico.rtvm.Stateful
import me.omico.rtvm.utility.capitalize
import me.omico.rtvm.utility.extractDefaultValue

data class RtvmGenerationInfo(
    val packageName: String,
    val viewStateClassName: ClassName,
    val generatedViewModelName: String,
    val generatedViewModelParameters: List<TypedParameter>,
) {
    val viewStateName = viewStateClassName.simpleName
    val generatedViewModelStatefulInterfaceClassName = Stateful::class.asClassName().parameterizedBy(viewStateClassName)
    val generatedViewModelFileName: String = "$generatedViewModelName.generated"
    val generatedViewModelStateClassName = KotlinxCoroutines.ClassNames.StateFlow.parameterizedBy(viewStateClassName)
}

@OptIn(KspExperimental::class)
fun createRtvmGenerationInfo(viewStateClassDeclaration: KSClassDeclaration): RtvmGenerationInfo =
    RtvmGenerationInfo(
        packageName = viewStateClassDeclaration.packageName.asString(),
        viewStateClassName = viewStateClassDeclaration.toClassName(),
        generatedViewModelName = viewStateClassDeclaration.getAnnotationsByType(RtvmState::class).first().scope.capitalize() + "ViewModel",
        generatedViewModelParameters = collectRtvmViewModelParameters(viewStateClassDeclaration),
    )

private fun collectRtvmViewModelParameters(classDeclaration: KSClassDeclaration): List<TypedParameter> =
    requireNotNull(classDeclaration.primaryConstructor?.parameters) {
        "\n" +
            "\tThe class with @StatefulViewModel annotation must have a primary constructor.\n" +
            "\tBut [${classDeclaration.toClassName()}] does not have one.\n"
    }.map { parameter ->
        require(parameter.isVal) { "\n" + "\tThe parameters in the class annotated with @StatefulViewModel must all be val.\n" }
        require(parameter.hasDefault) { "\n" + "\tThe class with @StatefulViewModel annotation must have a default value for it's parameter.\n" }
        val name = parameter.name!!.asString()
        val ksType = parameter.type.resolve()
        val type = KotlinxCoroutines.ClassNames.MutableStateFlow
            .parameterizedBy(ksType.toClassName().copy(nullable = ksType.isMarkedNullable))
        val defaultValue = requireNotNull(parameter.location as? FileLocation).extractDefaultValue(name)
        name with type defaultValue "MutableStateFlow($defaultValue)"
    }
