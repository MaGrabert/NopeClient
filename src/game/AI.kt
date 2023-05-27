package game

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import socket.SocketHandler

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
    private var hand = ArrayList<Card>()
    private lateinit var topCard: Card

    fun setTopCard(topUpdate: JSONObject) {
        try {
            var colors: String = topUpdate.getString("color")
            var color1: String?
            var color2: String?

            var type: String = topUpdate.getString("type")
            var value: Int = topUpdate.getString("value").toInt()

            if(colors.split("-").size == 2) {
                color1 = colors.split("-")[0]
                color2 = colors.split("-")[1]
            } else {
                color1 = colors
                color2 = null
            }

            this.topCard = Card(CardType.getElement(type), CardColor.getElement(color1),CardColor.getElement(color2), CardValue.getElement(value))

        } catch(e: JSONException) {
            e.printStackTrace()
        }
    }

    fun fillHand(updateHand: JSONArray) {
        hand.clear()
        for(index in 0 until updateHand.length()) {
            var jsonCard: JSONObject = updateHand.getJSONObject(index)
            try {
                var colors: String = jsonCard.getString("color")
                var color1: String?
                var color2: String?

                var type: String = jsonCard.getString("type")
                var value: Int = jsonCard.getString("value").toInt()

                if(colors.split("-").size == 2) {
                    color1 = colors.split("-")[0]
                    color2 = colors.split("-")[1]
                    println("Before Card: color1: $color1 & color2: $color2")
                } else {
                    color1 = colors
                    color2 = null
                    println("Before color1: $color1")
                }

                val card = Card(CardType.getElement(type), CardColor.getElement(color1),CardColor.getElement(color2), CardValue.getElement(value))
                println("After Card: color1: ${card.cardColor1.toString()} & color2: ${card.cardColor2.toString()}")
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

        jsonObject.put("type", card.type.value)
        jsonObject.put("color", card.cardColor1?.value ?: "null")
        jsonObject.put("color", card.cardColor2?.value ?: "null")
        jsonObject.put("value", card.cardValue?.value ?: "null")

        return jsonObject
    }

    /**
     * Sends cards to the server.
     */
    fun sendCards(
        action: Action,
        card1: JSONObject?,
        card2: JSONObject?,
        card3: JSONObject?,
        reason: String = "Because I can!"
    ): JSONObject {
        var jsonObject = JSONObject()

        jsonObject.put("type", action.toString().lowercase())
        jsonObject.put("card1", card1)
        jsonObject.put("card2", card2)
        jsonObject.put("card3", card3)
        jsonObject.put("reason", reason)

        return jsonObject
    }

    fun makeMove() {
        if(this.topCard.type == CardType.NUMBER) {
            val tmpHand = ArrayList<Card>()

            for(card: Card in hand) {
                if(card.cardColor1 == topCard.cardColor1 || card.cardColor1 == topCard.cardColor2 || card.cardColor2 == topCard.cardColor1 || card.cardColor2 == topCard.cardColor2) {
                    if(tmpHand.size < (topCard.cardValue?.value ?: 0)) {
                        tmpHand.add(card)
                    }
                }
            }

            if(tmpHand.size < (topCard.cardValue?.value ?: 0)) { // If the AI can't throw cards
                val answer = sendCards(Action.NOPE,null,null,null, "I don't have the right cards")
                SocketHandler.emit("game:makeMove", answer)
            } else {
                if(tmpHand.size == 1) {
                    val answer = sendCards(Action.PUT,createJSONCard(tmpHand[0]),null,null, "I have the right cards")
                    println("Put card: ${tmpHand[0]}")
                    SocketHandler.emit("game:makeMove", answer)
                } else if(tmpHand.size == 2) {
                    val answer = sendCards(Action.PUT,createJSONCard(tmpHand[0]),createJSONCard(tmpHand[1]),null, "I have the right cards")
                    println("Put cards: ${tmpHand[0]} ${tmpHand[1]}")
                    SocketHandler.emit("game:makeMove", answer)
                } else if(tmpHand.size == 3) {
                    val answer = sendCards(Action.PUT,createJSONCard(tmpHand[0]),createJSONCard(tmpHand[1]),createJSONCard(tmpHand[2]), "I have the right cards")
                    println("Put cards: ${tmpHand[0]} ${tmpHand[1]} ${tmpHand[2]}")
                    SocketHandler.emit("game:makeMove", answer)
                }
            }
        } else if(this.topCard.type == CardType.JOKER) {

        } else if(this.topCard.type == CardType.REBOOT) {

        } else if(this.topCard.type == CardType.SEE_THROUGH) {

        } else if(this.topCard.type == CardType.SELECTION) {

        }
    }
}