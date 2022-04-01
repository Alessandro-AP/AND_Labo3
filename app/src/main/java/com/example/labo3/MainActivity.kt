package com.example.labo3

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.labo3.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinners()

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

        binding.baseInclude.birthdayButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER");
        }

        datePicker.addOnPositiveButtonClickListener {
            binding.baseInclude.birthdayET.editText?.setText(datePicker.headerText)
        }

        binding.baseInclude.radioGroup.setOnCheckedChangeListener  { _, choiceId ->
            when(choiceId) {
                R.id.student -> {
                    binding.studentInclude.studentSection.visibility = View.VISIBLE
                    binding.employeeInclude.employeeSection.visibility = View.GONE
                }
                R.id.worker -> {
                    binding.studentInclude.studentSection.visibility = View.GONE
                    binding.employeeInclude.employeeSection.visibility = View.VISIBLE
                }
            }
        }

        binding.cancelBtn.setOnClickListener {
            clearFields(binding.baseInclude.baseDetailsSection)
            clearFields(binding.studentInclude.studentSection)
            clearFields(binding.employeeInclude.employeeSection)
            clearFields(binding.additionalDetailsInclude.additionalDetailSection)
        }
    }

    private fun initSpinners() {
        val nationalities = resources.getStringArray(R.array.nationalities)
        val arrayNationalitiesAdapter = ArrayAdapter(this, R.layout.list_item, nationalities)
        val sectors = resources.getStringArray(R.array.sectors)
        val arraySectorsAdapter = ArrayAdapter(this, R.layout.list_item, sectors)

        binding.baseInclude.nationalitySpinner.setAdapter(arrayNationalitiesAdapter)
        binding.employeeInclude.sectorsSpinner.setAdapter(arraySectorsAdapter)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


    /**
     * @see: Clear all Text fields from an ConstraintLayout
     */
    private fun clearFields(constraintLayout: ConstraintLayout) {
        val countField = constraintLayout.childCount
        for(i in 0..countField) {
            val view = constraintLayout.getChildAt(i)
            if (view is TextInputLayout) {
                view.editText?.text?.clear()
            } else if (view is RadioGroup) {
                view.clearCheck()
                binding.employeeInclude.employeeSection.visibility = View.GONE
                binding.studentInclude.studentSection.visibility = View.GONE
            }
        }
    }
}