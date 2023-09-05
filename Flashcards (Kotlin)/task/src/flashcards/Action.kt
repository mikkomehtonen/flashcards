package flashcards

internal enum class Action(val input: String) {
    ADD("add"),
    REMOVE("remove"),
    IMPORT("import"),
    EXPORT("export"),
    ASK("ask"),
    EXIT("exit"),
    LOG("log"),
    HARDEST_CARD("hardest card"),
    RESET_STATS("reset stats"),
    ;

    companion object {
        fun fromString(str: String) = enumValues<Action>().firstOrNull { it.input == str }
    }
}
