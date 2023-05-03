package socket

import game.Tournament
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
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
                    val players: String = jsonObject.getString("players")

                    val tmpTournament = Tournament()
                    tmpTournament.id = id
                    tmpTournament.date = date
                    tmpTournament.size = currentSize.toInt()
                    tmpTournament.status = status
                    tmpTournament.players = players
                    tableData.add(tmpTournament)
                }
            } else {
                println("No tournaments in list!")
            }
        }
    }

    fun joinTournament(id: String) {
        this.socket.emit("tournament:join", id)
        this.socket.on("tournament:join") { args ->
            println("Try to join Tournament")
            if (args[0] != 0) {
                for(element in args) {
                    println(element)
                }
            } else {
                println("No args")
            }
        }
    }

    fun leaveTournament() {
        this.socket.emit("tournament:leave")
        this.socket.on("tournament:leave") { args ->
            println("Try to leave Tournament")
            if (args[0] != 0) {
                for(element in args) {
                    println(element)
                }
            } else {
                println("No args")
            }
        }
    }
}