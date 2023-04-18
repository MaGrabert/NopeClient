package app

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

/**
 * The style class the design the graphic user interface
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class Styles : Stylesheet() {
    companion object {
        val label by cssclass()
    }
    init {
        label {
            fontSize = 17.px
            textFill = Color.BLACK
        }
    }
}