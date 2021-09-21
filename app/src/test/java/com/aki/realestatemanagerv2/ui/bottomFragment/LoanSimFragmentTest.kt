package com.aki.realestatemanagerv2.ui.bottomFragment

import org.junit.Assert.*
import org.junit.Test

class LoanSimFragmentTest{

    @Test
    fun loanCalculationTesting() {

        val rate = 10f
        val time = 25
        val amount = 150000
        val personal = 50000

        assertEquals(366, LoanSimFragment.loanCalculationTesting(rate, personal, time, amount).toInt())
    }
}