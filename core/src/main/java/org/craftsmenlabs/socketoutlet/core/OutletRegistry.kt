package org.craftsmenlabs.socketoutlet.core

class OutletRegistry {

    private val registry = mutableMapOf<String, Pair<Class<*>, Outlet<*>>>()

    fun <T> register(outlet: Outlet<T>) {
        registry.put(outlet.clazz.simpleName, Pair(outlet.clazz, outlet))
    }

    fun getClazz(simpleClazzName: String): Class<*>? {
        return registry[simpleClazzName]?.first
    }

    fun getOutlet(simpleClazzName: String): Outlet<*>? {
        return registry[simpleClazzName]?.second
    }
}
