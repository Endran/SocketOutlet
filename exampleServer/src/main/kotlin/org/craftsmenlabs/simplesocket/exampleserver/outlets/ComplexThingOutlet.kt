package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.core.log.CustomLogger
import org.craftsmenlabs.simplesocket.exampleapi.ComplexThing

class ComplexThingOutlet : Outlet<ComplexThing>(ComplexThing::class.java) {

    private val logger = CustomLogger(CustomLogger.Level.INFO)

    override fun onMessage(message: ComplexThing, egress: Egress) {
        logger.i { "Just received a ComplexThing: ${message.toString()}" }
        egress.send(message.simpleThing)
        logger.i { "And replied with the SimpleThing: ${message.simpleThing.toString()}" }
    }
}
