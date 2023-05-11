package game

enum class CardType {
    NUMBER, JOKER, REBOOT, SEE_THROUGH, SELECTION
}

enum class CardColor {
    RED, BLUE, GREEN, YELLOW, RED_YELLOW, BLUE_GREEN, YELLOW_BLUE, RED_BLUE, RED_GREEN, YELLOW_GREEN, MULTI, NULL
}

enum class CardValue {
    ONE, TWO, THREE, NULL
}

data class Card(val type: CardType, val cardColor: CardColor, val cardValue: CardValue)
