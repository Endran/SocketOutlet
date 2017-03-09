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
import org.junit.Test
import java.net.Socket

class SocketOutletClientTest {

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
        socketOutletClient = SocketOutletClient(ipAddress, port, outletRegistry, objectMapper, logger)
    }

    @Test
    fun shouldThrow_whenSendIsCalledWhileNotRunning() {
        assertThatThrownBy({ socketOutletClient.send(testMessage) }).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun shouldTByRunning_whenMessageThreadIsSet() {
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
        socketOutletClient.start()
        assertThatThrownBy({ socketOutletClient.start() }).isInstanceOf(RuntimeException::class.java)
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

        socketOutletClient.start()

        object : Verifications() {
            init {
                messageThread.start()
            }
        }
    }
}
