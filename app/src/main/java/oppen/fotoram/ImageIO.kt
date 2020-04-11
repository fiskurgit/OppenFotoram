package oppen.fotoram

import android.R.attr.rotation
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File


object ImageIO {

    var requiredRotation = 0

    fun storeImage(context: Context, widgetId: Int, imageUri: Uri?, highQuality: Boolean, callback: (uri: Uri?) -> Unit) {
        if(imageUri == null){
            fail(callback)
            return
        }

        val scale: Int = if (highQuality) {
            1
        } else {
            2
        }

        val bitmap = generateTargetWidthBitmap(context, imageUri, scale)
        if(bitmap == null){
            fail(callback)
            return
        }
        val filename: String = imageFilename(widgetId)

        saveFile(context, filename, bitmap)
        val imagePath = File(context.filesDir, ".")
        val storedFile = File(imagePath, filename)

        callback.invoke(FileProvider.getUriForFile(context, "oppen.fotoram", storedFile))
    }

    private fun fail(callback: (uri: Uri?) -> Unit){
        callback.invoke(null)
    }

    private fun saveFile(context: Context, filename: String, bitmap: Bitmap) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
    }

    private fun generateTargetWidthBitmap(context: Context, uri: Uri, scale: Int): Bitmap?{
        val targetWidth: Int = Resources.getSystem().displayMetrics.widthPixels / scale
        val factoryOptions = BitmapFactory.Options()
        factoryOptions.inJustDecodeBounds = true

        context.contentResolver.openInputStream(uri).use{
            if(it == null) return null

            val exif = ExifInterface(it)
            val exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            requiredRotation = exifRotationToDegrees(exifRotation)

            BitmapFactory.decodeStream(it, null, factoryOptions)
            val originalWidth = factoryOptions.outWidth
            when {
                originalWidth > targetWidth -> {
                    val scaleFactor = originalWidth / targetWidth
                    factoryOptions.inSampleSize = scaleFactor
                }
            }
        }

        context.contentResolver.openInputStream(uri).use{

            factoryOptions.inJustDecodeBounds = false

            return when {
                requiredRotation != 0 -> {
                    val bitmap = BitmapFactory.decodeStream(it, null, factoryOptions) ?: return null
                    val matrix = Matrix()
                    if(requiredRotation != 0) matrix.preRotate(requiredRotation.toFloat())
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
                }
                else -> {
                    BitmapFactory.decodeStream(it, null, factoryOptions)
                }
            }
        }
    }

    private fun exifRotationToDegrees(exifOrientation: Int): Int{
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    fun imageFilename(widgetId: Int): String = "PHOTO_WIDGET_ORIGINAL_IMAGE_$widgetId"
}