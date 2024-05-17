package fr.imt.atlantique.codesvi.app.data.model

import android.content.Context

data class User(
    val username : String,
    val password : String,
    var trophies : Int,
    var playerIcon : String,
    var title : String,
    var connectionState : Boolean,
    var friends : List<String>,
    var victory : Int,
    var game_played : Int,
    var peak_trophy : Int,
    var favorite_category : String,
    var money : Int,
    var friendsRequest : List<String>,
    var availableIcons : List<String>,
    var availableTitles : List<String>
){
    constructor() : this(
        "",
        "",
        50,
        "lightning",
        "Zappeur débutant",
        false,
        listOf(),
        0,
        0,
        50,
        "None",
        100,
        listOf(),
        listOf(
            "lightning", "lightning_blue", "lightning_black",
            "lightning_red", "lightning_green", "lightning_white",
            "lightning_purple", "lightning_pink"
        ),
        listOf("Zappeur débutant")
    )

    fun getImageResourceId(context: Context): Int {
        val resourceId = context.resources.getIdentifier(playerIcon, "drawable", context.packageName)
        return if (resourceId != 0) {
            resourceId
        } else {
            // Return the ID for "lightning" drawable as a fallback
            context.resources.getIdentifier("lightning", "drawable", context.packageName)
        }
    }


}

