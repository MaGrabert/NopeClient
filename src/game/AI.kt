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
    private var hand = ArrayList<Card>()
    private lateinit var topCard: Card
    private var takeCard: Boolean = false

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
        reason: String? = "Because I can!"
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

    fun buildAnswer(tmpHand: ArrayList<Card>) : JSONObject{
        var answer: JSONObject = JSONObject()
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
        return answer
    }

    fun makeMove() :JSONObject {
        var answer: JSONObject = JSONObject()
        if(this.topCard.type == CardType.NUMBER) {
            val listColor1 = ArrayList<Card>()
            val listColor2 = ArrayList<Card>()

            for(card: Card in hand) {
                if(listColor1.size < (topCard.cardValue?.value ?: 0) && topCard.cardColor1?.value != null && (topCard.cardColor1 == card.cardColor1 || topCard.cardColor1 == card.cardColor2)) {
                    listColor1.add(card)
                }
                if(listColor2.size < (topCard.cardValue?.value ?: 0) && topCard.cardColor2?.value != null && (topCard.cardColor2 == card.cardColor1 || topCard.cardColor2 == card.cardColor2)) {
                    listColor2.add(card)
                }
            }

            println("Liste 1: $listColor1 List 2: $listColor2")

            if(listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value ?: 0) && takeCard) {
                takeCard = false
                answer = sendCards(Action.NOPE,null,null,null, null)
            } else if(listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value ?: 0) && !takeCard) {
                takeCard = true
                answer = sendCards(Action.TAKE,null,null,null, null)
            } else if(listColor1.size == (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value ?: 0)) {
                takeCard = false
                answer = buildAnswer(listColor1)
            } else if(listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size == (topCard.cardValue?.value ?: 0)) {
                takeCard = false
                answer = buildAnswer(listColor2)
            } else if(listColor1.size == (topCard.cardValue?.value ?: 0) && listColor2.size == (topCard.cardValue?.value ?: 0)) {
                takeCard = false
                answer = buildAnswer(listColor1)
            } else {
                error("Something went wrong in List Choice")
            }

        } else if(this.topCard.type == CardType.JOKER) {
            takeCard = false
            answer = sendCards(Action.PUT, createJSONCard(hand[0]), null, null, "I have the right card")

        } else if(this.topCard.type == CardType.REBOOT) {
            takeCard = false
            answer = sendCards(Action.PUT, createJSONCard(hand[0]), null, null, "I have the right card")

        } else if(this.topCard.type == CardType.SEE_THROUGH) {
            takeCard = false
            answer = JSONObject()
        } else if(this.topCard.type == CardType.SELECTION) {
            takeCard = false
            answer = JSONObject()
        }
        return answer
    }
}