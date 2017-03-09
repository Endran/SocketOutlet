package org.craftsmenlabs.simplesocket.core

abstract class Outlet<T>(val clazz: Class<T>) {

    fun onTypelessMessage(message: Any, egress: ((Any) -> Unit)) {
        onMessage(message as T, object : Egress {
            override fun send(message: Any) {
                egress.invoke(message)
            }
        })
    }

    abstract fun onMessage(message: T, egress: Egress)
}
