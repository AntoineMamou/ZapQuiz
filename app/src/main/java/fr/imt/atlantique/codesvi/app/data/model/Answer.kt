package fr.imt.atlantique.codesvi.app.data.model

data class Answer(
    val isRight : Boolean,
    val answer: String
) {



    // Constructeur sans argument
    constructor() : this(false, "")
}