package org.craftsmenlabs.simplesocket.exampleapi

import java.time.ZonedDateTime

data class ComplexThing(val someDate: ZonedDateTime, val simpleThing: SimpleThing, val simpleThingList: List<SimpleThing>)
