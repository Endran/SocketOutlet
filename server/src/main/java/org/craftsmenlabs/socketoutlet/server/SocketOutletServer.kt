package org.craftsmenlabs.socketoutlet.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.socketoutlet.core.MessageThread
import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.core.initForSocketOutlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import java.net.ServerSocket

class SocketOutletServer constructor(
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSocketOutlet(),
        private val logger: SLogger = CustomLogger(CustomLogger.Level.INFO)) {

    var running: Boolean = false
        internal set

    internal var threadList = mutableListOf<MessageThread>()
    internal var threadMap = mutableMapOf<String, MessageThread>()

    var clientConnectedCallback: ((String) -> Unit)? = null
    var clientDisconnectedCallback: ((String) -> Unit)? = null

    fun open(port: Int) {
        if (running) {
            throw RuntimeException("Thread already running")
        }

        running = true
        Thread({
            ServerSocket(port).use {
                logger.i { "Server is running on port $port." }
                serverLoop(it)
            }
            running = false
            logger.i { "Server stopped." }
        }).start()
    }

    internal fun serverLoop(it: ServerSocket) {
        while (running) {
            val socket = it.accept()
            logger.i { "Client connected" }

            val messageThread = MessageThread(objectMapper, outletRegistry, socket, logger)
            messageThread.start()
            messageThread.idCallback = {
                threadMap.put(it, messageThread)
                clientConnectedCallback?.invoke(it)
            }
            messageThread.disconnctedCallback = {
                threadList.remove(messageThread)
                val id = threadMap.entries.find { it.value == messageThread }?.key
                id?.run {
                    val remove = threadMap.remove(id)
                    clientDisconnectedCallback?.invoke(id)
                    remove?.connectedCallback = null
                    remove?.disconnctedCallback = null
                }
            }
            threadList.add(messageThread)

            threadList.retainAll { it.isRunning() }
            threadMap.filter { it.value.isRunning() }
        }
    }

    fun close() {
        running = false
        threadList.forEach(MessageThread::interrupt)
        threadList.clear()
    }

    fun send(clientId: String, message: Any) {
        threadMap[clientId]?.send(message)
    }
}
