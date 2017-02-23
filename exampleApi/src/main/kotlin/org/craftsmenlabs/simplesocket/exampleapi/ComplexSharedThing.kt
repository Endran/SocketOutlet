package org.craftsmenlabs.simplesocket.exampleapi

import java.time.ZonedDateTime

data class ComplexSharedThing(val someDate: ZonedDateTime, val simpleSharedThing: SimpleSharedThing, val simpleSharedThingList: List<SimpleSharedThing>)
