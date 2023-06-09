package view

import app.Profile
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode
import javafx.scene.paint.Color
import socket.SocketHandler
import tornadofx.*

/**
 * Shows the tournament
 *
 * @author Mathis Grabert
 * @since 03.05.2023
 */
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
                    item("Profile") {
                        action {
                            replaceWith<ProfileView>()
                        }
                    }
                }
            }
        }

        left {
            prefHeight = 800.0
            prefWidth = 100.0
            listview(SocketHandler.listData)
        }

        center {
            //rectangle {
            //    fill = Color.BLUE
            //    width = 150.0
            //    height = 300.0
            //    arcWidth = 20.0
            //    arcHeight = 20.0
            //}
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

                if (Profile.isHost) {
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