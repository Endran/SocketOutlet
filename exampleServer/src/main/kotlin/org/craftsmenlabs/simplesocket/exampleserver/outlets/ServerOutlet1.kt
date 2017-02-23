package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.SimpleSharedThing

class ServerOutlet1 : Outlet<SimpleSharedThing>(SimpleSharedThing::class.java) {
    override fun onMessage(message: SimpleSharedThing, egress: Egress) {
        println("ServerOutlet1 just received a SimpleSharedThing: ${message.toString()}")
    }
}
