package org.craftsmenlabs.socketoutlet.core

interface Egress {
    fun send(message : Any)
}
