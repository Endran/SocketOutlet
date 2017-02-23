package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.SimpleSharedThing

class ExampleOutlet1 : Outlet<SimpleSharedThing>() {

    override fun onMessage(message: SimpleSharedThing) {
        println("ExampleOutlet1 just received ${message.toString()} as an object")
    }
}
