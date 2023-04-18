package view

import javafx.scene.control.TextField
import socket.HTTPClient
import socket.Socket
import tornadofx.*

/**
 * Shows the login window
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class SignUpView: View("Sign-Up") {
    private var userName: TextField by singleAssign()
    private var password: TextField by singleAssign()
    private var firstName: TextField by singleAssign()
    private var lastName: TextField by singleAssign()
    private var httpClient = HTTPClient

    override val root = vbox {
        prefWidth = 300.0
        prefHeight = 300.0

        form {
            label("User-Name")
            userName = textfield()

            label("Password")
            password = passwordfield()

            label("First-Name")
            firstName = textfield()

            label("Last-Name")
            lastName = textfield()

            val errorLabel = label()

            button("Register") {
                 action {

                    if(userName.text.toString() != "" && password.text.toString() != "" && firstName.text.toString() != "" && lastName.text.toString() != "") {
                        httpClient.registerRequest(userName.text.toString(), password.text.toString(), firstName.text.toString(), lastName.text.toString())
                        Socket.create()
                        Socket.connect()

                        replaceWith<SignInView>()
                        currentWindow?.sizeToScene()
                    } else {
                        errorLabel.text = "A Field in the mask was empty!"
                    }
                }
            }

            button("Sign In") {
                action {
                    replaceWith<SignInView>()
                    currentWindow?.sizeToScene()
                }
            }
        }
    }
}