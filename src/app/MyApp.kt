package app

import tornadofx.App
import tornadofx.importStylesheet
import view.SignInView

/**
 * This is the start point of the program
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class MyApp : App() {
    override var primaryView = SignInView::class

    init {
        importStylesheet(Styles::class)
    }
}
