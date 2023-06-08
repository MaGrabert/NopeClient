package game

/**
 * All card Types.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class CardType(val value: String?) {
    NUMBER("number"),
    JOKER("joker"),
    REBOOT("reboot"),
    SEE_THROUGH("see-through"),
    SELECTION("selection"),
    NULL(null);

    companion object {
        fun getElement(value: String?): CardType {
            for (element: CardType in CardType.values()) {
                if (element.value == value) {
                    return element
                }
            }
            throw IllegalArgumentException("$value is a illegal value!")
        }
    }
}

/**
 * All card colors.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class CardColor(val value: String?) {
    RED("red"),
    BLUE("blue"),
    GREEN("green"),
    YELLOW("yellow"),
    RED_YELLOW("red-yellow"),
    BLUE_GREEN("blue-green"),
    YELLOW_BLUE("yellow-blue"),
    RED_BLUE("red-blue"),
    RED_GREEN("red-green"),
    YELLOW_GREEN("yellow-green"),
    MULTI("multi"),
    NULL(null);

    companion object {
        fun getElement(param: String?): CardColor {
            for (element: CardColor in CardColor.values()) {
                if (element.value == param) {
                    return element
                }
            }
            throw IllegalArgumentException("$param is a illegal value!")
        }
    }
}

/**
 * All card values.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class CardValue(val value: Int?) {
    ONE(1),
    TWO(2),
    THREE(3),
    NULL(null);

    companion object {
        fun getElement(param: Int?): CardValue {
            for (element: CardValue in CardValue.values()) {
                if (element.value == param) {
                    return element
                }
            }
            throw IllegalArgumentException("$param is a illegal value!")
        }
    }
}

/**
 * Represents a card.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
data class Card(val type: CardType, val cardColor1: CardColor?, val cardColor2: CardColor?, val cardValue: CardValue?)