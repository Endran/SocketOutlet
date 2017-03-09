package org.craftsmenlabs.socketoutlet.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import java.io.*
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

open class MessageThread(
        private val objectMapper: ObjectMapper,
        private val outletRegistry: OutletRegistry,
        private val socket: Socket,
        private val logger: SLogger) : Thread() {

    private var writer: PrintWriter? = null
    private var running = false

    private val messageQueue = LinkedBlockingQueue<Any>()

    override fun run() {
        logger.v { ("Run") }
        running = true

        try {
            val reader = getReader(socket.inputStream)
            writer = getWriter(socket.outputStream)

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
        if (!running) throw RuntimeException("The thread is not running")

        logger.v { "Send queued" }
        messageQueue.put(message)

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

    internal open fun getReader(inputStream: InputStream) = BufferedReader(InputStreamReader(inputStream))
    internal open fun getWriter(outputStream: OutputStream) = PrintWriter(outputStream, true)
}
