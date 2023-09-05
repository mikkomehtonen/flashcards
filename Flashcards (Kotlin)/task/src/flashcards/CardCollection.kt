package flashcards

import java.io.File

internal class CardCollection {
    private val cardsByTerm = mutableMapOf<String, Card>()
    private val cardsByDefinition = mutableMapOf<String, Card>()

    fun isEmpty() = cardsByTerm.isEmpty()

    fun addCard(card: Card) {
        cardsByTerm[card.term] = card
        cardsByDefinition[card.definition] = card
    }

    fun removeCardWithTerm(term: String): Boolean {
        val card = cardsByTerm[term] ?: return false
        cardsByTerm.remove(card.term)
        cardsByDefinition.remove(card.definition)
        return true
    }

    /**
     * Throws NoSuchElementException if there are no cards
      */
    fun randomCard(): Card {
        return cardsByTerm.values.random()
    }

    fun exportToFile(filename: String): Int {
        File(filename).printWriter().use { out ->
            cardsByTerm.values.forEach() {
                out.println(it.term)
                out.println(it.definition)
                out.println(it.errorCount)
            }
        }
        return cardsByTerm.size
    }

    /**
     * Returns how many cards have been imported
     */
    fun importFromFile(filename: String): Int {
        var cardCount = 0
        var term: String? = null
        var definition: String? = null
        File(filename).forEachLine { line ->
            when {
                term == null -> term = line
                definition == null -> definition = line
                else -> {
                    addCard(Card(term!!, definition!!, line.toInt()))
                    term = null
                    definition = null
                    cardCount++
                }
            }
        }
        return cardCount
    }

    fun addError(card: Card) {
        card.errorCount += 1
    }

    fun hardestCards(): List<Card> {
        if (cardsByTerm.isEmpty()) {
            return emptyList()
        }
        val maxErrors = cardsByTerm.values.maxOf { it.errorCount }
        if (maxErrors == 0) {
            return emptyList()
        }
        return cardsByTerm.values.filter { it.errorCount == maxErrors }
    }

    fun resetStats() = cardsByTerm.values.forEach { it.errorCount = 0 }

    fun hasCardWithTerm(term: String) = cardsByTerm.contains(term)
    fun hasCardWithDefinition(definition: String) = cardsByDefinition.contains(definition)
    fun cardWithTerm(term: String) = cardsByTerm[term]
    fun cardWithDefinition(definition: String) = cardsByDefinition[definition]
}