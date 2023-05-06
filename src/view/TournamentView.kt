package view

import game.TournamentInfo
import javafx.geometry.Pos
import socket.SocketHandler
import tornadofx.*

class TournamentView : View("Nope-Client-KI") {
    override val root = borderpane() {
        prefWidth = 1024.0
        prefHeight = 800.0

        top {
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

        left {
            listview<String> {
                items.add("Message:")
                items.add(TournamentInfo.message)
                items.add("")
                items.add("Tournament ID:")
                items.add(TournamentInfo.id)
                items.add("")
                items.add("Number-of-Matches:")
                items.add(TournamentInfo.matches)
                items.add("")
                items.add("Current Size:")
                items.add(TournamentInfo.size)
                items.add("")
                items.add("Players Names:")
                items.add(TournamentInfo.players)
            }
        }

        bottom {
            hbox(spacing = 10, alignment = Pos.CENTER) {
                button("Leave Tournament") {
                    action {
                        SocketHandler.beInTournament = false
                        SocketHandler.leaveTournament()
                        replaceWith<MainView>()
                        currentWindow?.sizeToScene()
                    }
                }
            }
        }
    }
}