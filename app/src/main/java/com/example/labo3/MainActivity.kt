// AND - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra, Wilfried Karel ◕◡◕
// Date: 02.04.22

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
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.labo3.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.util.*
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputLayout

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
            MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
                .build()

        binding.baseInclude.birthdayButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
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

        binding.additionalDetailsInclude.photoIV.setOnClickListener {
            invokeCamera()
        }

        binding.cancelBtn.setOnClickListener {
            clearFields(binding.baseInclude.baseDetailsSection)
            clearFields(binding.studentInclude.studentSection)
            clearFields(binding.employeeInclude.employeeSection)
            clearFields(binding.additionalDetailsInclude.additionalDetailSection)
        }
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
        if(success) {
            Log.i(TAG, "Image location: $uri")
            renderImage(currentImagePath, binding.additionalDetailsInclude.photoIV)
            File(currentImagePath).delete()
        }
    }

    /**
     * Initialise spinners data in the layout.
     */
    private fun initSpinners() {
        val nationalities = resources.getStringArray(R.array.nationalities)
        val arrayNationalitiesAdapter = ArrayAdapter(this, R.layout.list_item, nationalities)
        val sectors = resources.getStringArray(R.array.sectors)
        val arraySectorsAdapter = ArrayAdapter(this, R.layout.list_item, sectors)

        binding.baseInclude.nationalitySpinner.setAdapter(arrayNationalitiesAdapter)
        binding.employeeInclude.sectorsSpinner.setAdapter(arraySectorsAdapter)
    }

/*    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }*/

    /**
     * Retrieves the image uri using the file provider
     * and launch an explicit intent to start the camera
     */
    private fun invokeCamera() {
        try {
            val file = createImageFile()
            uri = FileProvider.getUriForFile(this, "com.example.labo3.provider", file)
        } catch (e : Exception) {
            Log.e(TAG, "Error: ${e.message}")
        }
        getCameraImage.launch(uri)
    }

    /**
     * Create a temporary file with '.jpg' extension
     * in the external file directory and return it.
     */
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

    /**
     * Set an ImageView using the path of a given image.
     */
    private fun renderImage(imagePath: String?, imageView: ImageView){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val rotatedBitmap = rotateImage(currentImagePath,bitmap)
            imageView.setImageBitmap(rotatedBitmap)
        }
        else {
            Log.i(TAG,"ImagePath is null")
        }
    }

    /**
     * It retrieves the image rotation information and
     * create a new bitmap image correctly rotated.
     *
     * @param imagePath path of the image
     * @param source bitmap image
     * @return the new rotated bitmap
     */
    private fun rotateImage(imagePath: String, source: Bitmap): Bitmap {
        var result: Bitmap = source
        val ei = ExifInterface(imagePath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> result = rotateImageByAngle(result, 90.toFloat())
            ExifInterface.ORIENTATION_ROTATE_180 -> result = rotateImageByAngle(result, 180.toFloat())
            ExifInterface.ORIENTATION_ROTATE_270 -> result = rotateImageByAngle(result, 270.toFloat())
        }
        return result
    }

    /**
     * Rotates a bitmap image from a rotation angle.
     *
     * @param source bitmap image
     * @param angle rotation angle
     * @return a new bitmap image
     */
    private fun rotateImageByAngle(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    /**
     * Clear all Text fields from an ConstraintLayout
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