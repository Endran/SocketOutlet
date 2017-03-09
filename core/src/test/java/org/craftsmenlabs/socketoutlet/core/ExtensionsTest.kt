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
