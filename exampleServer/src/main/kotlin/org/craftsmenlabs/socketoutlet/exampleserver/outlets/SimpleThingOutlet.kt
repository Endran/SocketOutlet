package org.craftsmenlabs.socketoutlet.exampleserver.outlets

import org.craftsmenlabs.socketoutlet.core.Egress
import org.craftsmenlabs.socketoutlet.core.Outlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.exampleapi.SimpleThing

class SimpleThingOutlet : Outlet<SimpleThing>(SimpleThing::class.java) {

    private val logger = CustomLogger(CustomLogger.Level.INFO)

    override fun onMessage(message: SimpleThing, egress: Egress) {
        logger.i { "Just received a SimpleThing: ${message.toString()}" }
    }
}
