package fr.imt.atlantique.codesvi.app.data.model

data class User(
    val pseudo : String,
    val motDePasse : String,
    val trophees : Int,
    val iconeJoueur : String,
    val banniere : String,
    val titre : String,
    val etatConnection : Boolean,
    val amis : List<User>
)