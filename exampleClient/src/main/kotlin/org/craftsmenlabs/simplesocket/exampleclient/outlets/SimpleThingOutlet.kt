package org.craftsmenlabs.simplesocket.exampleclient.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.SimpleThing

class SimpleThingOutlet : Outlet<SimpleThing>(SimpleThing::class.java) {

    override fun onMessage(message: SimpleThing, egress: Egress) {
        println("SimpleThingOutlet just received ${message.toString()} as an object")
        egress.send(SimpleThing("This a new instance", message.anInt * 2, message.optionalBoolean?.not()))
    }
}
