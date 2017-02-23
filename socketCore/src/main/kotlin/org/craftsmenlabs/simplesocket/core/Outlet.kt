package org.craftsmenlabs.simplesocket.core

interface Outlet<in T> {
    fun onMessage(message: T)
}
