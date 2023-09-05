package flashcards

import java.io.File

internal class IoUtils {
    private val logMessages = mutableListOf<String>()

    fun println(message: String) {
        logMessages.add(message)
        kotlin.io.println(message)
    }

    fun println() {
        println("")
    }

    fun readln(): String {
        val input = kotlin.io.readln()
        logMessages.add(input)
        return input
    }

    fun saveLog(filename: String) {
        File(filename).printWriter().use { out ->
            logMessages.forEach() {
                out.println(it)
            }
        }
    }
}
