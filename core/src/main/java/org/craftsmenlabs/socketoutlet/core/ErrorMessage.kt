package org.craftsmenlabs.socketoutlet.core

data class ErrorMessage(val message: String, val throwable: Throwable? = null)
