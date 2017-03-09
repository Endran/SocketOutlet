package org.craftsmenlabs.socketoutlet.exampleclient.outlets

import org.craftsmenlabs.socketoutlet.core.Egress
import org.craftsmenlabs.socketoutlet.core.Outlet
import org.craftsmenlabs.socketoutlet.core.log.CustomLogger
import org.craftsmenlabs.socketoutlet.exampleapi.SimpleThing

class SimpleThingOutlet : Outlet<SimpleThing>(SimpleThing::class.java) {

    private val logger = CustomLogger(CustomLogger.Level.INFO)

    override fun onMessage(message: SimpleThing, egress: Egress) {
        logger.i { "Just received a SimpleThing: ${message.toString()}" }
        val reply = SimpleThing("This a new instance", message.anInt * 2, message.optionalBoolean?.not())
        egress.send(reply)
        logger.i { "And replied with something custom: ${reply.toString()}" }
    }
}
