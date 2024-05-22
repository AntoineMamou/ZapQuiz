package fr.imt.atlantique.codesvi.app.data.model

data class Game(
    val gameId: String,
    val players : List<Player>,
    var round: Int,
    val questions: List<QCM>,
    val gameState : String
){
    constructor():this(
        "",
        listOf<Player>(),
        0,
        listOf<QCM>(),
        ""
    )
}

data class Player(
    val user: User,
    val score : Int
){
    constructor():this(
        User(),
        0
    )
}