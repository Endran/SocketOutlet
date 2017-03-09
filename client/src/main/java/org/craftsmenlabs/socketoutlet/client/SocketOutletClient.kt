package org.craftsmenlabs.socketoutlet.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.socketoutlet.core.MessageThread
import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.core.initForSocketOutlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import java.net.Socket

class SocketOutletClient(
        private val id: String,
        private val ipAddress: String,
        private val port: Int,
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSocketOutlet(),
        private val logger: SLogger = CustomLogger(CustomLogger.Level.INFO)) {

    internal var messageThread: MessageThread? = null

    var serverConnectedCallback: (() -> Unit)? = null
    var serverDisconnectedCallback: (() -> Unit)? = null

    fun start() {
        if (isRunning()) {
            throw RuntimeException("Thread already running")
        }

        val socket = Socket(ipAddress, port)
        messageThread = MessageThread(objectMapper, outletRegistry, socket, logger)
        messageThread?.actorId = id
        messageThread?.connectedCallback = serverConnectedCallback
        messageThread?.disconnctedCallback = serverDisconnectedCallback
        messageThread?.start()
    }

    fun send(message: Any) {
        messageThread?.run {
            logger.d { "Sending" }
            send(message)
        } ?: throw RuntimeException("Thread not running")
    }

    fun stop() {
        messageThread?.interrupt()
        messageThread = null
    }

    fun isRunning() = messageThread != null
}
