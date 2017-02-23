package org.craftsmenlabs.simplesocket.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.simplesocket.core.OutletRegistry
import org.craftsmenlabs.simplesocket.core.SimpleSocketMessageThread
import org.craftsmenlabs.simplesocket.core.initForSimpleSockets
import java.net.Socket

class SimpleSocketClient(
        private val ipAddress: String,
        private val port: Int,
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSimpleSockets()) {

    private var messageThread: SimpleSocketMessageThread? = null
    private var running = false

    fun start() {
        if (running) {
            throw RuntimeException("Thread already running")
        }

        running = true
        val socket = Socket(ipAddress, port)
        messageThread = SimpleSocketMessageThread(objectMapper, outletRegistry, socket)
        messageThread?.start()
    }

    fun send(message: Any) {
        println("SimpleSocketClient send")
        messageThread?.send(message)
    }

    fun isRunning() = running
}
