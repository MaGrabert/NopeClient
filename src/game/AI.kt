package game

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * All actions that the AI could do.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class Action {
    TAKE, PUT, NOPE
}

/**
 * Represents the AI that should play Nope!.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
object AI {
    var hand = ArrayList<Card>()
    var topCard: Card? = null
    fun fillHand(updateHand: JSONArray) {
        hand.clear()
        for(index in 0 until updateHand.length()) {
            var jsonCard: JSONObject = updateHand.getJSONObject(index)
            try {
                var color: String = jsonCard.getString("color")
                var type: String = jsonCard.getString("type")
                var value: Int = jsonCard.getString("value").toInt()

                val card = Card(CardType.getElement(type), CardColor.getElement(color), CardValue.getElement(value))
                hand.add(card)

            } catch(e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Creates a card as JSONObject.
     *
     * @return jsonObject The card as JSONObject
     */
    fun createJSONCard(card: Card): JSONObject {
        var jsonObject = JSONObject()

        jsonObject.put("type", card.type.toString().lowercase())
        jsonObject.put("color", card.cardColor.toString().lowercase())
        jsonObject.put("value", card.cardValue.toString().lowercase())

        return jsonObject
    }

    /**
     * Sends cards to the server.
     */
    fun sendCards(
        action: Action,
        card1: JSONObject,
        card2: JSONObject,
        card3: JSONObject,
        reason: String = "Because I can!"
    ) {
        var jsonObject = JSONObject()

        jsonObject.put("type", action.toString().lowercase())
        jsonObject.put("card1", card1)
        jsonObject.put("card2", card2)
        jsonObject.put("card3", card3)
        jsonObject.put("reason", reason)
    }

    fun makeMove() {
        

        println("The current move ...")
    }
}