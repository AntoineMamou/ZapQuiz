package fr.imt.atlantique.codesvi.app.data.model

data class QCM(
    val answers : List<Reponses>,
    val categorie : String,
    val niveau : Int,
    val question : String,
    val type : String
) {
    // Constructeur sans argument
    constructor() : this(
        listOf(), // Liste vide pour les réponses
        "", // Chaîne vide pour la catégorie
        0, // Niveau par défaut
        "", // Chaîne vide pour la question
        "" // Chaîne vide pour le type
    )
}
