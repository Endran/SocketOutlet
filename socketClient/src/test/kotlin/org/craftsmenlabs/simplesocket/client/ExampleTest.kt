/*
 * Copyright 2017 David Hardy
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

package org.craftsmenlabs.simplesocket.client

import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.simplesocket.core.Example
import org.junit.Test

class ExampleTest {

    @Tested
    lateinit var example: Example

    private val TEST = "TEST"

    @Test
    fun shouldReturnName() {
        val response = example.hello(TEST)
        assertThat(response).contains(TEST)
    }
}