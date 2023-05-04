package socket

import game.Tournament
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
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
            tableData.clear()
            if(args[0] != null) {
                val jsonArray: JSONArray = args[0] as JSONArray
                for(index in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(index)
                    val id: String = jsonObject.getString("id")
                    val date: String = jsonObject.getString("createdAt")
                    val status: String = jsonObject.getString("status")
                    val currentSize: String = jsonObject.getString("currentSize")
                    val players: JSONArray = jsonObject.getJSONArray("players")
                    var playersString: String = ""

                    for(idx in 0 until players.length()) {
                        playersString += players.getJSONObject(idx).getString("username")
                        if(idx < players.length() - 1) {
                            playersString += ", "
                        }
                    }

                    val tmpTournament = Tournament()

                    tmpTournament.id = id
                    tmpTournament.date = date.split("T")[0] + " " + date.split("T")[1].split(".")[0]
                    tmpTournament.size = currentSize.toInt()
                    tmpTournament.status = status
                    tmpTournament.players = playersString
                    tableData.add(tmpTournament)
                }
            } else {
                println("No tournaments in list!")
            }
        }
    }

    fun joinTournament(id: String) {
        this.socket.emit("tournament:join", id, Ack { response ->
            val msg = response as Array
            for(element in msg)
                println(element)
        })
    }

    fun leaveTournament() {
        this.socket.emit("tournament:leave", Ack { response ->
            val msg = response as Array
            for(element in msg)
                println(element)
        })
    }

    fun createTurnament(numberOfMatches: String) {
        this.socket.emit("tournament:create", numberOfMatches.toInt(), Ack { response ->
            val msg = response as Array
            for(element in msg)
                println(element)
        })
    }
}