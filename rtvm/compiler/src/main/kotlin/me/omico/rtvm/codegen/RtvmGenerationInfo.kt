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
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import me.omico.elucidator.TypedParameter
import me.omico.elucidator.defaultValue
import me.omico.elucidator.with
import me.omico.rtvm.RtvmState
import me.omico.rtvm.utility.KotlinxCoroutines
import me.omico.rtvm.utility.capitalize
import me.omico.rtvm.utility.extractDefaultValue

data class RtvmGenerationInfo(
    val ksFile: KSFile,
    val packageName: String,
    val viewStateClassName: ClassName,
    val scopeName: String,
    val generatedViewStateDispatcherProperties: List<TypedParameter>,
) {
    val generatedViewStateDispatcherName: String = "${viewStateClassName.simpleName}Dispatcher"
    val generatedViewStateDispatcherFileName: String = "$generatedViewStateDispatcherName.generated"
    val generatedViewStateDispatcherFlowClassName = KotlinxCoroutines.ClassNames.Flow.parameterizedBy(viewStateClassName)
    val generatedViewStateDispatcherParametersClassName = ClassName(packageName, "$generatedViewStateDispatcherName.Parameters")
}

@OptIn(KspExperimental::class)
fun createRtvmGenerationInfo(viewStateClassDeclaration: KSClassDeclaration): RtvmGenerationInfo =
    RtvmGenerationInfo(
        ksFile = viewStateClassDeclaration.containingFile!!,
        packageName = viewStateClassDeclaration.packageName.asString(),
        viewStateClassName = viewStateClassDeclaration.toClassName(),
        scopeName = viewStateClassDeclaration.getAnnotationsByType(RtvmState::class).first().scope.capitalize(),
        generatedViewStateDispatcherProperties = collectViewStateDispatcherProperties(viewStateClassDeclaration),
    )

private fun collectViewStateDispatcherProperties(viewStateClassDeclaration: KSClassDeclaration): List<TypedParameter> =
    requireNotNull(viewStateClassDeclaration.primaryConstructor?.parameters) {
        "\n" +
            "\tThe class with @StatefulViewModel annotation must have a primary constructor.\n" +
            "\tBut [${viewStateClassDeclaration.toClassName()}] does not have one.\n"
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
