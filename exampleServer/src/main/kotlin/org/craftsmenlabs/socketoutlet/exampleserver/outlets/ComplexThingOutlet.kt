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

package org.craftsmenlabs.socketoutlet.exampleserver.outlets

import org.craftsmenlabs.socketoutlet.core.Egress
import org.craftsmenlabs.socketoutlet.core.Outlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.exampleapi.ComplexThing

class ComplexThingOutlet : Outlet<ComplexThing>(ComplexThing::class.java) {

    private val logger = CustomLogger(CustomLogger.Level.INFO)

    override fun onMessage(message: ComplexThing, egress: Egress) {
        logger.i { "Just received a ComplexThing: ${message.toString()}" }
        egress.send(message.simpleThing)
        logger.i { "And replied with the SimpleThing: ${message.simpleThing.toString()}" }
    }
}
