package socket

import game.Tournament
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
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
            if(args[0] != null) {
                this.tableData.clear()
                for (tournament in args) {
                    val date = tournament.toString().split("createdAt\":\"")[1].split("T")[0]
                    val time = tournament.toString().split("createdAt\":\"")[1].split("T")[1].split(".")[0]
                    var players: String = ""
                    for(player in tournament.toString().split("players\":[")[1].split("\"username\":\"").iterator()) {
                        players += player.split("\"")[0] + " "
                    }
                    var tempTournament = Tournament()
                    tempTournament.id = tournament.toString().split("id\":\"")[1].split("\",\"")[0]
                    tempTournament.date = "$date $time"
                    tempTournament.size = tournament.toString().split("currentSize\":")[1].replace("}]", "").toInt()
                    tempTournament.status = tournament.toString().split("status\":\"")[1].split("\",\"")[0]
                    tempTournament.players = players
                    this.tableData.add(tempTournament)
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