package view

import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import socket.SocketHandler
import tornadofx.*


class PopOutView: View("Pop out") {
    private val toggleGroup = ToggleGroup()

    override val root = vbox {
        form {
            fieldset("How many matches?") {
                field {
                    hbox(spacing = 2, alignment = Pos.CENTER) {
                        radiobutton("3", toggleGroup)
                        radiobutton("5", toggleGroup)
                        radiobutton("7", toggleGroup)
                    }
                }
            }
            hbox(alignment = Pos.CENTER) {
                button("Create") {
                    action {
                        var numberOFMatches: String = toggleGroup.selectedToggle.toString().split("'")[1].replace("'", "")
                        SocketHandler.emit("tournament:create", numberOFMatches.toInt())
                        close()
                    }
                }
            }
        }
    }
}