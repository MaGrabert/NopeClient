package socket

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI

/**
 * Creates a websocket
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
object SocketHandler {
    var token: String = ""
    private val opts = IO.Options()
    private lateinit var socket: Socket
    fun connect() {
        println("Token: $token")
        this.opts.query = "token=$token"
        this.socket = IO.socket(URI.create("https://nope-server.azurewebsites.net/"), opts)

        this.socket.on(Socket.EVENT_CONNECT) {
            println("Connected")
        }

        this.socket.on(Socket.EVENT_CONNECT_ERROR) {
            println(it.joinToString())
        }

        this.socket.connect()
    }

    fun disconnect() {
        this.socket.disconnect()
        println("Disconnected")
    }
}