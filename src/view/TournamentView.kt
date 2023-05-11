package view

import app.Profile
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
                    item("Profile"){
                        action {
                            replaceWith<ProfileView>()
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
                items.add("Status:")
                items.add(TournamentInfo.status)
                items.add("")
                items.add("Tournament ID:")
                items.add(TournamentInfo.id)
                items.add("")
                items.add("Current Size:")
                items.add(TournamentInfo.size)
                items.add("")
                items.add("Players Names:")
                items.add(TournamentInfo.players)
            }
        }

        center {
            setPrefSize(100.0, 100.0)
            imageview(url = "See-Through.png") {
                fitHeightProperty().bind(parent.prefHeight(100.0).toProperty())
                fitWidthProperty().bind(parent.prefWidth(100.0).toProperty())
            }
        }

        bottom {
            hbox(spacing = 10, alignment = Pos.CENTER) {
                button("Leave Tournament") {
                    action {
                        Profile.isInTournament = false
                        Profile.isHost = false
                        SocketHandler.emit("tournament:leave")
                        replaceWith<MainView>()
                        currentWindow?.sizeToScene()
                    }
                }

                if(Profile.isHost) {
                    button("Start Tournament") {
                        action {
                            SocketHandler.emit("tournament:start")
                        }
                    }
                }
            }
        }
    }
}