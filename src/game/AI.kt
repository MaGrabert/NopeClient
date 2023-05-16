package game

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
}