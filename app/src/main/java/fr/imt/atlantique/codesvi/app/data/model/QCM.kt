package fr.imt.atlantique.codesvi.app.data.model

data class QCM(
    val answers : List<Answer>,
    val id: String,
    val type: String,
    val category : String,
    val level : Int,
    val question : String,
    val image: String,
    val gap : Float,
    val explanation : String
) {
    // Constructeur sans argument
    constructor() : this(
        listOf(),
        "",
        "",
        "", // Chaîne vide pour la catégorie
        0, // Niveau par défaut
        "", // Chaîne vide pour la question
        "",// Chaîne vide pour le type
        0F,
        "",
    )
}
