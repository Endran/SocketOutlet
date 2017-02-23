package org.craftsmenlabs.simplesocket.exampleserver.outlets

import org.craftsmenlabs.simplesocket.core.Outlet
import org.craftsmenlabs.simplesocket.exampleapi.ComplexSharedThing

class ExampleOutlet2 : Outlet<ComplexSharedThing>() {

    override fun onMessage(message: ComplexSharedThing) {
        println("ExampleOutlet2 just received ${message.toString()} as an object")
    }
}
