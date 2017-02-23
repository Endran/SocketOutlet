package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.ComplexThing

class ComplexThingOutlet : Outlet<ComplexThing>(ComplexThing::class.java) {
    override fun onMessage(message: ComplexThing, egress: Egress) {
        println("ComplexThingOutlet just received a ComplexThing: ${message.toString()}")
        println("Lets reply with simpleThing!")
        egress.send(message.simpleThing)
    }
}
