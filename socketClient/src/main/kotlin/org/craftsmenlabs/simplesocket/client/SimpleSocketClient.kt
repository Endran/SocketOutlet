package org.craftsmenlabs.simplesocket.client

import org.craftsmenlabs.simplesocket.core.SharedFactory
import org.craftsmenlabs.simplesocket.core.SocketMessage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SimpleSocketClient {

    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    @Throws(IOException::class)
    fun connectToServer(ipAddress: String, port: Int, s: SocketMessage) {

        try {
            Socket(ipAddress, port).use { socket ->
                reader = BufferedReader(InputStreamReader(socket.inputStream))
                writer = PrintWriter(socket.outputStream, true)

                // Consume the initial welcoming messages from the server
                for (i in 0..2) {
                    println(reader!!.readLine() + "\n")
                }
                val objectMapper = SharedFactory().objectMapper()
                writer!!.println(objectMapper.writeValueAsString(s))

                println(reader!!.readLine() + "\n")
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

}
