package org.craftsmenlabs.simplesocket.server

import java.net.ServerSocket

class SimpleSocketServer {

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
                Capitalizer(serverSocket.accept(), clientNumber++).start()
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