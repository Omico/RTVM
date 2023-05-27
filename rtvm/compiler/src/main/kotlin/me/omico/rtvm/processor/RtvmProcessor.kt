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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ksp.writeTo
import me.omico.rtvm.RtvmState
import me.omico.rtvm.codegen.createRtvmGenerationInfo
import me.omico.rtvm.codegen.generateViewStateDispatcher

class RtvmProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    private var resolved = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (resolved) return emptyList()
        resolver.generate()
        resolved = true
        return emptyList()
    }

    private fun Resolver.generate(): Unit =
        getAllFiles()
            .flatMap(KSFile::collectViewStateClassDeclarations)
            .map(::createRtvmGenerationInfo)
            .map(::generateViewStateDispatcher)
            .forEach { file -> file.writeTo(codeGenerator, aggregating = true) }
}

@OptIn(KspExperimental::class)
private fun KSFile.collectViewStateClassDeclarations(): List<KSClassDeclaration> =
    declarations
        .flatMap { ksDeclaration -> ksDeclaration.annotations.filter { ksDeclaration.isAnnotationPresent(RtvmState::class) } }
        .mapNotNull { ksAnnotation -> ksAnnotation.parent as? KSClassDeclaration }
        .toList()
