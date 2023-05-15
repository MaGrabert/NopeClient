package socket

import app.Profile
import game.Tournament
import game.TournamentInfo
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import tornadofx.asObservable
import java.net.URI
import java.util.Collections.singletonMap

/**
 * Creates, connects and disconnects a websocket.
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
object SocketHandler {
    private lateinit var socket: Socket
    var tableData = mutableListOf<Tournament>().asObservable()


    /**
     * Creates and connects the client socket with the server socket.
     */
    fun connect() {
        val opts = IO.Options.builder()
            .setAuth(singletonMap("token", Profile.token))
            .build()

        this.socket = IO.socket(URI.create("https://nope-server.azurewebsites.net/"), opts)

        this.socket.on(Socket.EVENT_CONNECT) {
            println("Socket is Connected")
        }

        this.socket.on(Socket.EVENT_CONNECT_ERROR) {
            println(it.joinToString())
        }

        this.socket.connect()
        refreshingTableData()
    }

    /**
     * Disconnects the client socket.
     */
    fun disconnect() {
        this.socket.disconnect()
        println("Socket is disconnected")
    }

    /**
     * Refreshes the table data of the current matches
     */
    private fun refreshingTableData() {
        this.socket.on("list:tournaments") { args ->
            tableData.clear()
            if (args[0] != null) {
                val jsonArray: JSONArray = args[0] as JSONArray
                for (index in 0 until jsonArray.length()) {
                    try {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(index)
                        val tmpTournament = Tournament(jsonObject)
                        tableData.add(tmpTournament)
                    } catch (e: JSONException) {
                        print("No data in getTournamentList!")
                    }
                }
            } else {
                println("No tournaments in list!")
            }
        }
    }

    /**
     * Sends data over the socket as String and return response.
     *
     * @return response The responding data as JSONObject
     */
    fun emit(event: String, data: String): JSONObject {
        var response = JSONObject()

        this.socket.emit(event, data, Ack { acknowledgement ->
            response = acknowledgement[0] as JSONObject
            println("Server response on event $event: $response")
        })

        return response
    }

    /**
     * Sends data over the socket as int.
     *
     * @return response The responding data as JSONObject
     */
    fun emit(event: String, data: Int): JSONObject {
        var response = JSONObject()

        this.socket.emit(event, data, Ack { acknowledgement ->
            response = acknowledgement[0] as JSONObject
            println("Server response on event $event: $response")
        })

        return response
    }

    /**
     * Sends data over the socket as JSONObject.
     *
     * @return response The responding data as JSONObject
     */
    fun emit(event: String): JSONObject {
        var response = JSONObject()

        this.socket.emit(event, Ack { acknowledgement ->
            response = acknowledgement[0] as JSONObject
            println("Server response on event $event: $response")
        })

        return response
    }

    /**
     * Receives data from the server for tournament info.
     */
    fun refreshingTournamentInfo() {
        this.socket.on("tournament:info") { args ->
            if (args[0] != null) {
                val jsonObject: JSONObject = args[0] as JSONObject
                TournamentInfo.createInfo(jsonObject)
            }
        }
    }

    /**
     * Receives data from the server for player info.
     */
    fun refreshingPlayerInfo() {
        this.socket.on("tournament:playerInfo") { args ->
            if (args[0] != null) {
                val jsonObject: JSONObject = args[0] as JSONObject
                TournamentInfo.createPlayerInfo(jsonObject)
            }
        }
    }
}