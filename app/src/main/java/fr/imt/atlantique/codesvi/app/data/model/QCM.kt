package fr.imt.atlantique.codesvi.app.data.model

data class QCM(
    val id : Int,
    val themeQuestion : String,
    val typeQuestion : String,
    val niveau : Int,
    val question : String,
    val image : String, // A changer de type éventuellement
    val marge : Float,
    val answers : List<Reponses>
)
