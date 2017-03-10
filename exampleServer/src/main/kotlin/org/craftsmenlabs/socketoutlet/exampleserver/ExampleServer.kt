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

package org.craftsmenlabs.socketoutlet.exampleserver

import org.craftsmenlabs.socketoutlet.core.OutletRegistry
import org.craftsmenlabs.socketoutlet.exampleserver.outlets.ComplexThingOutlet
import org.craftsmenlabs.socketoutlet.exampleserver.outlets.SimpleThingOutlet
import org.craftsmenlabs.socketoutlet.server.SocketOutletServer

class ExampleServer {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val port = args.getOrElse(0, { "6000" }).toInt()
            ExampleServer().execute(port)
        }
    }

    fun execute(port: Int) {
        val outletRegistry = OutletRegistry()
        outletRegistry.register(SimpleThingOutlet())
        outletRegistry.register(ComplexThingOutlet())

        val server = SocketOutletServer(outletRegistry)

        server.clientConnectedCallback = {
            println("Client with id $it connected")
        }

        server.clientDisconnectedCallback = {
            println("Client with id $it disconnected")
        }

        server.open(port)

        while (server.running){
            // Do some otherstuff it you want
            Thread.sleep(1000)
        }

        server.close()
    }
}
