package com.unciv.ui.worldscreen.mainmenu

import com.unciv.logic.GameInfo
import com.unciv.logic.GameInfoPreview
import com.unciv.logic.GameSaver
import com.unciv.ui.saves.Gzip
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
        val method = if (overwrite) { "POST" } else { "PUT" }
        request("http://$hostName/game", method, data, "application/json")
    }

    fun deleteGame(hostName: String, gameId: String) {
        request("http://$hostName/game", "DELETE", "{\"gameId\": \"$gameId\"}", "application/json")
    }

}

class OnlineMultiplayerUnREST constructor(serverName: String = "") : OnlineMultiplayer {

    var hostName = serverName

    override fun getGameLocation(gameId: String) = hostName

    // NOTE: withPreview isn't needed here because UnREST is able to parse JSON
    override fun tryUploadGame(gameInfo: GameInfo, withPreview: Boolean) {
        val zippedGameInfo = Gzip.zip(GameSaver.json().toJson(gameInfo))
        UnRESTClient.uploadGameFull(hostName, gameInfo.gameId, zippedGameInfo, true)
    }

    override fun tryDownloadGame(gameId: String): GameInfo {
        val zippedGameInfo = UnRESTClient.downloadGameFullAsString(hostName, gameId)
        return GameSaver.gameInfoFromString(Gzip.unzip(zippedGameInfo))
    }

    /**
     * UnREST doesn't need a separate preview because it is capable of generating one on request
     */
    override fun tryUploadGamePreview(gameInfo: GameInfoPreview) {}

    /**
     * TODO: Once available, use the RESTful API to HTTP GET only the data needed for preview
     */
    override fun tryDownloadGamePreview(gameId: String): GameInfoPreview {
        return tryDownloadGame(gameId).asPreview()
    }

    override fun tryDownloadGameUninitialized(gameId: String): GameInfo {
        val zippedGameInfo = UnRESTClient.downloadGameFullAsString(hostName, gameId)
        return GameSaver.gameInfoFromStringWithoutTransients(Gzip.unzip(zippedGameInfo))
    }
}
