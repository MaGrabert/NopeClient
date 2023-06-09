package view

import app.Profile
import game.TournamentInfo
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import socket.HTTPClient
import socket.SocketHandler
import tornadofx.*

/**
 * Shows a sign in window.
 *
 * @author Mathis Grabert
 * @since 18.04.2023
 */
class SignInView : View("Sign-Up") {
    private var userName: TextField by singleAssign()
    private var password: TextField by singleAssign()
    private var httpClient = HTTPClient
    private lateinit var errorLabel: Label

    override val root = vbox {
        prefWidth = 300.0
        prefHeight = 200.0

        form {
            fieldset("Login") {
                field("User-Name") {
                    userName = textfield()
                }

                field("Password") {
                    password = passwordfield()
                }
                errorLabel = label()
            }
            hbox(spacing = 10, alignment = Pos.CENTER) {
                button("Login") {
                    action {
                        if (userName.text.toString() != "" && password.text.toString() != "") {
                            httpClient.loginRequest(userName.text.toString(), password.text.toString())
                            SocketHandler.connect()
                            Profile.userName = userName.text.toString()

                            replaceWith<MainView>()
                            currentWindow?.sizeToScene()
                        } else {
                            errorLabel.text = "A Field in the mask was empty!"
                        }
                    }
                }

                button("Sign Up") {
                    action {
                        replaceWith<SignUpView>()
                        currentWindow?.sizeToScene()
                    }
                }
            }
        }
    }
}