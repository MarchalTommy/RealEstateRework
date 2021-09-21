package com.aki.realestatemanagerv2.ui.bottomFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aki.realestatemanagerv2.databinding.FragmentBottomNavLoanSimBinding
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class LoanSimFragment : Fragment() {

    private var _binding: FragmentBottomNavLoanSimBinding? = null
    private val binding get() = _binding!!
    private var rate: Float = 0f
    private var personalInvestment = 0
    private var monthly = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomNavLoanSimBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
    }

    private fun initLayout() {
        binding.amountSlider.apply {
            setLabelFormatter { value: Float ->
                val format = NumberFormat.getCurrencyInstance()
                format.maximumFractionDigits = 0
                format.currency = Currency.getInstance("USD")
                format.format(value.toDouble())
            }
            value = 650000.0f
            addOnChangeListener { slider, value, fromUser ->
                loanCalculation()
            }
        }
        binding.durationSlider.apply {
            value = 10.0f
            addOnChangeListener { slider, value, fromUser ->
                loanCalculation()
            }
        }
    }

    private fun loanCalculation() {
        rate = if (binding.rateLayout.editText?.text?.isEmpty() == true) {
            1.25f
        } else {
            binding.rateLayout.editText?.text.toString().toFloat()
        }

        personalInvestment = if (binding.investmentLayout.editText?.text.isNullOrEmpty()) {
            0
        } else {
            binding.investmentLayout.editText?.text.toString().toInt()
        }

        val totalAmount = (binding.amountSlider.value - personalInvestment)

        val monthlyWithoutInterests = (totalAmount / (binding.durationSlider.value * 12))

        monthly = (monthlyWithoutInterests + (monthlyWithoutInterests * (rate / 100)))

        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("USD")

        binding.monthlyTv.text = format.format(monthly.roundToInt())
    }

    companion object {
        // Same math as above, but with parameters to be able to test it in unit tests
        fun loanCalculationTesting(rate: Float, personal: Int, time: Int, amount: Int): Float {
            val totalAmount = (amount - personal)

            val monthlyWithoutInterests = (totalAmount / (time * 12))

            val monthlyLoan = (monthlyWithoutInterests + (monthlyWithoutInterests * (rate / 100)))

            return monthlyLoan
        }
    }
}