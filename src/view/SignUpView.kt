package view

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import socket.HTTPClient
import tornadofx.*

/**
 * Shows the login window
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class SignUpView : View("Sign-Up") {
    private var userName: TextField by singleAssign()
    private var password: TextField by singleAssign()
    private var firstName: TextField by singleAssign()
    private var lastName: TextField by singleAssign()
    private var httpClient = HTTPClient
    private lateinit var errorLabel: Label

    override val root = vbox {
        prefWidth = 300.0
        prefHeight = 300.0

        form {
            fieldset("Register") {
                field("User-Name") {
                    userName = textfield()
                }

                field("Password") {
                    password = passwordfield()
                }

                field("First-Name") {
                    firstName = textfield()
                }

                field("Last-Name") {
                    lastName = textfield()
                }
                errorLabel = label()
            }
            hbox(spacing = 10, alignment = Pos.CENTER) {
                button("Register") {
                    action {
                        if (userName.text.toString() != "" && password.text.toString() != "" && firstName.text.toString() != "" && lastName.text.toString() != "") {
                            httpClient.registerRequest(
                                userName.text.toString(),
                                password.text.toString(),
                                firstName.text.toString(),
                                lastName.text.toString()
                            )

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
}