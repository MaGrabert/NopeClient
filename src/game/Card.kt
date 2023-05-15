package game

/**
 * All card Types.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class CardType {
    NUMBER, JOKER, REBOOT, SEE_THROUGH, SELECTION
}

/**
 * All card colors.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class CardColor {
    RED, BLUE, GREEN, YELLOW, RED_YELLOW, BLUE_GREEN, YELLOW_BLUE, RED_BLUE, RED_GREEN, YELLOW_GREEN, MULTI, NULL
}

/**
 * All card values.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
enum class CardValue {
    ONE, TWO, THREE, NULL
}

/**
 * Represents a card.
 *
 * @author Mathis Grabert
 * @since 11.05.2023
 */
data class Card(val type: CardType, val cardColor: CardColor, val cardValue: CardValue)