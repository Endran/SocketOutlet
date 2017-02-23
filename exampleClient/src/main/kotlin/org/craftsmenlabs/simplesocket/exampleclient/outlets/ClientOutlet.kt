package org.craftsmenlabs.simplesocket.exampleclient.outlets

import org.craftsmenlabs.simplesocket.core.Egress
import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.SimpleSharedThing

class ClientOutlet : Outlet<SimpleSharedThing>(SimpleSharedThing::class.java) {

    override fun onMessage(message: SimpleSharedThing, egress: Egress) {
        println("ClientOutlet just received ${message.toString()} as an object")
        egress.send(SimpleSharedThing("This a new instance", message.anInt * 2, message.optionalBoolean?.not()))
    }
}
