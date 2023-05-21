package com.example.chatjet.ui.screen

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class LoginFragmentTest {

    private val loginFragment = LoginFragment()

    @Test
    fun `empty email or password return false`() {
        val result = loginFragment.validateOnLogin(
            "xxx@xx.xx",
            ""
        )
        assertThat(result).isFalse()
    }
}