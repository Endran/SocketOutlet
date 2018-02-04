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

package org.craftsmenlabs.socketoutlet.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.socketoutlet.client.SocketOutletClient
import org.craftsmenlabs.socketoutlet.core.Egress
import org.craftsmenlabs.socketoutlet.core.Outlet
import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.core.initForSocketOutlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.server.SocketOutletServer
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class IntegrationTest() {

    companion object {
        @JvmStatic
        var PORT = 7000
        val IP_ADDRESS = "127.0.0.1"
        val TEST_MESSAGE = "TEST_MESSAGE"
        val TEST_CLIENT_ID = "TEST_CLIENT_ID"
        val TEST_DELAY: Long = 2000L
    }

    lateinit var server: SocketOutletServer
    lateinit var simpleServerOutlet: CollectingOutlet<SimpleParam>
    lateinit var complexServerOutlet: CollectingOutlet<ComplexParam>

    lateinit var client: SocketOutletClient
    lateinit var simpleClientOutlet: CollectingOutlet<SimpleParam>
    lateinit var complexClientOutlet: CollectingOutlet<ComplexParam>

    var serverConnected = false
    val serverConnectedClientIds = mutableListOf<String>()
    val serverDisconnectedClientIds = mutableListOf<String>()

    @Before
    fun setUp() {
        PORT++
        setupServer()
        setupClient(TEST_CLIENT_ID)
        Thread.sleep(TEST_DELAY)
    }

    @After
    fun tearDown() {
        client.stop()
        server.close()

        server.clientConnectedCallback = null
        server.clientDisconnectedCallback = null

        client.serverConnectedCallback = null
        client.serverDisconnectedCallback = null

        Thread.sleep(TEST_DELAY)
    }

    @Test
    fun shouldSendSimpleMessageFromClientToServer() {
        val testMessage = SimpleParam(TEST_MESSAGE)

        client.send(testMessage)
        await()

        assertThat(simpleServerOutlet.messageCalls).hasSize(1)
        assertThat(simpleServerOutlet.messageCalls[0].first).isEqualTo(testMessage)
    }

    @Test
    fun shouldSendSimpleMessageFromServerToClient() {
        val testMessage = SimpleParam(TEST_MESSAGE)

        server.send(TEST_CLIENT_ID, testMessage)
        await()

        assertThat(simpleClientOutlet.messageCalls).hasSize(1)
        assertThat(simpleClientOutlet.messageCalls[0].first).isEqualTo(testMessage)
    }

    @Test
    fun shouldSendComplexMessageFromClientToServer() {
        val testMessage = ComplexParam(TEST_MESSAGE, listOf(SimpleParam(TEST_MESSAGE + 1), SimpleParam(TEST_MESSAGE + 2)))

        client.send(testMessage)
        await()

        assertThat(complexServerOutlet.messageCalls).hasSize(1)
        assertThat(complexServerOutlet.messageCalls[0].first).isEqualTo(testMessage)
    }

    @Test
    fun shouldSendComplexMessageFromServerToClient() {
        val testMessage = ComplexParam(TEST_MESSAGE, listOf(SimpleParam(TEST_MESSAGE + 1), SimpleParam(TEST_MESSAGE + 2)))

        server.send(TEST_CLIENT_ID, testMessage)
        await()

        assertThat(complexClientOutlet.messageCalls).hasSize(1)
        assertThat(complexClientOutlet.messageCalls[0].first).isEqualTo(testMessage)
    }

    @Test
    fun shouldSendTwoMessagesInOrderFromClientToServer() {
        val testMessage1 = ComplexParam(TEST_MESSAGE + 0, listOf(SimpleParam(TEST_MESSAGE + 1), SimpleParam(TEST_MESSAGE + 2)))
        val testMessage2 = SimpleParam(TEST_MESSAGE + 3)
        val testMessage3 = SimpleParam(TEST_MESSAGE + 4)

        client.send(testMessage1)
        client.send(testMessage2)
        client.send(testMessage3)
        await()

        assertThat(complexServerOutlet.messageCalls).hasSize(1)
        assertThat(complexServerOutlet.messageCalls[0].first).isEqualTo(testMessage1)

        assertThat(simpleServerOutlet.messageCalls).hasSize(2)
        assertThat(simpleServerOutlet.messageCalls[0].first).isEqualTo(testMessage2)
        assertThat(simpleServerOutlet.messageCalls[1].first).isEqualTo(testMessage3)
    }

    @Test
    fun shouldSendMultipleMessagesInOrderFromServerToClient() {
        val testMessage1 = SimpleParam(TEST_MESSAGE + 0)
        val testMessage2 = ComplexParam(TEST_MESSAGE + 1, listOf(SimpleParam(TEST_MESSAGE + 2), SimpleParam(TEST_MESSAGE + 3)))
        val testMessage3 = ComplexParam(TEST_MESSAGE + 4, listOf(SimpleParam(TEST_MESSAGE + 5), SimpleParam(TEST_MESSAGE + 6)))

        server.send(TEST_CLIENT_ID, testMessage1)
        server.send(TEST_CLIENT_ID, testMessage2)
        server.send(TEST_CLIENT_ID, testMessage3)
        await()

        assertThat(simpleClientOutlet.messageCalls).hasSize(1)
        assertThat(simpleClientOutlet.messageCalls[0].first).isEqualTo(testMessage1)

        assertThat(complexClientOutlet.messageCalls).hasSize(2)
        assertThat(complexClientOutlet.messageCalls[0].first).isEqualTo(testMessage2)
        assertThat(complexClientOutlet.messageCalls[1].first).isEqualTo(testMessage3)
    }

    @Test
    fun shouldBeAbleToReactToReceivingAMessageReceived() {
        val testMessage1 = SimpleParam(TEST_MESSAGE + 1)
        val testMessage2 = SimpleParam(TEST_MESSAGE + 2)
        val testMessage3 = SimpleParam(TEST_MESSAGE + 3)

        client.send(testMessage1)
        await()

        assertThat(simpleServerOutlet.messageCalls).hasSize(1)
        assertThat(simpleServerOutlet.messageCalls[0].first).isEqualTo(testMessage1)

        simpleServerOutlet.messageCalls[0].second.send(testMessage2)
        await()

        assertThat(simpleClientOutlet.messageCalls).hasSize(1)
        assertThat(simpleClientOutlet.messageCalls[0].first).isEqualTo(testMessage2)

        simpleClientOutlet.messageCalls[0].second.send(testMessage3)
        await()

        assertThat(simpleServerOutlet.messageCalls).hasSize(2)
        assertThat(simpleServerOutlet.messageCalls[1].first).isEqualTo(testMessage3)
    }

    @Test
    fun shouldGetNotifiedAsServer_whenClientConnectsAndDisconnects() {

        await()

        assertThat(serverConnectedClientIds).containsExactly(TEST_CLIENT_ID)

        client.stop()
        await()

        assertThat(serverDisconnectedClientIds).containsExactly(TEST_CLIENT_ID)
    }

    @Test
    fun shouldGetNotifiedAsServer_whenANewClientConnectsAndDisconnects() {
        setupClient(TEST_CLIENT_ID + 1)
        await()

        assertThat(serverConnectedClientIds).containsExactly(TEST_CLIENT_ID, TEST_CLIENT_ID + 1)

        client.stop()
        await()

        assertThat(serverDisconnectedClientIds).containsExactly(TEST_CLIENT_ID + 1)
    }

    @Ignore("This test is flaky, in isolation it runs fun, but it fails when the whole suite is executed")
    @Test
    fun shouldGetNotifiedAsClient_whenConnectedAndDisconnectedToServer() {

        await()

        assertThat(serverConnected).isTrue()

        server.close()
        await()

        assertThat(serverConnected).isFalse()
    }

    @Test
    fun shouldIndicateThatClientAndServerAreRunning() {
        await()

        assertThat(client.isRunning()).isTrue()
        assertThat(server.running).isTrue()

        server.close()
        client.stop()
        await()

        assertThat(client.isRunning()).isFalse()
        assertThat(server.running).isFalse()
    }

    // ------

    private fun setupClient(clientId: String) {
        simpleClientOutlet = CollectingOutlet<SimpleParam>(SimpleParam::class.java)
        complexClientOutlet = CollectingOutlet<ComplexParam>(ComplexParam::class.java)
        val clientOutletRegistry = OutletRegistry()
        clientOutletRegistry.register(simpleClientOutlet)
        clientOutletRegistry.register(complexClientOutlet)

        client = SocketOutletClient(clientId, clientOutletRegistry, ObjectMapper().initForSocketOutlet(), CustomLogger(CustomLogger.Level.VERBOSE, "client"))

        client.serverConnectedCallback = {
            serverConnected = true
        }

        client.serverDisconnectedCallback = {
            serverConnected = false
        }

        client.start(IP_ADDRESS, PORT)
    }

    private fun setupServer() {
        simpleServerOutlet = CollectingOutlet<SimpleParam>(SimpleParam::class.java)
        complexServerOutlet = CollectingOutlet<ComplexParam>(ComplexParam::class.java)
        val serverOutletRegistry = OutletRegistry()
        serverOutletRegistry.register(simpleServerOutlet)
        serverOutletRegistry.register(complexServerOutlet)

        server = SocketOutletServer(serverOutletRegistry, ObjectMapper().initForSocketOutlet(), CustomLogger(CustomLogger.Level.VERBOSE, "server"))

        server.clientConnectedCallback = {
            serverConnectedClientIds.add(it)
        }

        server.clientDisconnectedCallback = {
            serverDisconnectedClientIds.add(it)
        }

        server.open(PORT)
    }

    class CollectingOutlet<T>(t: Class<T>) : Outlet<T>(t) {

        val messageCalls = mutableListOf<Pair<T, Egress>>()

        override fun onMessage(sender: String, message: T, egress: Egress) {
            messageCalls.add(Pair(message, egress))
        }
    }

    private fun await() {
        Thread.sleep(TEST_DELAY)
    }

    data class SimpleParam(val simple: String)
    data class ComplexParam(val simple: String, val complex: List<SimpleParam>)
}
