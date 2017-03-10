package org.craftsmenlabs.socketoutlet.server

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
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.ServerSocket
import java.net.Socket

class SocketOutletServerTest {

    @Injectable
    private lateinit var outletRegistry: OutletRegistry

    @Injectable
    private lateinit var objectMapper: ObjectMapper

    @Injectable
    private lateinit var logger: SLogger

    private lateinit var server: SocketOutletServer

    @Mocked
    private lateinit var serverSocket: ServerSocket

    @Mocked
    private lateinit var messageThread: MessageThread

    private val port = 111

    @Before
    fun setUp() {
        server = SocketOutletServer(outletRegistry, objectMapper, logger)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun shouldOpenPort_whenStarted(@Mocked thread: Thread) {
        val runtimeException = RuntimeException()

        object : Expectations() {
            init {
                ServerSocket(port)
                result = runtimeException
            }
        }


        assertThatThrownBy({
            server.open(port)
            Captors.captureThreadRunnable().run()
        }).isEqualTo(runtimeException)
    }

    @Test
    fun shouldThrow_WhenOpenedWhileRunning() {
        server.running = true
        assertThatThrownBy({ server.open(port) }).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun shouldCreateAndStartMessageThread_whenSocketAccepts(
            @Injectable serverSocket: ServerSocket,
            @Injectable socket: Socket
    ) {
        val runtimeException = RuntimeException()

        object : Expectations() {
            init {
                serverSocket.accept()
                result = socket
                result = socket
                result = runtimeException

                MessageThread(objectMapper, outletRegistry, socket, logger)
                result = messageThread
                result = messageThread
            }
        }

        server.running = true

        assertThatThrownBy({ server.serverLoop(serverSocket) }).isEqualTo(runtimeException)

        object : Verifications() {
            init {
                messageThread.start()
                messageThread.start()
            }
        }
    }

    @Test
    fun shouldAddMessageThreadToList_whenSocketAndThreadRuns(
            @Injectable serverSocket: ServerSocket,
            @Injectable socket: Socket) {

        object : Expectations() {
            init {
                messageThread.isRunning()
                result = true
                result = false
                result = true
            }
        }

        shouldCreateAndStartMessageThread_whenSocketAccepts(serverSocket, socket)

        assertThat(server.threadList).hasSize(1)
    }

    @Test
    fun shouldClearMessageThread_whenServerIsClosed() {
        server.running = true
        server.threadList.add(messageThread)
        server.threadList.add(messageThread)

        server.close()

        object : Verifications() {
            init {
                messageThread.interrupt()
                times = 2
            }
        }

        assertThat(server.running).isFalse()
        assertThat(server.threadList).isEmpty()
    }
}
