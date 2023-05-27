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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.UNIT
import me.omico.elucidator.TypeScope
import me.omico.elucidator.TypedParameter
import me.omico.elucidator.addClass
import me.omico.elucidator.addFunction
import me.omico.elucidator.addParameter
import me.omico.elucidator.addProperty
import me.omico.elucidator.initializer
import me.omico.elucidator.ktFile
import me.omico.elucidator.lambdaTypeName
import me.omico.elucidator.modifier
import me.omico.elucidator.primaryConstructor
import me.omico.elucidator.returnStatement
import me.omico.rtvm.utility.KotlinxCoroutines

fun generateViewStateDispatcher(generationInfo: RtvmGenerationInfo): FileSpec =
    ktFile(generationInfo.packageName, generationInfo.generatedViewStateDispatcherFileName) {
        addClass(generationInfo.generatedViewStateDispatcherName) {
            addViewStateDispatcherProperties(generationInfo)
            addViewStateDispatcherFlow(generationInfo)
            addViewStateDispatcherParameters(generationInfo)
            addViewStateDispatcherParametersOperator(generationInfo)
        }
    }

private fun TypeScope.addViewStateDispatcherProperties(generationInfo: RtvmGenerationInfo): Unit =
    generationInfo.generatedViewStateDispatcherProperties.forEach { parameter ->
        addProperty(name = parameter.name, type = parameter.type) {
            modifier(KModifier.PRIVATE)
            initializer(parameter.defaultValue)
        }
    }

private fun TypeScope.addViewStateDispatcherFlow(generationInfo: RtvmGenerationInfo): Unit =
    addProperty("flow", generationInfo.generatedViewStateDispatcherFlowClassName) {
        initializer(
            "%M(${generationInfo.generatedViewStateDispatcherProperties.joinToString(transform = TypedParameter::name)}, %L)",
            KotlinxCoroutines.MemberNames.combineMemberName,
            generationInfo.viewStateClassName.constructorReference(),
        )
    }

private fun TypeScope.addViewStateDispatcherParameters(generationInfo: RtvmGenerationInfo) {
    addClass("Parameters") {
        primaryConstructor {
            generationInfo.generatedViewStateDispatcherProperties.forEach { parameter ->
                addParameter(parameter.name, parameter.type)
            }
        }
        generationInfo.generatedViewStateDispatcherProperties.forEach { parameter ->
            addProperty(parameter.name, parameter.type) {
                initializer(parameter.name)
            }
        }
    }
    addProperty("parameters", generationInfo.generatedViewStateDispatcherParametersClassName) {
        modifier(KModifier.PRIVATE)
        initializer("Parameters(${generationInfo.generatedViewStateDispatcherProperties.joinToString(transform = TypedParameter::name)})")
    }
}

private fun TypeScope.addViewStateDispatcherParametersOperator(generationInfo: RtvmGenerationInfo): Unit =
    addFunction("invoke") {
        modifier(KModifier.OPERATOR)
        addParameter(
            name = "block",
            type = lambdaTypeName(
                receiver = generationInfo.generatedViewStateDispatcherParametersClassName,
                returnType = UNIT,
            ),
        )
        returnStatement("block(parameters)")
    }
