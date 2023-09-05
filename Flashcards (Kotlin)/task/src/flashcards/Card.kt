package flashcards

internal data class Card(
    val term: String,
    val definition: String,
    var errorCount: Int,
)
