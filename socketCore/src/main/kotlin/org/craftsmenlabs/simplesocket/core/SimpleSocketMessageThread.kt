package org.craftsmenlabs.simplesocket.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.simplesocket.core.log.SLogger
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class SimpleSocketMessageThread(
        private val objectMapper: ObjectMapper,
        private val outletRegistry: OutletRegistry,
        private val socket: Socket,
        private val logger: SLogger) : Thread() {

    private val QUIT_MESSAGE = "QUIT_MESSAGE"
    private var writer: PrintWriter? = null
    private var running = true

    private val messageQueue = LinkedBlockingQueue<Any>()

    override fun run() {
        logger.v { ("Eun") }

        try {
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            writer = PrintWriter(socket.outputStream, true)

            while (running) {
                sendNow()
                val line = reader.readLine()
                logger.v { "Line: $line" }
                if (line == null || line == QUIT_MESSAGE) {
                    break
                }

                logger.d { "<--" }
                handleMessage(line)
            }
        } catch (e: IOException) {
            logger.e { "Error handling a client: $e" }
        } finally {
            printQuit()
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

        val (className, messageObject) = objectMapper.readValue(line, SocketMessage::class.java)
        val clazz = outletRegistry.getClazz(className)
        val typelessObject = objectMapper.readValue(messageObject, clazz)

        val outlet = outletRegistry.getOutlet(className)
        if (outlet == null) {
            logger.w { "Could not find outlet for $className" }
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
            val socketMessage = SocketMessage(take.javaClass.name, messageObject)
            val valueAsString = objectMapper.writeValueAsString(socketMessage)
            logger.d { "-->" }
            println(valueAsString)
            logger.v { "Messagae send" }
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
