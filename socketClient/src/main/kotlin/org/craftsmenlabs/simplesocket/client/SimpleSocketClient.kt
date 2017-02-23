package org.craftsmenlabs.simplesocket.client

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SimpleSocketClient {

    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    @Throws(IOException::class)
    fun connectToServer(ipAddress: String, port: Int) {

        try {
            Socket(ipAddress, port).use { socket ->
                reader = BufferedReader(InputStreamReader(socket.inputStream))
                writer = PrintWriter(socket.outputStream, true)

                // Consume the initial welcoming messages from the server
                for (i in 0..2) {
                    println(reader!!.readLine() + "\n")
                }

                writer!!.println("Hello from client")

                println(reader!!.readLine() + "\n")
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

}
