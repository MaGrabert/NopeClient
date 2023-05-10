package game

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Tournament() {
    var id: String = ""
    var date: String = ""
    var status: String = ""
    var size: String = ""
    var players: String = ""

    constructor(id: String, date: String, status: String, size: String, players: String) : this() {
        this.id = id
        this.date = date
        this.status = status
        this.size = size
        this.players = players
    }
    constructor(jsonObject: JSONObject) : this() {
        try{
            this.id = jsonObject.getString("id")
            val date: String = jsonObject.getString("createdAt")
            this.date = date.split("T")[0] + " " + date.split("T")[1].split(".")[0]
            this.status = jsonObject.getString("status")
            this.size = jsonObject.getString("currentSize")

            val players: JSONArray = jsonObject.getJSONArray("players")
            var playersString: String = ""

            for(idx in 0 until players.length()) {
                playersString += players.getJSONObject(idx).getString("username")
                if(idx < players.length() - 1) {
                    playersString += ", "
                }
            }

            this.players = playersString

        } catch(exception : JSONException) {
            println(exception.toString())
        }
    }
}