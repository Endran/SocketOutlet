/*
 * Copyright 2017 David Hardy
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

package org.craftsmenlabs.simplesocket.exampleclient

import org.craftsmenlabs.simplesocket.client.SimpleSocketClient
import org.craftsmenlabs.simplesocket.core.SharedFactory
import org.craftsmenlabs.simplesocket.core.SocketMessage
import org.craftsmenlabs.simplesocket.exampleapi.ComplexSharedThing
import org.craftsmenlabs.simplesocket.exampleapi.SimpleSharedThing
import java.time.ZonedDateTime

class ExampleClient {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val ipAddress = args.getOrElse(0, { "127.0.0.1" })
            val port = args.getOrElse(0, { "6000" }).toInt()
            ExampleClient().run(ipAddress, port)
        }
    }

    fun run(ipAddress: String, port: Int) {
        val client = SimpleSocketClient()

        val objectMapper = SharedFactory().objectMapper()

        val simpleSharedThing1 = SimpleSharedThing("one", 1, true)
        val simpleSharedThing2 = SimpleSharedThing("two", 1, false)
        val simpleSharedThing3 = SimpleSharedThing("three", 3)
        val complexSharedThing = ComplexSharedThing(ZonedDateTime.now(), simpleSharedThing1, listOf(simpleSharedThing2, simpleSharedThing3))
        val socketMessage = SocketMessage(complexSharedThing.javaClass.name, objectMapper.writeValueAsString(complexSharedThing))

        client.connectToServer(ipAddress, port, socketMessage)
    }
}
