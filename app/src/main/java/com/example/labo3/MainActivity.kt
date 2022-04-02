package com.example.labo3

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.labo3.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var uri: Uri
    private lateinit var currentImagePath: String
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
            binding.baseInclude.birthdayTextField.editText?.setText(datePicker.headerText)
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

        binding.additionalDetailsInclude.imageView.setOnClickListener {
            invokeCamera()
        }
    }

    private fun initSpinners() {
        val nationalities = resources.getStringArray(R.array.nationalities)
        val arrayNationalitiesAdapter = ArrayAdapter(this, R.layout.list_item, nationalities)
        val sectors = resources.getStringArray(R.array.sectors)
        val arraySectorsAdapter = ArrayAdapter(this, R.layout.list_item, sectors)

        binding.baseInclude.autoCompleteTextView.setAdapter(arrayNationalitiesAdapter)
        binding.employeeInclude.autoCompleteTextView.setAdapter(arraySectorsAdapter)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun invokeCamera() {
        val file = createImageFile()
        try {
            uri = FileProvider.getUriForFile(this, "com.example.labo3.provider", file)
        } catch (e : Exception) {
            Log.e(TAG, "Error: ${e.message}")
        }
        getCameraImage.launch(uri)
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
        if(success) {
            Log.i(TAG, "Image location: $uri")
            renderImage(currentImagePath)
            File(currentImagePath).delete()
        } else {
            Log.i(TAG, "Image location: $uri")
        }
    }

    private fun createImageFile() : File {
        val imageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "file_${Date().time}",
                ".jpg",
            imageDirectory
        ).apply {
            currentImagePath = absolutePath
        }
    }

    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val rotatedBitmap = rotateImage(currentImagePath,bitmap)
            binding.additionalDetailsInclude.imageView.setImageBitmap(rotatedBitmap)
        }
        else {
            Log.i(TAG,"ImagePath is null")
        }
    }

    private fun rotateImage(imagePath: String, source: Bitmap): Bitmap? {
        var source: Bitmap = source
        val ei = ExifInterface(imagePath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> source = rotateImageByAngle(source, 90.toFloat())
            ExifInterface.ORIENTATION_ROTATE_180 -> source = rotateImageByAngle(source, 180.toFloat())
            ExifInterface.ORIENTATION_ROTATE_270 -> source = rotateImageByAngle(source, 270.toFloat())
        }
        return source
    }

    fun rotateImageByAngle(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}

