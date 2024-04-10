package fr.imt.atlantique.codesvi.app.data.model

data class Reponses(
    val reponse: String,
    val bonne_réponse : Boolean
) {
    // Constructeur sans argument
    constructor() : this(
        "", // Chaîne vide pour la réponse
        false // Valeur par défaut pour bonneReponse
    )
}