package org.craftsmenlabs.socketoutlet.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class MessageThread(
        private val objectMapper: ObjectMapper,
        private val outletRegistry: OutletRegistry,
        private val socket: Socket,
        private val logger: SLogger) : Thread() {

    private var writer: PrintWriter? = null
    private var running = true

    private val messageQueue = LinkedBlockingQueue<Any>()

    override fun run() {
        logger.v { ("Run") }

        try {
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            writer = PrintWriter(socket.outputStream, true)

            while (running) {
                sendNow()
                val line = reader.readLine()
                logger.v { "Line: $line" }
                if (line == null) {
                    break
                }

                logger.d { "<--" }
                handleMessage(line)
            }
        } catch (e: IOException) {
            if (running) {
                logger.e { "An error in the run loop, shutting down: $e" }
            }
        } finally {
            running = false;

            try {
                socket.close()
            } catch (e: IOException) {
                logger.e { "Couldn't close a socket, what's going on?" }
            }

            logger.i { "Connection with a client closed" }
        }
    }

    private fun handleMessage(line: String) {

        val (simpleName, messageObject) = objectMapper.readValue(line, SocketMessage::class.java)
        val clazz = outletRegistry.getClazz(simpleName)
        val typelessObject = objectMapper.readValue(messageObject, clazz)

        val outlet = outletRegistry.getOutlet(simpleName)
        if (outlet == null) {
            logger.w { "Could not find outlet for $simpleName" }
        } else {
            outlet.onTypelessMessage(typelessObject) {
                logger.v { "Egress used" }
                messageQueue.put(it)
                sendNow()
            }
        }
    }

    fun send(message: Any) {
        if (running) {
            logger.v { "Send queued" }
            messageQueue.put(message)
        }

        Thread({
            sendNow()
        }).start()
    }

    private fun sendNow() {
        logger.v { "SendNow writer=${writer != null}" }
        if (messageQueue.isEmpty()) {
            return
        }

        writer?.run {
            val take = messageQueue.take()
            logger.v { "Send queued" }
            val messageObject = objectMapper.writeValueAsString(take)
            val socketMessage = SocketMessage(take.javaClass.simpleName, messageObject)
            val valueAsString = objectMapper.writeValueAsString(socketMessage)
            logger.d { "-->" }
            println(valueAsString)
            logger.v { "Message send" }
        }
    }

    override fun interrupt() {
        running = false
        socket.close()
        super.interrupt()
    }

    fun isRunning() = running
}
