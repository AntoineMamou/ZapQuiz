package fr.imt.atlantique.codesvi.app.data.model

import fr.imt.atlantique.codesvi.app.R

data class User(
    val username : String,
    val password : String,
    var trophies : Int,
    var playerIcon : Int,
    var title : String,
    var connectionState : Boolean,
    var friends : List<String>,
    var victory : Int,
    var game_played : Int,
    var peak_trophy : Int,
    var favorite_category : String,
    var money : Int,
    var friendsRequest : List<String>
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
        listOf()
    )
}