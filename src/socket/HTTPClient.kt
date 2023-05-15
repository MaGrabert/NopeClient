package socket

import app.Profile
import java.lang.Exception
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * HTTP client to sign in and sign up.
 *
 * @author Mathis Grabert
 * @since 18.04.2023
 */
object HTTPClient {
    private var accessToken: String = ""

    /**
     * Registers a new player.
     */
    fun registerRequest(username: String, password: String, firstname: String, lastname: String) {
        val client: HttpClient = HttpClient.newHttpClient()
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://nope-server.azurewebsites.net/api/auth/register"))
            .header("Content-Type", "application/json")
            .method(
                "POST",
                HttpRequest.BodyPublishers.ofString("{\"username\":\"$username\",\"password\":\"$password\",\"firstname\":\"$firstname\",\"lastname\":\"$lastname\"}")
            )
            .build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() in 200..299)
                println("Register successful!")
            else
                println("Error during registration!")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Signs up the player and saves the received token.
     */
    fun loginRequest(username: String, password: String) {
        val client: HttpClient = HttpClient.newHttpClient()
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://nope-server.azurewebsites.net/api/auth/login"))
            .header("Content-Type", "application/json")
            .method(
                "POST",
                HttpRequest.BodyPublishers.ofString("{\"username\":\"$username\",\"password\":\"$password\"}")
            )
            .build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            accessToken = response.body().split(":")[1].replace("\"", "").replace("}", "")
            accessToken = accessToken.split(",")[0]
            Profile.token = accessToken

            if (response.statusCode() in 200..299)
                println("Login successful!")
            else
                println("Error during login!")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}