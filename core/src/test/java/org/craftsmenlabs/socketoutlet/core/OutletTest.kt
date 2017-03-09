package org.craftsmenlabs.socketoutlet.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class OutletTest {

    val testOutlet = TestOutlet()

    @Test
    fun shouldCallConcreteImplementation_whenTypelessIsInvoked() {
        val expected = ErrorMessage("TEST_MESSAGE")
        testOutlet.onTypelessMessage(expected) { x ->

        }
        assertThat(testOutlet.message).isEqualTo(expected)
    }

    @Test
    fun shouldEgressWithMessage_whenEggresIsInvoked() {
        val message = ErrorMessage("TEST_MESSAGE")
        val egressExpected = Object()

        var egressActual: Any? = null
        val egress: (Any) -> Unit = { x ->
            egressActual = x
        }

        testOutlet.onTypelessMessage(message, egress)
        testOutlet.egress?.send(egressExpected)

        assertThat(egressActual).isSameAs(egressExpected)
    }

    @Test
    fun shouldReturnError_whenClassCannotBeCasted() {
        val someObject = Object()

        var egressMessage: Any? = null
        val egress: (Any) -> Unit = { x ->
            egressMessage = x
        }

        testOutlet.onTypelessMessage(someObject, egress)

        assertThat(egressMessage).isInstanceOf(ErrorMessage::class.java)
        assertThat((egressMessage as ErrorMessage).message).isEqualTo("An error occurred when invoking TestOutlet.onMessage(...)")
    }

    class TestOutlet : Outlet<ErrorMessage>(ErrorMessage::class.java) {
        var message: ErrorMessage? = null
        var egress: Egress? = null

        override fun onMessage(message: ErrorMessage, egress: Egress) {
            this.message = message
            this.egress = egress
        }
    }
}
