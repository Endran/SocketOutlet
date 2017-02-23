package org.craftsmenlabs.simplesocket.client

import org.craftsmenlabs.simplesocket.core.OutletRegistry
import org.craftsmenlabs.simplesocket.core.SharedFactory
import org.craftsmenlabs.simplesocket.core.SimpleSocketMessageThread
import java.net.Socket

class SimpleSocketClient(
        private val ipAddress: String,
        private val port: Int,
        private val outletRegistry: OutletRegistry,
        sharedFactory: SharedFactory = SharedFactory()) {

    private val objectMapper = sharedFactory.objectMapper()
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


//    @Throws(IOException::class)
//    fun connectToServer(ipAddress: String, port: Int, s: SocketMessage) {
//
//        Socket(ipAddress, port).use { socket ->
//            reader = BufferedReader(InputStreamReader(socket.inputStream))
//            writer = PrintWriter(socket.outputStream, true)
//
//            // Consume the initial welcoming messages from the server
//            for (i in 0..2) {
//                println(reader!!.readLine() + "\n")
//            }
//            val objectMapper = SharedFactory().objectMapper()
//            writer!!.println(objectMapper.writeValueAsString(s))
//
//            println(reader!!.readLine() + "\n")
//        }
//    }