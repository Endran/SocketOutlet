/*
 * Copyright (c) 2017 David Hardy.
 * Copyright (c) 2017 Craftsmenlabs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.craftsmenlabs.socketoutlet.core

import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class OutletRegistryTest {

    @Tested
    lateinit var outletRegistry: OutletRegistry

    private var outlet1 = TestOutlet<TestData1>(TestData1::class.java)
    private var outlet2 = TestOutlet<TestData2>(TestData2::class.java)

    data class TestData1(val message: String)
    data class TestData2(val message: String)

    @Test
    fun shouldGetClassByName() {
        val expectedClazz1 = TestData1::class.java
        val expectedClazz2 = TestData2::class.java

        outletRegistry.register(outlet1)
        outletRegistry.register(outlet2)

        val actualClazz1 = outletRegistry.getClazz(expectedClazz1.simpleName)
        val actualClazz2 = outletRegistry.getClazz(expectedClazz2.simpleName)

        assertThat(actualClazz1).isSameAs(expectedClazz1)
        assertThat(actualClazz2).isSameAs(expectedClazz2)
    }

    @Test
    fun shouldGetOutletByName() {
        val expectedClazz1 = TestData1::class.java
        val expectedClazz2 = TestData2::class.java

        outletRegistry.register(outlet1)
        outletRegistry.register(outlet2)

        val actualOutlet1 = outletRegistry.getOutlet(expectedClazz1.simpleName)
        val actualOutlet2 = outletRegistry.getOutlet(expectedClazz2.simpleName)

        assertThat(actualOutlet1).isSameAs(outlet1)
        assertThat(actualOutlet2).isSameAs(outlet2)
    }

    class TestOutlet<T>(clazz: Class<T>) : Outlet<T>(clazz) {
        var message: T? = null
        override fun onMessage(message: T, egress: Egress) {
            this.message = message
        }
    }
}
