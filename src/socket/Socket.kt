package socket

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URI
import java.net.URISyntaxException
import javax.json.JsonArray
import javax.json.JsonObject

/**
 * Creates a websocket
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
object Socket {
    var mSocket: Socket? = null

    @Synchronized
    fun create() {
        try {
            val opts = IO.Options.builder().setTransports(arrayOf(io.socket.engineio.client.transports.WebSocket.NAME)).build()
            mSocket = IO.socket(URI.create("https://nope-server.azurewebsites.net/"), opts)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun connect() {
        val socket = mSocket!!.connect()
        socket.on(Socket.EVENT_CONNECT,{println("Connected")}).on(Socket.EVENT_CONNECT_ERROR, {println(it.joinToString())})
    }

    @Synchronized
    fun disconnect() {
        mSocket!!.disconnect()
    }

    fun emit(event: String, data: JsonObject) : Emitter? {
        return mSocket?.emit(event, data)
    }

    fun emit(event: String, data: JsonArray) : Emitter?{
        return mSocket?.emit(event, data)
    }

    fun emit(event: String, data: String) : Emitter? {
        return mSocket?.emit(event, data)
    }
}