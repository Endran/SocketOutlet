package org.craftsmenlabs.socketoutlet.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.socketoutlet.core.MessageThread
import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.core.initForSocketOutlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import java.net.ServerSocket

class SocketOutletServer constructor(
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSocketOutlet(),
        private val logger: SLogger = CustomLogger(CustomLogger.Level.INFO)) {

    private var running: Boolean = false
    private var threadList = mutableListOf<MessageThread>()

    fun open(port: Int) {
        if (running) {
            throw RuntimeException("Thread already running")
        }

        running = true
        ServerSocket(port).use {
            logger.i { "Server is running." }

            while (running) {
                val socket = it.accept()
                logger.i { "Client connected" }

                val messageThread = MessageThread(objectMapper, outletRegistry, socket, logger)
                messageThread.start()
                threadList.add(messageThread)

                threadList.retainAll { it.isRunning() }
            }
        }
        running = false

        logger.i { "Server stopped." }
    }

    fun close() {
        running = false
        threadList.forEach(MessageThread::interrupt)
        threadList.clear()
    }
}
