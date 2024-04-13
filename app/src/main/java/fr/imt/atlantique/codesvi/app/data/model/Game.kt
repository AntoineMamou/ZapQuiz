package fr.imt.atlantique.codesvi.app.data.model

data class Game(
    val players : List<User>,
    val scores : List<Int>
)