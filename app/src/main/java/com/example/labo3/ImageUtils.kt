// AND - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra, Wilfried Karel Ngueukam Djeuda ◕◡◕
// Date: 02.04.22

package com.example.labo3

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.widget.ImageView

/**
 * Utility class for image handling.
 */
class ImageUtils {
    companion object {

        /**
         * Set an ImageView using the path of a given image.
         */
        fun renderImage(imagePath: String?, imageView: ImageView) {
            if (imagePath != null) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                val rotatedBitmap = rotateImage(imagePath, bitmap)
                imageView.setImageBitmap(rotatedBitmap)
            } else {
                Log.i(ContentValues.TAG, "ImagePath is null")
            }
        }

        /**
         * Retrieve the image rotation information and create a new bitmap image correctly rotated.
         *
         * @param imagePath path of the image
         * @param source bitmap image
         * @return the new rotated bitmap
         */
        private fun rotateImage(imagePath: String, source: Bitmap): Bitmap {
            var result: Bitmap = source
            val ei = ExifInterface(imagePath)
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> result =
                    rotateImageByAngle(result, 90.toFloat())
                ExifInterface.ORIENTATION_ROTATE_180 -> result =
                    rotateImageByAngle(result, 180.toFloat())
                ExifInterface.ORIENTATION_ROTATE_270 -> result =
                    rotateImageByAngle(result, 270.toFloat())
            }
            return result
        }

        /**
         * Rotate a bitmap image from a rotation angle.
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
    }
}