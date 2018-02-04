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

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class OutletTest {

    val testSender = "TEST_SENDER"
    val testOutlet = TestOutlet()

    @Test
    fun shouldCallConcreteImplementation_whenTypelessIsInvoked() {
        val expected = ErrorMessage("TEST_MESSAGE")
        testOutlet.onTypelessMessage(testSender, expected) { x ->

        }
        assertThat(testOutlet.sender).isSameAs(testSender)
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

        testOutlet.onTypelessMessage(testSender, message, egress)
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

        testOutlet.onTypelessMessage(testSender, someObject, egress)

        assertThat(egressMessage).isInstanceOf(ErrorMessage::class.java)
        assertThat((egressMessage as ErrorMessage).message).isEqualTo("An error occurred when invoking TestOutlet.onMessage(...)")
    }

    class TestOutlet : Outlet<ErrorMessage>(ErrorMessage::class.java) {
        var sender: String? = null
        var message: ErrorMessage? = null
        var egress: Egress? = null

        override fun onMessage(sender: String, message: ErrorMessage, egress: Egress) {
            this.sender = sender
            this.message = message
            this.egress = egress
        }
    }
}
