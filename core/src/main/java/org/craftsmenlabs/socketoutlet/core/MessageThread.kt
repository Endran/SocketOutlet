/*
 * Copyright (c) 2017 David Hardy.
 * Copyright (c) 2017 Craftsmenlabs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    var localActorId: String? = null
    var remoteActorId: String? = null

    private val messageQueue = LinkedBlockingQueue<Any>()
    var connectedCallback: (() -> Unit)? = null
    var idCallback: ((String) -> Unit)? = null
    var disconnctedCallback: (() -> Unit)? = null

    override fun run() {
        logger.v { ("Run") }
        running = true

        localActorId?.run { putMessage(HelloMessage(this)) }

        try {
            val reader = getReader(socket.inputStream)
            writer = getWriter(socket.outputStream)

            connectedCallback?.invoke()

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

            disconnctedCallback?.invoke()
            logger.i { "Connection with a client closed" }
        }
    }

    private fun handleMessage(line: String) {
        val (name, messageObject) = objectMapper.readValue(line, SocketMessage::class.java)

        val handled = handleHelloIdCallback(messageObject, name)
        if (handled) {
            return
        }

        val clazz = outletRegistry.getClazz(name)
        if (clazz == null) {
            putMessage(ErrorMessage("Class $name cannot be found by the server"))
            sendNow()
            return
        }

        val typelessObject = objectMapper.readValue(messageObject, clazz)

        val outlet = outletRegistry.getOutlet(name)
        if (outlet == null) {
            logger.d { "Could not find outlet for $name" }
        } else {
            outlet.onTypelessMessage(remoteActorId ?: "", typelessObject) {
                logger.v { "Egress used" }
                putMessage(it)
                sendNow()
            }
        }
    }

    private fun putMessage(it: Any) {
        synchronized(messageQueue) {
            messageQueue.put(it)
        }
    }

    private fun handleHelloIdCallback(messageObject: String, name: String): Boolean {
        val isHelloCallback = name == HelloMessage::class.java.name
        idCallback?.run {
            if (isHelloCallback) {
                val id = objectMapper.readValue(messageObject, HelloMessage::class.java).id
                remoteActorId = id
                this.invoke(id)
            }
        }
        idCallback == null
        return isHelloCallback
    }

    fun send(message: Any) {
        if (!running) throw RuntimeException("The thread is not running")

        logger.v { "Send queued" }
        putMessage(message)

        Thread({
            sendNow()
        }).start()
    }

    private fun sendNow() {
        logger.v { "SendNow writer=${writer != null}" }
        synchronized(messageQueue) {
            if (messageQueue.isEmpty()) {
                return
            }
        }

        writer?.run {
            synchronized(messageQueue) {
                while (messageQueue.isNotEmpty()) {
                    val take = messageQueue.take()
                    logger.v { "Send queued" }
                    val messageObject = objectMapper.writeValueAsString(take)
                    val socketMessage = SocketMessage(take.javaClass.name, messageObject)
                    val valueAsString = objectMapper.writeValueAsString(socketMessage)
                    logger.d { "-->" }
                    println(valueAsString)
                    logger.v { "Message send" }
                }
            }
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
