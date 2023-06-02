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
            val colors: String = topUpdate.getString("color")
            val color1: String?
            val color2: String?

            val type: String = topUpdate.getString("type")
            val value: Int = topUpdate.getString("value").toInt()

            if(colors.split("-").size == 2) {
                color1 = colors.split("-")[0]
                color2 = colors.split("-")[1]
            } else {
                color1 = colors
                color2 = null
            }

            if(type == "number") {
                this.topCard = Card(CardType.getElement(type), CardColor.getElement(color1),CardColor.getElement(color2), CardValue.getElement(value), null, null, null)
            }

        } catch(e: JSONException) {
            e.printStackTrace()
        }
    }

    fun fillHand(updateHand: JSONArray) {
        hand.clear()
        for(index in 0 until updateHand.length()) {
            val jsonCard: JSONObject = updateHand.getJSONObject(index)
            try {
                val colors: String = jsonCard.getString("color")
                var color1: String?
                var color2: String?

                val type: String = jsonCard.getString("type")
                val value: Int = jsonCard.getString("value").toInt()

                if(colors.split("-").size == 2) {
                    color1 = colors.split("-")[0]
                    color2 = colors.split("-")[1]
                } else {
                    color1 = colors
                    color2 = null
                }

                if(type == "number") {
                    val card = Card(CardType.getElement(type), CardColor.getElement(color1),CardColor.getElement(color2), CardValue.getElement(value), null, null, null)
                    hand.add(card)
                }

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
    private fun createJSONCard(card: Card): JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("type", card.type.value)

        if(card.cardColor1?.value != null && card.cardColor2?.value != null) {
            jsonObject.put("color", card.cardColor1.value + "-" + card.cardColor2.value)
        } else if(card.cardColor1?.value != null && card.cardColor2?.value == null){
            jsonObject.put("color", card.cardColor1.value)
        } else {
            jsonObject.put("color", JSONObject.NULL)
        }

        jsonObject.put("value", card.cardValue?.value ?: JSONObject.NULL)
        jsonObject.put("select", card.select?.value ?: JSONObject.NULL)
        jsonObject.put("selectValue", card.selectValue?.value ?: JSONObject.NULL)
        jsonObject.put("selectedColor", card.selectedColor?.value ?: JSONObject.NULL)

        return jsonObject
    }

    /**
     * Sends cards to the server.
     */
    private fun sendCards(
        action: Action,
        card1: JSONObject?,
        card2: JSONObject?,
        card3: JSONObject?,
        reason: String = "Because I can!"
    ): JSONObject {
        var jsonObject = JSONObject()

        jsonObject.put("type", action.toString().lowercase())

        if(card1 != null) {
            jsonObject.put("card1", card1)
        } else {
            jsonObject.put("card1", JSONObject.NULL)
        }

        if(card2 != null) {
            jsonObject.put("card2", card2)
        } else {
            jsonObject.put("card2", JSONObject.NULL)
        }

        if(card3 != null) {
            jsonObject.put("card3", card3)
        } else {
            jsonObject.put("card3", JSONObject.NULL)
        }

        jsonObject.put("reason", reason)

        return jsonObject
    }

    fun makeMove() :JSONObject {
        var answer: JSONObject = JSONObject()
        if(this.topCard.type == CardType.NUMBER) {
            val tmpHand = ArrayList<Card>()

            for(card: Card in hand) {
                if(card.cardColor1 == topCard.cardColor1 || card.cardColor1 == topCard.cardColor2 || card.cardColor2 == topCard.cardColor1 || card.cardColor2 == topCard.cardColor2 || card.cardColor1 == CardColor.MULTI) {
                    if(tmpHand.size < (topCard.cardValue?.value ?: 0)) {
                        tmpHand.add(card)
                    }
                }
            }

            if(tmpHand.size < (topCard.cardValue?.value ?: 0)) { // If the AI can't throw cards
                answer = sendCards(Action.NOPE,null,null,null, "I don't have the right cards")

            } else {
                if(tmpHand.size == 1) {
                    answer = sendCards(Action.PUT,createJSONCard(tmpHand[0]),null,null, "I have the right card")
                    println("Put card: ${tmpHand[0]}")
                } else if(tmpHand.size == 2) {
                    answer = sendCards(Action.PUT,createJSONCard(tmpHand[0]),createJSONCard(tmpHand[1]),null, "I have the right cards")
                    println("Put cards: ${tmpHand[0]} ${tmpHand[1]}")
                } else if(tmpHand.size == 3) {
                    answer = sendCards(Action.PUT,createJSONCard(tmpHand[0]),createJSONCard(tmpHand[1]),createJSONCard(tmpHand[2]), "I have the right cards")
                    println("Put cards: ${tmpHand[0]} ${tmpHand[1]} ${tmpHand[2]}")
                }
            }
        } else if(this.topCard.type == CardType.JOKER) {
            answer = sendCards(Action.PUT, createJSONCard(hand[0]), null, null, "I have the right card")

        } else if(this.topCard.type == CardType.REBOOT) {
            answer = sendCards(Action.PUT, createJSONCard(hand[0]), null, null, "I have the right card")

        } else if(this.topCard.type == CardType.SEE_THROUGH) {
            answer = JSONObject()
        } else if(this.topCard.type == CardType.SELECTION) {
            answer = JSONObject()
        }
        return answer
    }
}