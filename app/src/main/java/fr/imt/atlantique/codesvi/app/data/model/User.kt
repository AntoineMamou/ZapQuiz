package fr.imt.atlantique.codesvi.app.data.model

import fr.imt.atlantique.codesvi.app.R

data class User(
    val username : String,
    val password : String,
    val trophies : Int,
    val playerIcon : Int,
    val title : String,
    val connectionState : Boolean,
    val friends : List<String>,
    val victory : Int,
    val game_played : Int,
    val peak_trophy : Int,
    val favorite_category : String,
    val money : Int
){
    constructor() : this(
        "",
        "",
        50,
        R.drawable.lightning,
        "Zappeur débutant",
        false,
        listOf(),
        0,
        0,
        50,
        "None",
        100,
    )
}