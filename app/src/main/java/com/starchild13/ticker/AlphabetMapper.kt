package com.starchild13.ticker


object AlphabetMapper {
    // 1
    private val Alphabet = " ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789•".toList()

    val size: Int = Alphabet.size

    // 2
    fun getLetterAt(index: Int): Char = Alphabet[index % size]

    // 3
    fun getIndexOf(letter: Char): Int {
        val index = Alphabet.indexOf(letter.uppercaseChar())
        return if (index < 0) Alphabet.lastIndex else index
    }
}