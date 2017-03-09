package org.craftsmenlabs.simplesocket.core

class OutletRegistry {

    private val registry = mutableMapOf<Class<*>, Outlet<*>>()

    fun <T> register(outlet: Outlet<T>) {
        registry.put(outlet.clazz, outlet)
    }

    fun getClazz(clazzName: String): Class<*>? {
        return registry.keys.find { it.name == clazzName }
    }

    fun getOutlet(clazzName: String): Outlet<*>? {
        val clazz = getClazz(clazzName)
        return registry[clazz]
    }
}
