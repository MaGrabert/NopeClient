package view

import game.Tournament
import javafx.scene.control.SelectionMode
import socket.SocketHandler
import tornadofx.*

/**
 * Shows what the AI currently do
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class MainView: View("Nope-Client-KI") {
    override val root = vbox {
        prefWidth = 1024.0
        prefHeight = 800.0

        menubar {
            menu("User") {
               item("Disconnect") {
                   action {
                       SocketHandler.disconnect()
                       replaceWith<SignInView>()
                       currentWindow?.sizeToScene()
                   }
               }
            }
            menu("Game") {
               item("Results"){
                   action {
                       replaceWith<ResultView>()
                   }
               }
            }
        }

        var tableview = tableview(SocketHandler.tableData) {
            column("ID", Tournament::id).prefWidth(204)
            column("Created at", Tournament::date).prefWidth(204)
            column("Size", Tournament::size).prefWidth(204)
            column("Status", Tournament::status).prefWidth(204)
            column("Player", Tournament::players).prefWidth(204.5)
            selectionModel.selectionMode = SelectionMode.SINGLE
        }

        button("Join Tournament") {
            action {
                SocketHandler.joinTournament(tableview.selectionModel.selectedItem.id)
            }
        }

        button("Create Tournament") {
            action {

            }
        }
    }
}