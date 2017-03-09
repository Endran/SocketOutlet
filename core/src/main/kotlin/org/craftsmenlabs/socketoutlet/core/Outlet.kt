package org.craftsmenlabs.socketoutlet.core

abstract class Outlet<T>(val clazz: Class<T>) {

    @Suppress("UNCHECKED_CAST")
    fun onTypelessMessage(typelessObject: Any, egress: ((Any) -> Unit)) {
        try {
            onMessage(typelessObject as T, object : Egress {
                override fun send(message: Any) {
                    egress.invoke(message)
                }
            })
        } catch (t: Throwable) {
            egress.invoke(ErrorMessage("An error occurred when invoking ${this.javaClass.simpleName}.onMessage(...)", t))
        }
    }

    protected abstract fun onMessage(message: T, egress: Egress)
}
