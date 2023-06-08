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
    private var lastTopCard: Card? = null
    private var nextPlayerHandSize: Int = 0

    /*
    *
    * null wird als String übermittel als "null"
    *
    *
    * */

    private fun createCard(jsonObject: JSONObject?, card: String): Card? {
        if (jsonObject != null) {
            var color1: String? = null
            var color2: String? = null
            var stringValue: String? = null
            var intValue: Int? = null
            var type: String = ""

            try {
                val colors: String = jsonObject.getString("color")

                if (colors.split("-").size == 2) {
                    color1 = colors.split("-")[0]
                    color2 = colors.split("-")[1]
                } else {
                    color1 = colors
                }
            } catch (e: JSONException) {
                println("Don't get $card-Color!")
            }

            try {
                stringValue = jsonObject.getString("value")
                if (stringValue != null && stringValue != "null") {
                    intValue = stringValue.toInt()
                }
            } catch (e: JSONException) {
                println("Don't get $card-Value")
            }

            try {
                type = jsonObject.getString("type")
            } catch (e: JSONException) {
                println("Don't get $card-Type")
            }

            return Card(
                CardType.getElement(type),
                CardColor.getElement(color1),
                CardColor.getElement(color2),
                CardValue.getElement(intValue)
            )
        } else {
            return null
        }
    }

    fun setTopCard(topUpdate: JSONObject) {
        this.topCard = createCard(topUpdate, "Top-Card")!!
    }

    fun setNextPlayerHandSize(playerList: JSONArray, nextPlayerIndex: Int) {
        try {
            val nextPlayer: JSONObject = playerList.getJSONObject(nextPlayerIndex)
            val handSizeString = nextPlayer.getString("handSize")

            if (handSizeString != null) {
                this.nextPlayerHandSize = handSizeString.toInt()
            }

        } catch (e: JSONException) {
            println("Don't get hand size of next player")
        }
    }

    fun setLastTopCard(lastTopUpdate: JSONObject?) {
        this.lastTopCard = createCard(lastTopUpdate, "LastTop-Card")
    }

    fun fillHand(updateHand: JSONArray) {
        hand.clear()
        for (index in 0 until updateHand.length()) {
            try {
                val jsonCard: JSONObject = updateHand.getJSONObject(index)
                hand.add(createCard(jsonCard, "Hand-Card")!!)
            } catch (e: JSONException) {
                println("Don't get a Card for the hand")
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

        if (card.cardColor1?.value != null && card.cardColor2?.value != null) {
            jsonObject.put("color", card.cardColor1.value + "-" + card.cardColor2.value)
        } else if (card.cardColor1?.value != null && card.cardColor2?.value == null) {
            jsonObject.put("color", card.cardColor1.value)
        } else {
            jsonObject.put("color", JSONObject.NULL)
        }

        jsonObject.put("value", card.cardValue?.value ?: JSONObject.NULL)
        //jsonObject.put("select", card.select?.value ?: JSONObject.NULL)
        //jsonObject.put("selectValue", card.selectValue?.value ?: JSONObject.NULL)
        //jsonObject.put("selectedColor", card.selectedColor?.value ?: JSONObject.NULL)

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

        if (card1 != null) {
            jsonObject.put("card1", card1)
        } else {
            jsonObject.put("card1", JSONObject.NULL)
        }

        if (card2 != null) {
            jsonObject.put("card2", card2)
        } else {
            jsonObject.put("card2", JSONObject.NULL)
        }

        if (card3 != null) {
            jsonObject.put("card3", card3)
        } else {
            jsonObject.put("card3", JSONObject.NULL)
        }

        jsonObject.put("reason", reason)

        return jsonObject
    }

    private fun buildAnswer(tmpHand: ArrayList<Card>): JSONObject {
        var answer: JSONObject = JSONObject()
        if (tmpHand.size == 1) {
            answer = sendCards(Action.PUT, createJSONCard(tmpHand[0]), null, null, "I have the right card")
        } else if (tmpHand.size == 2) {
            answer = sendCards(
                Action.PUT,
                createJSONCard(tmpHand[0]),
                createJSONCard(tmpHand[1]),
                null,
                "I have the right cards"
            )
        } else if (tmpHand.size == 3) {
            answer = sendCards(
                Action.PUT,
                createJSONCard(tmpHand[0]),
                createJSONCard(tmpHand[1]),
                createJSONCard(tmpHand[2]),
                "I have the right cards"
            )
        }
        return answer
    }

    fun makeMove(): JSONObject {
        var answer: JSONObject = JSONObject()
        if (this.topCard.type == CardType.NUMBER) {
            answer = reactOnColor()

        } else if (this.topCard.type == CardType.JOKER) {
            takeCard = false
            answer = createJSONCard(searchBestCard(hand))

        } else if (this.topCard.type == CardType.REBOOT) {
            takeCard = false
            answer = createJSONCard(searchBestCard(hand))

        } else if (this.topCard.type == CardType.SEE_THROUGH) {
            takeCard = false
            if (this.lastTopCard != null) {
                this.topCard = this.lastTopCard!!
                answer = makeMove()
            } else {
                answer = JSONObject() // Soll auf Farbe der jetzt aktuellen see-through reagieren
            }
        } else if (this.topCard.type == CardType.SELECTION) {
            takeCard = false
            answer = JSONObject()
        }
        return answer
    }

    private fun searchBestCard(cardList: ArrayList<Card>): Card {
        var specialCard: Card? = null
        var bigCard: Card? = null
        var midCard: Card? = null
        var smallCard: Card? = null
        var bestCard = Card(CardType.NULL, null, null, null)

        for(card: Card in cardList) {
            if(card.type != CardType.NUMBER) {
                specialCard = card
            } else {
                if(card.cardValue?.value == 3) {
                    bigCard = card
                } else if(card.cardValue?.value == 2) {
                    midCard = card
                } else if(card.cardValue?.value == 1) {
                    smallCard = card
                }
            }
        }

        if(specialCard != null) {
            bestCard = specialCard
        } else if(nextPlayerHandSize > 5) {
            if(bigCard != null) {
                bestCard = bigCard
            } else if(midCard != null) {
                bestCard = midCard
            } else if(smallCard != null){
                bestCard = smallCard
            }
        } else if(nextPlayerHandSize > 1) {
            if(midCard != null) {
                bestCard = midCard
            } else if(smallCard != null){
                bestCard = smallCard
            } else if(bigCard != null) {
                bestCard = bigCard
            }
        } else {
            if(smallCard != null) {
                bestCard = smallCard
            } else if(midCard != null) {
                bestCard = midCard
            } else if(bigCard != null) {
                bestCard = bigCard
            }
        }

        return bestCard
    }

    private fun reactOnColor(): JSONObject {
        val listColor1 = ArrayList<Card>()
        val listColor2 = ArrayList<Card>()

        fillColorLists(listColor1, listColor2)
        sortColorList(listColor1, listColor2)

        if (listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value
                ?: 0) && takeCard
        ) {
            takeCard = false
            return sendCards(Action.NOPE, null, null, null, null)
        } else if (listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value
                ?: 0) && !takeCard
        ) {
            takeCard = true
            return sendCards(Action.TAKE, null, null, null, null)
        } else if (listColor1.size == (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value
                ?: 0)
        ) {
            takeCard = false
            return buildAnswer(listColor1)
        } else if (listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size == (topCard.cardValue?.value
                ?: 0)
        ) {
            takeCard = false
            return buildAnswer(listColor2)
        } else if (listColor1.size == (topCard.cardValue?.value ?: 0) && listColor2.size == (topCard.cardValue?.value
                ?: 0)
        ) {
            takeCard = false
            return buildAnswer(listColor1)
        } else {
            error("Something went wrong in List Choice")
        }
    }

    private fun fillColorLists(
        listColor1: ArrayList<Card>,
        listColor2: ArrayList<Card>
    ) {
        for (card: Card in hand) {
            if (listColor1.size < (topCard.cardValue?.value
                    ?: 0) && topCard.cardColor1?.value != null && (topCard.cardColor1 == card.cardColor1 || topCard.cardColor1 == card.cardColor2 || card.cardColor1 == CardColor.MULTI)
            ) {
                listColor1.add(card)
            }
            if (listColor2.size < (topCard.cardValue?.value
                    ?: 0) && topCard.cardColor2?.value != null && (topCard.cardColor2 == card.cardColor1 || topCard.cardColor2 == card.cardColor2 || card.cardColor1 == CardColor.MULTI)
            ) {
                listColor2.add(card)
            }
        }
    }

    private fun sortColorList(
        listColor1: ArrayList<Card>,
        listColor2: ArrayList<Card>
    ) {
        val bestCardList1 = searchBestCard(listColor1)
        listColor1.remove(bestCardList1)
        listColor1.add(bestCardList1)

        val bestCardList2 = searchBestCard(listColor2)
        listColor2.remove(bestCardList2)
        listColor2.add(bestCardList2)
    }
}