package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.ComplexSharedThing

class ServerOutlet2 : Outlet<ComplexSharedThing>(ComplexSharedThing::class.java) {
    override fun onMessage(message: ComplexSharedThing, egress: Egress) {
        println("ServerOutlet2 just received a ComplexSharedThing: ${message.toString()}")
        println("Lets reply with simpleSharedThing!")
        egress.send(message.simpleSharedThing)
    }
}
