package com.example.chatjet.ui.screen

import com.google.common.truth.Truth
import org.junit.Test


class ResetPasswordFragmentTest{
    private val resetPasswordFragment = ResetPasswordFragment()

    @Test
    fun `empty field return false`() {
        val result = resetPasswordFragment.validateResetPassword(
            "",
            "xxx@xx.xx"
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `e-mail is not valid returns false`() {
        val result = resetPasswordFragment.validateResetPassword(
            "xxxxx.xx@",
            ""
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `not valid email and correctly repeated email returns false`() {
        val result = resetPasswordFragment.validateResetPassword(
            "",
            "xxx@xx.xx"
        )
        Truth.assertThat(result).isFalse()
    }
}