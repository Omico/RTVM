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
package me.omico.rtvm.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.writeTo
import me.omico.rtvm.RtvmState
import me.omico.rtvm.codegen.createRtvmGenerationInfo
import me.omico.rtvm.codegen.generateViewStateDispatcher

class RtvmProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("RtvmProcessor starting.")
        resolver.generateViewStateDispatchers()
        return emptyList()
    }

    private fun Resolver.generateViewStateDispatchers(): Unit =
        getSymbolsWithAnnotation(RtvmState::class.java.name)
            .map { ksAnnotated -> ksAnnotated as KSClassDeclaration }
            .map(::createRtvmGenerationInfo)
            .forEach { generationInfo ->
                generateViewStateDispatcher(generationInfo)
                    .writeTo(
                        codeGenerator = codeGenerator,
                        aggregating = false,
                        originatingKSFiles = listOf(generationInfo.ksFile),
                    )
            }
}
