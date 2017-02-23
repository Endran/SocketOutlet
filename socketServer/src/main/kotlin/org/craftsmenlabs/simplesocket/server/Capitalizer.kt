package org.craftsmenlabs.simplesocket.server

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Capitalizer(private val socket: Socket, private val clientNumber: Int) : Thread() {

    init {
        log("New connection with client# $clientNumber at $socket")
    }

    /**
     * Services this thread's client by first sending the
     * client a welcome message then repeatedly reading strings
     * and sending back the capitalized version of the string.
     */
    override fun run() {
        try {

            // Decorate the streams so we can send characters
            // and not just bytes.  Ensure output is flushed
            // after every newline.
            val `in` = BufferedReader(
                    InputStreamReader(socket.inputStream))
            val out = PrintWriter(socket.outputStream, true)

            // Send a welcome message to the client.
            out.println("Hello, you are client #$clientNumber.")
            out.println("Enter a line with only a period to quit\n")

            // Get messages from the client, line by line; return them
            // capitalized
            while (true) {
                val input = `in`.readLine()
                if (input == null || input == ".") {
                    break
                }
                val message = input.toUpperCase()
                out.println(message)
            }
        } catch (e: IOException) {
            log("Error handling client# $clientNumber: $e")
        } finally {
            try {
                socket.close()
            } catch (e: IOException) {
                log("Couldn't close a socket, what's going on?")
            }

            log("Connection with client# $clientNumber closed")
        }
    }

    /**
     * Logs a simple message.  In this case we just write the
     * message to the server applications standard output.
     */
    private fun log(message: String) {
        System.out.println(message)
    }
}
