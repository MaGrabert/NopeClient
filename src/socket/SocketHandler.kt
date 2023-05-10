package socket

import game.Tournament
import game.TournamentInfo
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import tornadofx.asObservable
import java.net.URI
import java.util.Collections.singletonMap
import kotlin.reflect.typeOf

/**
 * Creates, connects and disconnects a websocket.
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
object SocketHandler {
    var token: String = ""
    private lateinit var socket: Socket
    var tableData = mutableListOf<Tournament>().asObservable()
    var beInTournament: Boolean = false

    fun connect() {
        val opts = IO.Options.builder()
            .setAuth(singletonMap("token", token))
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

    fun disconnect() {
        this.socket.disconnect()
        println("Socket is disconnected")
    }

    private fun refreshingTableData() {
        this.socket.on("list:tournaments") { args ->
            tableData.clear()
            if(args[0] != null) {
                val jsonArray: JSONArray = args[0] as JSONArray
                for(index in 0 until jsonArray.length()) {
                    try {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(index)
                        val tmpTournament = Tournament(jsonObject)
                        tableData.add(tmpTournament)
                    } catch (e :JSONException) {
                        print("No data in getTournamentList!")
                    }
                }
            } else {
                println("No tournaments in list!")
            }
        }
    }

    fun emit(event: String, data: String): JSONObject {
        var response = JSONObject()

        this.socket.emit(event, data, Ack { acknowledgement ->
            response = acknowledgement[0] as JSONObject
            println("Server response on event $event: $response")
        })

        return response
    }

    fun emit(event: String, data: Int): JSONObject {
        var response = JSONObject()

        this.socket.emit(event, data, Ack { acknowledgement ->
            response = acknowledgement[0] as JSONObject
            println("Server response on event $event: $response")
        })

        return response
    }

    fun emit(event: String): JSONObject {
        var response = JSONObject()

        this.socket.emit(event, Ack { acknowledgement ->
            response = acknowledgement[0] as JSONObject
            println("Server response on event $event: $response")
        })

        return response
    }

    fun on(event: String): JSONObject {
        var response = JSONObject()

        this.socket.on(event) { args ->
            if(args[0] != null) {
                println("Server response on event $event")
                response = args[0] as JSONObject
            } else {
                println("No data received for event $event!")
            }
        }

        return response
    }

    fun getTournamentInfo() {
        this.socket.on("tournament:playerInfo") { args ->
            if(args[0] != null) {
                try {
                    val jsonObject: JSONObject = args[0] as JSONObject
                    val message: String = jsonObject.getString("message")
                    val id: String = jsonObject.getString("tournamentId")
                    val size: String = jsonObject.getString("currentSize")
                    val matches: String = jsonObject.getString("bestOf")
                    val players: JSONArray = jsonObject.getJSONArray("players")
                    var playersString: String = ""

                    for (idx in 0 until players.length()) {
                        playersString += players.getJSONObject(idx).getString("username")
                        if (idx < players.length() - 1) {
                            playersString += ", "
                        }
                    }
                    TournamentInfo.matches = matches
                    TournamentInfo.id = id
                    TournamentInfo.size = size
                    TournamentInfo.players = playersString
                    TournamentInfo.message = message
                } catch (e :JSONException) {

                }
            }
        }
    }
}