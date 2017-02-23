package org.craftsmenlabs.simplesocket.core

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class SimpleSocketMessageThread(
        private val objectMapper: ObjectMapper,
        private val outletRegistry: OutletRegistry,
        private val socket: Socket) : Thread() {

    private val QUIT_MESSAGE = "QUIT_MESSAGE"
    private var writer: PrintWriter? = null
    private var running = true

    private val messageQueue = LinkedBlockingQueue<Any>()

    override fun run() {
        println("SimpleSocketMessageThread run")
        try {
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            writer = PrintWriter(socket.outputStream, true)

            while (running) {
                sendNow()
                val line = reader.readLine()
                println("SimpleSocketMessageThread line: $line")
                if (line == null || line == QUIT_MESSAGE) {
                    break
                }

                handleMessage(line)
            }
        } catch (e: IOException) {
            println("Error handling a client: $e")
        } finally {
            printQuit()
            running = false;

            try {
                socket.close()
            } catch (e: IOException) {
                println("Couldn't close a socket, what's going on?")
            }

            println("Connection with a client closed")
        }
    }

    private fun handleMessage(line: String) {
        val (className, messageObject) = objectMapper.readValue(line, SocketMessage::class.java)
        val clazz = outletRegistry.getClazz(className)
        val typelessObject = objectMapper.readValue(messageObject, clazz)

        val outlet = outletRegistry.getOutlet(className)
        if (outlet == null) {
            println("SimpleSocketMessageThread could not find outlet for $className")
        } else {
            outlet.onTypelessMessage(typelessObject) {
                println("SimpleSocketMessageThread egress used")
                messageQueue.put(it)
                sendNow()
            }
        }
    }

    fun send(message: Any) {
        if (running) {
            println("SimpleSocketMessageThread send queued")
            messageQueue.put(message)
        }

        Thread({
            sendNow()
        }).start()
    }

    private fun sendNow() {
        kotlin.io.println("SimpleSocketMessageThread sendNow writer=${writer != null}")
        if (messageQueue.isEmpty()) {
            return
        }

        writer?.run {
            val take = messageQueue.take()
            kotlin.io.println("SimpleSocketMessageThread send queued")
            val messageObject = objectMapper.writeValueAsString(take)
            val socketMessage = SocketMessage(take.javaClass.name, messageObject)
            val valueAsString = objectMapper.writeValueAsString(socketMessage)
            println(valueAsString)
            kotlin.io.println("SimpleSocketMessageThread messagae send")
        }
    }

    override fun interrupt() {
        printQuit()
        running = false
        super.interrupt()
    }

    private fun printQuit() {
        writer?.print(QUIT_MESSAGE)
    }
}
