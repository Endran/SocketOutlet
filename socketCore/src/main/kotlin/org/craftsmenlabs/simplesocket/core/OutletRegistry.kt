package org.craftsmenlabs.simplesocket.core

class OutletRegistry {

    val registry = mutableMapOf<Class<*>, Outlet<*>>()

    fun <T> register(clazz: Class<T>, outlet: Outlet<T>) {
        registry.put(clazz, outlet)
    }

    fun getClazz(clazzName: String): Class<*>? {
        return registry.keys.find { it.name == clazzName }
    }

    fun getOutlet(clazzName: String): Outlet<*>? {
        val clazz = getClazz(clazzName)
        return registry[clazz]
    }
}
