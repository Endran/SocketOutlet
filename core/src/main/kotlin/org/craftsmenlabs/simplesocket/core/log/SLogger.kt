package org.craftsmenlabs.simplesocket.core.log

interface SLogger {
    fun v(message: (() -> String))
    fun d(message: (() -> String))
    fun i(message: (() -> String))
    fun w(message: (() -> String))
    fun e(message: (() -> String))
}
