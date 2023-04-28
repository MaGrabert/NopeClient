package view

import javafx.scene.control.TextField
import socket.HTTPClient
import socket.SocketHandler
import tornadofx.*

class SignInView: View("Sign-Up") {
    private var userName: TextField by singleAssign()
    private var password: TextField by singleAssign()
    private var httpClient = HTTPClient

    override val root = vbox {
        prefWidth = 300.0
        prefHeight = 200.0

        form {
            label("User-Name")
            userName = textfield()

            label("Password")
            password = passwordfield()

            val errorLabel = label()

            button("Login") {
                action {
                    if (userName.text.toString() != "" && password.text.toString() != "") {
                        httpClient.loginRequest(userName.text.toString(), password.text.toString())
                        SocketHandler.connect()

                        replaceWith<MainView>()
                        currentWindow?.sizeToScene()
                    } else {
                        println(userName.text.toString() + " " + password.text.toString())
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