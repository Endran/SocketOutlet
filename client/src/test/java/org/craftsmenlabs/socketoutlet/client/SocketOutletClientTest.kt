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
import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.socketoutlet.core.MessageThread
import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.core.log.SLogger
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.net.Socket

class SocketOutletClientTest {

    private val testId = "TEST_CLIENT"
    private var ipAddress = "TEST_IP_ADDRESS"
    private var port = 111
    private var objectMapper: ObjectMapper = ObjectMapper();

    @Injectable
    private lateinit var outletRegistry: OutletRegistry

    @Injectable
    private lateinit var logger: SLogger

    private lateinit var socketOutletClient: SocketOutletClient

    @Injectable
    private lateinit var messageThread: MessageThread

    private val testMessage = Object()

    @Before
    fun setUp() {
        socketOutletClient = SocketOutletClient(testId, outletRegistry, objectMapper, logger)
    }

    @Test
    fun shouldThrow_whenSendIsCalledWhileNotRunning() {
        assertThatThrownBy({ socketOutletClient.send(testMessage) }).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun shouldBeRunning_whenMessageThreadIsSet() {
        assertThat(socketOutletClient.isRunning()).isFalse()

        socketOutletClient.messageThread = messageThread

        assertThat(socketOutletClient.isRunning()).isTrue()
    }

    @Test
    fun shouldSendMessageOnThread_whenSendIsCalled() {
        socketOutletClient.messageThread = messageThread
        socketOutletClient.send(testMessage)

        object : Verifications() {
            init {
                messageThread.send(testMessage)
            }
        }
    }

    @Test
    fun shouldThrow_whenSendIsCalledAfterStop() {
        socketOutletClient.messageThread = messageThread

        socketOutletClient.stop()

        assertThatThrownBy({ socketOutletClient.send(testMessage) }).isInstanceOf(RuntimeException::class.java)

        object : Verifications() {
            init {
                messageThread.interrupt()
            }
        }
    }

    @Test
    fun shouldThrow_whenStartedTwice(
            @Mocked socket: Socket,
            @Mocked messageThread: MessageThread) {
        socketOutletClient.start(ipAddress, port)
        assertThatThrownBy({ socketOutletClient.start(ipAddress, port) }).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun shouldStartMessageThread_whenStarted(
            @Mocked socket: Socket,
            @Mocked messageThread: MessageThread) {

        object : Expectations() {
            init {
                Socket(ipAddress, port)
                result = socket

                MessageThread(objectMapper, outletRegistry, socket, logger)
                result = messageThread
            }
        }

        socketOutletClient.start(ipAddress, port)

        object : Verifications() {
            init {
                messageThread.start()
            }
        }
    }

    @Ignore("Something with captureing the callbacks in Java")
    @Test
    fun shouldGetACallBack_whenConnected(
            @Mocked socket: Socket,
            @Mocked messageThread: MessageThread) {

        object : Expectations() {
            init {
                Socket(ipAddress, port)
                result = socket

                MessageThread(objectMapper, outletRegistry, socket, logger)
                result = messageThread
            }
        }

        socketOutletClient.start(ipAddress, port)

        var serverConnectedCallbackInvoked = false
        socketOutletClient.serverConnectedCallback = {
            serverConnectedCallbackInvoked = true
        }

        object : Verifications() {
            init {
                // Something with captureing the callbacks in Java :/
            }
        }

        assertThat(serverConnectedCallbackInvoked).isTrue()
    }

    @Ignore("Something with captureing the callbacks in Java")
    @Test
    fun shouldGetACallBack_whenDisconnected(
            @Mocked socket: Socket,
            @Mocked messageThread: MessageThread) {

        object : Expectations() {
            init {
                Socket(ipAddress, port)
                result = socket

                MessageThread(objectMapper, outletRegistry, socket, logger)
                result = messageThread
            }
        }

        socketOutletClient.start(ipAddress, port)

        var serverDisconnectedCallbackInvoked = false
        socketOutletClient.serverDisconnectedCallback = {
            serverDisconnectedCallbackInvoked = true
        }

        object : Verifications() {
            init {
                // Something with captureing the callbacks in Java :/
            }
        }

        assertThat(serverDisconnectedCallbackInvoked).isTrue()
    }
}
