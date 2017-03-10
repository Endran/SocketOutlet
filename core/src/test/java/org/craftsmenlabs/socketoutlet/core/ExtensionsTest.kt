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

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ExtensionsTest {

    @Test
    fun shouldCreateCorrectObjecMapper() {
        val objectMapper = ObjectMapper().initForSocketOutlet()
        val expectedDateTime = ZonedDateTime.of(2000, 12, 31, 22, 33, 44, 55066077, ZoneId.of("UTC"))

        val textDateTime = objectMapper.writeValueAsString(expectedDateTime)
        assertThat(textDateTime).isEqualTo("\"2000-12-31T22:33:44.055066077Z[UTC]\"")

        val actualDateTime = objectMapper.readValue(textDateTime, ZonedDateTime::class.java)
        assertThat(actualDateTime).isEqualTo(expectedDateTime)
    }
}
