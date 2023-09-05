package flashcards

import java.io.FileNotFoundException

private val cards = CardCollection()
private val ioUtils = IoUtils()
private var importFile: String? = null
private var exportFile: String? = null

fun main(args: Array<String>) {
    parseArgs(args)
    importFile?.let { doImport(it) }

    while (true) {
        ioUtils.println("Input the action (${ Action.values().joinToString(", ") { it.input } }):")
        when (Action.fromString(ioUtils.readln())) {
            Action.ADD -> addCard()
            Action.REMOVE -> removeCard()
            Action.IMPORT -> importCards()
            Action.EXPORT -> exportCards()
            Action.ASK -> askCards()
            Action.EXIT -> break
            null -> ioUtils.println("Invalid action")
            Action.LOG -> saveLog()
            Action.HARDEST_CARD -> hardestCard()
            Action.RESET_STATS -> resetStats()
        }
        ioUtils.println()
    }
    ioUtils.println("Bye bye!")
    exportFile?.let { doExport(it) }
}

private fun parseArgs(args: Array<String>) {
    for (i in 0 until (args.size - 1)) {
        when (args[i]) {
            "-import" -> importFile = args[i + 1]
            "-export" -> exportFile = args[i + 1]
        }
    }
}

private fun addCard() {
    readCard()?.let {
        cards.addCard(it)
        ioUtils.println("The pair (\"${it.term}\":\"${it.definition}\") has been added.")
    }
}

private fun readCard(): Card? {
    ioUtils.println("The card:")
    readValue(cards::hasCardWithTerm, "The card \"%s\" already exists.")?.let { term ->
        ioUtils.println("The definition of the card:")
        readValue(cards::hasCardWithDefinition, "The definition \"%s\" already exists.")?.let { definition ->
            return Card(term = term, definition = definition, errorCount = 0)
        }
    }
    return null
}

private fun readValue(valueExists: (String) -> Boolean, errorMessage: String): String? {
    val value = ioUtils.readln()
    if (valueExists(value)) {
        ioUtils.println(errorMessage.format(value))
        return null
    }
    return value
}

private fun removeCard() {
    ioUtils.println("Which card?")
    val term = ioUtils.readln()
    if (cards.removeCardWithTerm(term)) {
        ioUtils.println("The card has been removed.")
    } else {
        ioUtils.println("Can't remove \"$term\": there is no such card.")
    }
}

private fun askCards() {
    if (cards.isEmpty()) {
        ioUtils.println("No cards.")
        return
    }
    ioUtils.println("How many times to ask?")
    val askCount = ioUtils.readln().toInt()
    for (i in 1..askCount) {
        askCard()
    }
}

private fun askCard() {
    val card = cards.randomCard()
    ioUtils.println("Print the definition of \"${card.term}\":")
    val answer = ioUtils.readln()
    if (answer == card.definition) {
        ioUtils.println("Correct!")
    } else {
        cards.addError(card)
        val correctCard = cards.cardWithDefinition(answer)
        if (correctCard != null) {
            ioUtils.println("Wrong. The right answer is \"${card.definition}\", but your definition is correct for \"${correctCard.term}\"")
        } else {
            ioUtils.println("Wrong. The right answer is \"${card.definition}\".")
        }
    }
}

private fun exportCards() {
    ioUtils.println("File name:")
    val filename = ioUtils.readln()
    if (filename.isNotBlank()) {
        doExport(filename)
    }
}

private fun doExport(filename: String) {
    val cardCount = cards.exportToFile(filename)
    ioUtils.println("$cardCount cards have been saved.")
}

private fun importCards() {
    ioUtils.println("File name:")
    val filename = ioUtils.readln()
    try {
        if (filename.isNotBlank()) {
            doImport(filename)
        }
    } catch (e: FileNotFoundException) {
        ioUtils.println("File not found.")
    }
}

private fun doImport(filename: String) {
    val cardCount = cards.importFromFile(filename)
    ioUtils.println("$cardCount cards have been loaded.")

}

private fun saveLog() {
    ioUtils.println("File name:")
    val filename = ioUtils.readln()
    if (filename.isNotBlank()) {
        ioUtils.saveLog(filename)
        ioUtils.println("The log has been saved.")
    }
}

private fun hardestCard() {
    val hardestCards = cards.hardestCards()
    when (hardestCards.size) {
        0 -> ioUtils.println("There are no cards with errors.")
        1 -> {
            val card = hardestCards[0]
            ioUtils.println("The hardest card is \"${card.term}\". You have ${card.errorCount} errors answering it.")
        }
        else -> {
            val errorCount = hardestCards[0].errorCount
            val hardestTerms = hardestCards.joinToString(", ") { "\"${it.term}\"" }
            println("The hardest cards are $hardestTerms. You have $errorCount errors answering them.")
        }
    }
}

private fun resetStats() {
    cards.resetStats()
    ioUtils.println("Card statistics have been reset.")
}