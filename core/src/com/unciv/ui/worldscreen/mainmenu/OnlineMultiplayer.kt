package com.unciv.ui.worldscreen.mainmenu

import com.unciv.logic.GameInfo
import com.unciv.logic.GameInfoPreview

interface OnlineMultiplayer {
    fun getGameLocation(gameId: String) : String
    fun tryUploadGame(gameInfo: GameInfo, withPreview: Boolean)
    fun tryUploadGamePreview(gameInfo: GameInfoPreview)
    fun tryDownloadGame(gameId: String): GameInfo
    fun tryDownloadGamePreview(gameId: String): GameInfoPreview
    fun tryDownloadGameUninitialized(gameId: String): GameInfo
}
