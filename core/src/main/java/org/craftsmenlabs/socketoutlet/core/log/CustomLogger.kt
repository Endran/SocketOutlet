/*
 * Copyright (c) 2017 David Hardy.
 * Copyright (c) 2017 Craftsmenlabs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.craftsmenlabs.socketoutlet.core.log

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern


class CustomLogger(val level: Level, val logTag: String = "") : SLogger {

    enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, NONE
    }

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    override fun v(message: () -> String) {
        if (Level.VERBOSE.ordinal >= level.ordinal) {
            println("${getTimeStamp()} VERBOSE (${getTag()}) $logTag: ${message.invoke()}")
        }
    }

    override fun d(message: () -> String) {
        if (Level.DEBUG.ordinal >= level.ordinal) {
            println("${getTimeStamp()} DEBUG   (${getTag()}) $logTag: ${message.invoke()}")
        }
    }

    override fun i(message: () -> String) {
        if (Level.INFO.ordinal >= level.ordinal) {
            println("${getTimeStamp()} INFO    (${getTag()}) $logTag: ${message.invoke()}")
        }
    }

    override fun w(message: () -> String) {
        if (Level.WARNING.ordinal >= level.ordinal) {
            println("${getTimeStamp()} WARNING (${getTag()}) $logTag: ${message.invoke()}")
        }
    }

    override fun e(message: () -> String) {
        if (Level.ERROR.ordinal >= level.ordinal) {
            println("${getTimeStamp()} ERROR   (${getTag()}) $logTag: ${message.invoke()}")
        }
    }

    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    private val CALL_STACK_INDEX = 2
    private fun createStackElementTag(element: StackTraceElement): String {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag.substring(tag.lastIndexOf('.') + 1)
    }

    private fun getTag(): String {
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= CALL_STACK_INDEX) {
            println("LOG ERROR: Synthetic stacktrace didn't have enough elements :(")
            return "GENERIC"
        }
        return createStackElementTag(stackTrace[CALL_STACK_INDEX])
    }

    private fun getTimeStamp(): String {
        return LocalDateTime.now().format(formatter)
    }
}
