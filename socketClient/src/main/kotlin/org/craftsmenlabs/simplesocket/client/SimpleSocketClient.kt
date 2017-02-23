package org.craftsmenlabs.simplesocket.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.simplesocket.core.OutletRegistry
import org.craftsmenlabs.simplesocket.core.SimpleSocketMessageThread
import org.craftsmenlabs.simplesocket.core.initForSimpleSockets
import org.craftsmenlabs.simplesocket.core.log.CustomLogger
import org.craftsmenlabs.simplesocket.core.log.SLogger
import java.net.Socket

class SimpleSocketClient(
        private val ipAddress: String,
        private val port: Int,
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSimpleSockets(),
        private val logger: SLogger = CustomLogger(CustomLogger.Level.DEBUG)) {

    private var messageThread: SimpleSocketMessageThread? = null
    private var running = false

    fun start() {
        if (running) {
            throw RuntimeException("Thread already running")
        }

        running = true
        val socket = Socket(ipAddress, port)
        messageThread = SimpleSocketMessageThread(objectMapper, outletRegistry, socket, logger)
        messageThread?.start()
    }

    fun send(message: Any) {
        logger.d { "Sending" }
        messageThread?.send(message)
    }

    fun isRunning() = running
}
