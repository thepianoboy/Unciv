package com.unciv.ui.worldscreen.mainmenu

import com.unciv.logic.GameSaver
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * This is meant to be an analogue to the DropBox client object
 */
object UnRESTClient {

    fun request(uri: String, method: String = "GET",  data: String = "", contentType: String = ""): InputStream? {
        with(URL(uri).openConnection() as HttpURLConnection) {
            requestMethod = method

            if (contentType != "") setRequestProperty("Content-Type", contentType)

            doOutput = true

            try {
                if (data != "") {
                    val payload: ByteArray = data.toByteArray(Charset.forName("UTF-8"))
                    val stream = DataOutputStream(outputStream)
                    stream.write(payload)
                    stream.flush()
                }
                return inputStream
            } catch (ex: Exception) {
                println(ex.message)
                val reader = BufferedReader(InputStreamReader(errorStream))
                println(reader.readText())
                return null
            } catch (err: Error) {
                println(err.message)
                val reader = BufferedReader(InputStreamReader(errorStream))
                println(reader.readText())
                return null
            }
        }
    }

    fun createGame(hostName: String, gameId: String, data: String) {
        uploadGameFull(hostName, gameId, data, false)
    }

    fun downloadGameFull(hostName: String, gameId: String): InputStream {
        val response = request("http://$hostName/game?gameId=$gameId", "GET")
        return response!!
    }

    fun downloadGameFullAsString(hostName: String, gameId: String): String {
        val stream = downloadGameFull(hostName, gameId)
        return BufferedReader(InputStreamReader(stream)).readText()
    }

    fun uploadGameFull(hostName: String, gameId: String, data: String, overwrite: Boolean = false) {
        //...
    }

    fun deleteGame(hostName: String, gameId: String) {
        //...
    }

}

class OnlineMultiplayerUnREST : OnlineMultiplayer {

}
