package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.SimpleThing

class SimpleThingOutlet : Outlet<SimpleThing>(SimpleThing::class.java) {
    override fun onMessage(message: SimpleThing, egress: Egress) {
        println("SimpleThingOutlet just received a SimpleThing: ${message.toString()}")
    }
}
