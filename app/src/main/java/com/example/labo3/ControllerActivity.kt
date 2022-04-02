// AND - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra, Wilfried Karel ◕◡◕
// Date: 02.04.22

package com.example.labo3

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.labo3.ImageUtils.Companion.renderImage
import com.example.labo3.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ControllerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding // Binding used for link with the layout.

    private var uri: Uri = Uri.EMPTY // Uri used to refers an image in our storage.
    private var currentImagePath: String = "" // Current image path.
    private  var person : Person? = null // Current Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinners()

        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
                .build()

        datePicker.addOnPositiveButtonClickListener {timeInMillis ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = timeInMillis
            val dateStr = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.time)

            binding.baseInclude.birthdayET.editText?.setText(dateStr)
        }

        binding.baseInclude.birthdayButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        // Shows or hides the student and worker sections according to the choice of the radioGroup
        binding.baseInclude.radioGroup.setOnCheckedChangeListener  { _, choiceId ->
            when(choiceId) {
                R.id.studentBtn -> {
                    binding.studentInclude.studentSection.visibility = View.VISIBLE
                    binding.employeeInclude.employeeSection.visibility = View.GONE
                }
                R.id.workerBtn -> {
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
            clearPicture()
            person = null
        }

        binding.okBtn.setOnClickListener {
            if(!binding.baseInclude.studentBtn.isChecked && !binding.baseInclude.workerBtn.isChecked){
                Toast.makeText(applicationContext,"You need to check an occupation", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Note: we suppose that the user has entered all the data correctly !
            person = createPerson()
            println("Result:\n $person")
        }
    }

    /**
     * Define a ActivityResultLauncher object that contains an ActivityResultContract
     * that allows us to take a picture and saving it into the provided content-Uri.
     *
     * The callback function will be invoked when the result is received.
     */
    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
        if(success) {
            Log.i(TAG, "Image location: $uri")
            renderImage(currentImagePath, binding.additionalDetailsInclude.photoIV)
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
     * Clear all EditText fields and RadioButtons from a ConstraintLayout
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

    /**
     * Reset the picture field and delete the picture if necessary.
     */
    private fun clearPicture() {
        if (currentImagePath != "") {
            val file = File(currentImagePath)
            if (file.exists())
                file.delete()
        }
        binding.additionalDetailsInclude.photoIV.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.placeholder_selfie
            )
        )

        // Reset variables
        uri = Uri.EMPTY
        currentImagePath = ""
    }

    /**
     * Create a new person using the data from each form.
     * The person will be of type Student or Worker
     * according to the choice provided by the user (present in the RadioGroup).
     *
     * @return the new person
     */
    private fun createPerson() : Person {
        // Basic details
        val firstName = binding.baseInclude.firstnameET.editText?.text.toString()
        val lastName = binding.baseInclude.lastNameET.editText?.text.toString()
        val nationality =  binding.baseInclude.nationalitySpinnerContainer.editText?.text.toString()
        // Parse birthday String to Calendar
        val birthdayStr = binding.baseInclude.birthdayET.editText?.text.toString()
        val birthdayCal = Calendar.getInstance()
        if(birthdayStr != ""){
            val birthdayDate = SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(birthdayStr)!!
            birthdayCal.time = birthdayDate
        }

        // Additional details
        val email = binding.additionalDetailsInclude.mailET.editText?.text.toString()
        val path = currentImagePath
        val remarks = binding.additionalDetailsInclude.remarksET.editText?.text.toString()

        when {
            binding.baseInclude.studentBtn.isChecked -> {
                val university = binding.studentInclude.schoolET.editText?.text.toString()
                val graduationYearStr = binding.studentInclude.graduationyearET.editText?.text.toString()
                val graduationYear = if (graduationYearStr != "") graduationYearStr.toInt() else 0
                return Student(lastName,
                    firstName,
                    birthdayCal,
                    nationality,
                    university,
                    graduationYear,
                    email,
                    remarks,
                    path)
            }
            binding.baseInclude.workerBtn.isChecked -> {
                val company = binding.employeeInclude.companyET.editText?.text.toString()
                val sector = binding.employeeInclude.sectorsSpinnerContainer.editText?.text.toString()
                val experienceStr = binding.employeeInclude.experienceET.editText?.text.toString()
                val experience = if (experienceStr != "") experienceStr.toInt() else 0
                return Worker(lastName,
                    firstName,
                    birthdayCal,
                    nationality,
                    company,
                    sector,
                    experience,
                    email,
                    remarks,
                    path)
            }
            else -> {
                throw Exception("Error: no choice has been made for occupation")
            }
        }
    }
}