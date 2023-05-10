package game

import app.Profile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object TournamentInfo {
    var message: String = ""
    var id: String = ""
    var size: String = ""
    var status: String = ""
    var matches: String = ""
    var players: String = ""
    var host: Player = Player()
    var winner: Player = Player()

    fun createInfo(jsonObject: JSONObject) {
        try {
            this.message = jsonObject.getString("message")
            this.status = jsonObject.getString("status")

            val winnerObject = jsonObject.getJSONObject("winner")
            if (winnerObject != null) {
                this.winner = Player(winnerObject.getString("id"), winnerObject.getString("username"), winnerObject.getString("score"))
            }

            val host = jsonObject.getJSONObject("host")
            this.host = Player(host.getString("id"), host.getString("username"))

        } catch(exception : JSONException) {
            println(exception.toString())
        }
    }

    fun createHostInfo(jsonObject: JSONObject) {
        try {
            this.id = jsonObject.getJSONObject("data").getString("currentSize")
            this.size = jsonObject.getJSONObject("data").getString("currentSize")
            this.players = Profile.userName
            this.status = "WAITING_FOR_MORE_PLAYERS"

        } catch(exception : JSONException) {
            println(exception.toString())
        }
    }

    fun createJoinInfo(id: String, status: String, size: String, players: String) {
        this.id = id
        this.status = status
        this.size = size
        this.players = players
    }

    fun createPlayerInfo(jsonObject: JSONObject) {
        try {
            this.message = jsonObject.getString("message")

            val players: JSONArray = jsonObject.getJSONArray("players")
            var playersString: String = ""

            for(idx in 0 until players.length()) {
                playersString += players.getJSONObject(idx).getString("username")
                if(idx < players.length() - 1) {
                    playersString += ", "
                }
            }

            this.players = playersString

        } catch(exception: JSONException) {
            exception.toString()
        }
    }
}