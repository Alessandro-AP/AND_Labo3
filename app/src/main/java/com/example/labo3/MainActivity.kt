package com.example.labo3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.labo3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioGroup.setOnCheckedChangeListener { _, choiceId ->
            when(choiceId) {
                R.id.student -> {
                    binding.workerGroup.visibility = View.GONE
                    binding.studentGroup.visibility = View.VISIBLE
                }
                R.id.worker -> {
                    binding.studentGroup.visibility = View.GONE
                    binding.workerGroup.visibility = View.VISIBLE
                }
            }
        }
    }
}