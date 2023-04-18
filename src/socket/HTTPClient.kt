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

            println("Status-Code: " + response.statusCode())
            println("Body: " + response.body())
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

            println("Status-Code: " + response.statusCode())
            println("Body: " + response.body())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAccessToken() :String {
        return this.accessToken
    }
}