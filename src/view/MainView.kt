package view

import app.Profile
import game.Tournament
import game.TournamentInfo
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
                    item("Profile"){
                        action {
                            replaceWith<ProfileView>()
                        }
                    }
                }
            }
        }

        center {
            tableview = tableview(SocketHandler.tableData) {
                column("ID", Tournament::id).prefWidth(1024.0 / 5)
                column("Created at", Tournament::date).prefWidth(1024.0 / 5)
                column("Size", Tournament::size).prefWidth(1024.0 / 5)
                column("Status", Tournament::status).prefWidth(1024.0 / 5)
                column("Players", Tournament::players).prefWidth(1024.0 / 5)
                selectionModel.selectionMode = SelectionMode.SINGLE
            }
        }

        bottom {
            hbox(spacing = 10,alignment = Pos.CENTER) {
                button("Join Tournament") {
                    action {
                        val selectedItem = tableview.selectionModel.selectedItem
                        if(selectedItem != null) {
                            Profile.isInTournament = true
                            SocketHandler.emit("tournament:join", selectedItem.id)
                            val size = (selectedItem.size.toInt() + 1).toString()
                            TournamentInfo.createJoinInfo(selectedItem.id, selectedItem.status, size, selectedItem.players + ", ${Profile.userName}")
                            SocketHandler.refreshingTournamentInfo()
                            SocketHandler.refreshingPlayerInfo()
                            replaceWith<TournamentView>()
                        } else {
                            println("Nothing was selected")
                        }
                    }
                }

                button("Create Tournament") {
                    action {
                        find(PopOutView::class).openWindow()
                    }
                }
            }
        }
    }
}