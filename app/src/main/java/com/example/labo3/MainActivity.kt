package com.example.labo3

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.labo3.databinding.ActivityMainBinding
import com.example.labo3.databinding.EmployeeSectionBinding
import com.example.labo3.databinding.StudentSectionBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nationalities = resources.getStringArray(R.array.nationalities)
        val arrayAdapter = ArrayAdapter(this, R.layout.list_item, nationalities)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)


        binding.radioGroup.setOnCheckedChangeListener  { _, choiceId ->
            when(choiceId) {
                R.id.student -> {
                    binding.include.studentSection.visibility = View.VISIBLE
                    binding.include2.employeeSection.visibility = View.GONE
                }
                R.id.worker -> {
                    binding.include.studentSection.visibility = View.GONE
                    binding.include2.employeeSection.visibility = View.VISIBLE
                }
            }
        }

    }
}