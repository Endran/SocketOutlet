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
