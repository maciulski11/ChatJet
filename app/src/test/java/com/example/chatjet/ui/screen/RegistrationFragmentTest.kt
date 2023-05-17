package com.example.chatjet.ui.screen

import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Stworzenie test class -> klkiamy w class i generate i zmieniamy na unity4
class RegistrationFragmentTest() {

    private val registrationFragment = RegistrationFragment()

    @Test
    fun `empty full name return false`() {
        val result = registrationFragment.validateRegistrationInput(
            "",
            "123",
            "123",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `valid username and correctly repeated password returns true`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philip",
            "12345678AS",
            "12345678AS",
        )
        assertThat(result).isTrue()
    }

    @Test
    fun `username already exists returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Carl",
            "123",
            "123",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `incorrectly confirmed password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "123456",
            "abcdefg"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "",
            "",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 1 digit password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcdefg",
            "abcdefg",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 1 upper case password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcdefg",
            "abcdefg",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 8 password length returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcde",
            "abcde",
        )
        assertThat(result).isFalse()
    }
}