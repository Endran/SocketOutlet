package org.craftsmenlabs.simplesocket.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.simplesocket.core.OutletRegistry
import org.craftsmenlabs.simplesocket.core.SimpleSocketMessageThread
import org.craftsmenlabs.simplesocket.core.initForSimpleSockets
import java.net.ServerSocket

class SimpleSocketServer constructor(
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSimpleSockets()) {

    private var running: Boolean = false

    fun open(port: Int) {
        if (running) {
            throw RuntimeException("Thread already running")
        }

        running = true
        ServerSocket(port).use {
            System.out.println("The SimpleSocketServer is running.")

            while (running) {
                val socket = it.accept()
                val messageThread = SimpleSocketMessageThread(objectMapper, outletRegistry, socket)
                messageThread.start()
            }
        }
        running = false

        System.out.println("The SimpleSocketServer stopped.")
    }

//    fun close() {
    // This will leak... needs to be book kept
//        messageThread?.interrupt()
//        messageThread = null
//    }
}
