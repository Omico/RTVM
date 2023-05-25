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
package me.omico.rtvm.utility

import com.google.devtools.ksp.symbol.FileLocation
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun FileLocation.extractDefaultValue(name: String): String =
    extractDefaultValue(name, Path(filePath).readLines()[lineNumber - 1])

fun extractDefaultValue(name: String, content: String): String =
    content.split(",")
        .first { it.contains("val\\s+$name(\\s+)?:".toRegex()) }
        .replaceBefore("=", "")
        .replace("=", "")
        .trim()
