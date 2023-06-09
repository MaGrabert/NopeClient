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
    private var topIsSeeTrough = false
    private var lastTopCard: Card? = null
    private var nextPlayerHandSize: Int = 0

    /*
     * Gets a card as jsonObject and creates a card
     */
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

    /**
     * Sets the top card with content from a jsonObject
     */
    fun setTopCard(topUpdate: JSONObject) {
        this.topCard = createCard(topUpdate, "Top-Card")!!
    }

    /**
     * Sets the next player hand size with content from a jsonObject
     */
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

    /**
     * Sets the last top card with content from a jsonObject
     */
    fun setLastTopCard(lastTopUpdate: JSONObject?) {
        this.lastTopCard = createCard(lastTopUpdate, "LastTop-Card")
    }

    /**
     * Sets the hand with content from a jsonObject
     */
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


    /*
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
        jsonObject.put("select", JSONObject.NULL)
        jsonObject.put("selectValue", JSONObject.NULL)
        jsonObject.put("selectedColor", JSONObject.NULL)

        return jsonObject
    }

    /*
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

    /*
     * Build an answer as jsonObject with action, card 1-3 and reason
     */
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

    /**
     * Reacts on the top Card and builds the correct answer as jsonObject
     *
     * @return answer The answer with action, card 1-3 and reason
     */
    fun makeMove(): JSONObject {
        var answer: JSONObject = JSONObject()
        if (this.topCard.type == CardType.NUMBER) {
            topIsSeeTrough = false
            answer = reactOnColor()

        } else if (this.topCard.type == CardType.JOKER) {
            topIsSeeTrough = false
            takeCard = false
            val bestCard = searchBestCard(hand)
            println("Send card $bestCard, because topcard is a joker")
            answer =  sendCards(Action.PUT,createJSONCard(bestCard),null,null,"I have the right Card")

        } else if (this.topCard.type == CardType.REBOOT) {
            topIsSeeTrough = false
            takeCard = false
            val bestCard = searchBestCard(hand)
            println("Send card $bestCard, because topcard is a reboot")
            answer =  sendCards(Action.PUT,createJSONCard(bestCard),null,null,"I have the right Card")

        } else if (this.topCard.type == CardType.SEE_THROUGH) {
            takeCard = false
            if (this.lastTopCard != null && !topIsSeeTrough) {
                topIsSeeTrough = true
                this.topCard = this.lastTopCard!!
                answer = makeMove()
            } else {
                answer = reactOnSeeThrough()
            }
        } else if (this.topCard.type == CardType.SELECTION) {
            topIsSeeTrough = false
            takeCard = false
            answer = JSONObject()
        }
        return answer
    }

    /*
     * Reaction on a see-through card
     */
    private fun reactOnSeeThrough(): JSONObject {
        val colorList =  ArrayList<Card>()

        for(card: Card in hand) {
            if(card.cardColor1 == topCard.cardColor1 || card.cardColor2 == topCard.cardColor2) {
                colorList.add(card)
            }
        }

        val bestCard = searchBestCard(colorList)

        return sendCards(Action.PUT,createJSONCard(bestCard),null,null,"I have the right Card")
    }

    /*
     * Searches a special card or the best number card in the given list
     */
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

    /*
     * Reacts on number cards
     */
    private fun reactOnColor(): JSONObject {
        val listColor1 = ArrayList<Card>()
        val listColor2 = ArrayList<Card>()

        fillColorLists(listColor1, listColor2)

        println("List1 before sort: $listColor1, List2 before sort: $listColor2")

        sortColorList(listColor1, listColor2)

        println("List1 after sort: $listColor1, List2 after sort: $listColor2")

        if (listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value
                ?: 0) && takeCard
        ) {
            takeCard = false
            if(listColor1.size == 1) {
                if(listColor1[0].type != CardType.NUMBER) {
                    return sendCards(Action.PUT, createJSONCard(listColor1[0]), null, null, "I have the right card")
                } else {
                    return sendCards(Action.NOPE, null, null, null, null)
                }
            } else if(listColor2.size == 1){
                if(listColor2[0].type != CardType.NUMBER) {
                    return sendCards(Action.PUT, createJSONCard(listColor2[0]), null, null, "I have the right card")
                } else {
                    return sendCards(Action.NOPE, null, null, null, null)
                }
            } else {
                return sendCards(Action.NOPE, null, null, null, null)
            }
        } else if (listColor1.size < (topCard.cardValue?.value ?: 0) && listColor2.size < (topCard.cardValue?.value
                ?: 0) && !takeCard
        ) {
            if(listColor1.size == 1) {
                if(listColor1[0].type != CardType.NUMBER) {
                    return sendCards(Action.PUT, createJSONCard(listColor1[0]), null, null, "I have the right card")
                } else {
                    takeCard = true
                    return sendCards(Action.TAKE, null, null, null, null)
                }
            } else if(listColor2.size == 1){
                if(listColor2[0].type != CardType.NUMBER) {
                    return sendCards(Action.PUT, createJSONCard(listColor2[0]), null, null, "I have the right card")
                } else {
                    takeCard = true
                    return sendCards(Action.TAKE, null, null, null, null)
                }
            } else {
                takeCard = true
                return sendCards(Action.TAKE, null, null, null, null)
            }
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

    /*
     * Fill two list. List 1 is for color 1 and list 2 for color 2
     */
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

    /*
     * Sets the best card as last card
     */
    private fun sortColorList(
        listColor1: ArrayList<Card>,
        listColor2: ArrayList<Card>
    ) {
        val bestCardList1 = searchBestCard(listColor1)

        if(bestCardList1.type != CardType.NULL) {
            listColor1.remove(bestCardList1)
            listColor1.add(bestCardList1)
        }
        

        val bestCardList2 = searchBestCard(listColor2)

        if(bestCardList2.type != CardType.NULL) {
            listColor2.remove(bestCardList2)
            listColor2.add(bestCardList2)
        }
    }
}