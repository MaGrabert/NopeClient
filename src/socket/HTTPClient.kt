package socket

import java.lang.Exception
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object HTTPClient {
    private var accessToken: String = ""

    fun registerRequest(username: String, password: String, firstname: String, lastname: String) {
        val client: HttpClient = HttpClient.newHttpClient()
        val request: HttpRequest = HttpRequest.newBuilder()
                                        .uri(URI.create("https://nope-server.azurewebsites.net/api/auth/register"))
                                        .header("Content-Type", "application/json")
                                        .method("POST", HttpRequest.BodyPublishers.ofString("{\"username\":\"$username\",\"password\":\"$password\",\"firstname\":\"$firstname\",\"lastname\":\"$lastname\"}"))
                                        .build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if(response.statusCode() in 200..299)
                println("Register successful!")
            else
                println("Error during registration!")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loginRequest(username: String, password: String) {
        val client: HttpClient = HttpClient.newHttpClient()
        val request: HttpRequest = HttpRequest.newBuilder()
                                        .uri(URI.create("https://nope-server.azurewebsites.net/api/auth/login"))
                                        .header("Content-Type", "application/json")
                                        .method("POST", HttpRequest.BodyPublishers.ofString("{\"username\":\"$username\",\"password\":\"$password\"}"))
                                        .build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            accessToken = response.body().split(":")[1].replace("\"", "").replace("}", "")
            accessToken = accessToken.split(",")[0]
            SocketHandler.token = accessToken

            if(response.statusCode() in 200..299)
                println("Login successful!")
            else
                println("Error during login!")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAccessToken() :String {
        return this.accessToken
    }
}