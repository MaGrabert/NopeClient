package view

import game.Tournament
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import socket.SocketHandler
import tornadofx.*

/**
 * Shows what the AI currently do
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class MainView: View("Nope-Client-KI") {
    override val root = borderpane() {
        prefWidth = 1024.0
        prefHeight = 800.0
        lateinit var tableview: TableView<Tournament>

        top = hbox {
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
        }

        center {
            tableview = tableview(SocketHandler.tableData) {
                column("ID", Tournament::id).prefWidth(204)
                column("Created at", Tournament::date).prefWidth(204)
                column("Size", Tournament::size).prefWidth(204)
                column("Status", Tournament::status).prefWidth(204)
                column("Player", Tournament::players).prefWidth(204.5)
                selectionModel.selectionMode = SelectionMode.SINGLE
            }
        }

        bottom {
            hbox(spacing = 10,alignment = Pos.CENTER) {
                button("Join Tournament") {
                    action {
                        SocketHandler.joinTournament(tableview.selectionModel.selectedItem.id)
                        replaceWith<TournamentView>()
                    }
                }

                button("Create Tournament") {
                    action {

                    }
                }
            }
        }
    }
}