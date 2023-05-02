package socket

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URI
import java.net.http.WebSocket
import java.util.Collections.singletonMap

/**
 * Creates, connects and disconnects a websocket.
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
object SocketHandler {
    var token: String = ""
    private lateinit var socket: Socket

    fun connect() {
        val opts = IO.Options.builder()
            .setAuth(singletonMap("token", "$token"))
            .build()

        this.socket = IO.socket(URI.create("https://nope-server.azurewebsites.net/"), opts)

        this.socket.on(Socket.EVENT_CONNECT) {
            println("Connected")
        }

        this.socket.on(Socket.EVENT_CONNECT_ERROR) {
            println(it.joinToString())
        }

        this.socket.connect()
        getTournamentList()
    }

    fun disconnect() {
        this.socket.disconnect()
        println("Disconnected")
    }

    fun getTournamentList() {
        this.socket.on("list:tournaments") { args ->
            if(args[0] != null) {
                for(item in args) {
                    println(item)
                }
            } else {
                println("No tournaments in list!")
            }
        }
    }
}