package com.example.chatjet.ui.screen

import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Create test class -> click in class and click in generate and we have to change on unity4
class RegisterFragmentTest() {

    private val registrationFragment = RegisterFragment()

    @Test
    fun `empty full name return false`() {
        val result = registrationFragment.validateRegistrationInput(
            "",
            "123",
            "123",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `not valid password and correctly repeated password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "",
            "12345A12",
            "1234ds5A12",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `e-mail is not valid returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Carl",
            "123",
            "123",
            "",
            "macio@ewfew"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `incorrectly confirmed password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "123456",
            "abcdefg",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "",
            "",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 1 digit password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcdefg",
            "abcdefg",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 1 upper case password returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcdefg",
            "abcdefg",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 8 password length returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcde",
            "abcde",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `less than 9 password length returns false`() {
        val result = registrationFragment.validateRegistrationInput(
            "Philipp",
            "abcde",
            "abcde",
            "432332",
            ""
        )
        assertThat(result).isFalse()
    }
}