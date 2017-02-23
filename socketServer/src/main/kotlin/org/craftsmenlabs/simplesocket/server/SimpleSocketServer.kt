package org.craftsmenlabs.simplesocket.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.simplesocket.core.OutletRegistry
import org.craftsmenlabs.simplesocket.core.SharedFactory
import java.net.ServerSocket

class SimpleSocketServer constructor(val outletRegistry: OutletRegistry, sharedFactory: SharedFactory = SharedFactory()) {

    val objectMapper: ObjectMapper

    init {
        objectMapper = sharedFactory.objectMapper()
    }

    var running: Boolean = false

    fun open(port: Int) {
        if (running) {
            return
        }

        running = true
        var clientNumber = 0
        val serverSocket = ServerSocket(port)
        try {
            System.out.println("The capitalization server is running.")
            while (running) {
                Capitalizer(objectMapper, outletRegistry, serverSocket.accept(), clientNumber++).start()
            }
        } finally {
            serverSocket.close()
        }
        System.out.println("The capitalization server stopped.")
    }

    fun close() {
        running = false
    }
}