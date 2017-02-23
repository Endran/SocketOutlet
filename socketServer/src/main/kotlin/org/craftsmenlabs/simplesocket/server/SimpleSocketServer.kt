package org.craftsmenlabs.simplesocket.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.simplesocket.core.OutletRegistry
import org.craftsmenlabs.simplesocket.core.SimpleSocketMessageThread
import org.craftsmenlabs.simplesocket.core.initForSimpleSockets
import org.craftsmenlabs.simplesocket.core.log.CustomLogger
import org.craftsmenlabs.simplesocket.core.log.SLogger
import java.net.ServerSocket

class SimpleSocketServer constructor(
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSimpleSockets(),
        private val logger: SLogger = CustomLogger(CustomLogger.Level.DEBUG)) {

    private var running: Boolean = false

    fun open(port: Int) {
        if (running) {
            throw RuntimeException("Thread already running")
        }

        running = true
        ServerSocket(port).use {
            logger.i {
                "Server is running."
            }

            while (running) {
                val socket = it.accept()
                logger.i {
                    "Client connected"
                }
                val messageThread = SimpleSocketMessageThread(objectMapper, outletRegistry, socket, logger)
                messageThread.start()
            }
        }
        running = false

        logger.i {
            "Server stopped."
        }
    }

//    fun close() {
    // This will leak... needs to be book kept
//        messageThread?.interrupt()
//        messageThread = null
//    }
}
