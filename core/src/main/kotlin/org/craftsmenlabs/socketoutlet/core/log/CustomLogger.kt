package org.craftsmenlabs.socketoutlet.core.log

import java.util.regex.Pattern


class CustomLogger(val level: Level) : SLogger {

    enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, NONE
    }

    override fun v(message: () -> String) {
        if (Level.VERBOSE.ordinal >= level.ordinal) {
            println("VERBOSE (${getTag()}): ${message.invoke()}")
        }
    }

    override fun d(message: () -> String) {
        if (Level.DEBUG.ordinal >= level.ordinal) {
            println("DEBUG  (${getTag()}): ${message.invoke()}")
        }
    }

    override fun i(message: () -> String) {
        if (Level.INFO.ordinal >= level.ordinal) {
            println("INFO    (${getTag()}): ${message.invoke()}")
        }
    }

    override fun w(message: () -> String) {
        if (Level.WARNING.ordinal >= level.ordinal) {
            println("WARNING (${getTag()}): ${message.invoke()}")
        }
    }

    override fun e(message: () -> String) {
        if (Level.ERROR.ordinal >= level.ordinal) {
            println("ERROR  (${getTag()}): ${message.invoke()}")
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
}