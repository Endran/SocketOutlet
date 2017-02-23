package org.craftsmenlabs.simplesocket.core

import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class OutletRegistryTest {

    @Tested
    lateinit var outletRegistry: OutletRegistry

    private var outlet1 = TestOutlet<TestData1>()
    private var outlet2 = TestOutlet<TestData2>()

    data class TestData1(val message: String)
    data class TestData2(val message: String)

    @Test
    fun shouldGetClassByName() {
        val expectedClazz1 = TestData1::class.java
        val expectedClazz2 = TestData2::class.java

        outletRegistry.register(expectedClazz1, outlet1)
        outletRegistry.register(expectedClazz2, outlet2)

        val actualClazz1 = outletRegistry.getClazz(expectedClazz1.name)
        val actualClazz2 = outletRegistry.getClazz(expectedClazz2.name)

        assertThat(actualClazz1).isSameAs(expectedClazz1)
        assertThat(actualClazz2).isSameAs(expectedClazz2)
    }

    @Test
    fun shouldGetOutletByName() {
        val expectedClazz1 = TestData1::class.java
        val expectedClazz2 = TestData2::class.java

        outletRegistry.register(expectedClazz1, outlet1)
        outletRegistry.register(expectedClazz2, outlet2)

        val actualOutlet1 = outletRegistry.getOutlet(expectedClazz1.name)
        val actualOutlet2 = outletRegistry.getOutlet(expectedClazz2.name)

        assertThat(actualOutlet1).isSameAs(outlet1)
        assertThat(actualOutlet2).isSameAs(outlet2)
    }

    class TestOutlet<T> : Outlet<T>() {
        var message: T? = null
        override fun onMessage(message: T) {
            this.message = message
        }
    }
}
