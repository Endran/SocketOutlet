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

package org.craftsmenlabs.socketoutlet.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.craftsmenlabs.socketoutlet.core.MessageThread
import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.core.initForSocketOutlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import java.net.Socket

class SocketOutletClient(
        private val id: String,
        private val outletRegistry: OutletRegistry,
        private val objectMapper: ObjectMapper = ObjectMapper().initForSocketOutlet(),
        private val logger: SLogger = CustomLogger(CustomLogger.Level.INFO)) {

    internal var messageThread: MessageThread? = null

    var serverConnectedCallback: (() -> Unit)? = null
    var serverDisconnectedCallback: (() -> Unit)? = null

    fun start(ipAddress: String, port: Int) {
        if (isRunning()) {
            throw RuntimeException("Thread already running")
        }

        val socket = Socket(ipAddress, port)
        messageThread = MessageThread(objectMapper, outletRegistry, socket, logger)
        messageThread?.localActorId = id
        messageThread?.connectedCallback = serverConnectedCallback
        messageThread?.disconnctedCallback = serverDisconnectedCallback
        messageThread?.start()
    }

    fun send(message: Any) {
        messageThread?.run {
            logger.d { "Sending" }
            send(message)
        } ?: throw RuntimeException("Thread not running")
    }

    fun stop() {
        messageThread?.interrupt()
        messageThread?.connectedCallback = null
        messageThread?.disconnctedCallback = null
        messageThread = null
    }

    fun isRunning() = messageThread != null
}
