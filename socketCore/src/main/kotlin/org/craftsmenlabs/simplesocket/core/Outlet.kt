package org.craftsmenlabs.simplesocket.core

abstract class Outlet<in T> {

    fun onTypelessMessage(message: Any) {
        onMessage(message as T)
    }

    abstract fun onMessage(message: T)
}
