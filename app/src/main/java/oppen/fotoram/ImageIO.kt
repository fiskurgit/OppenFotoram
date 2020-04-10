package oppen.fotoram

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


object ImageIO {

    fun storeImage(context: Context, widgetId: Int, imageUri: Uri?, highQuality: Boolean): Uri? {
        if(imageUri == null) return null
        val scale: Int = if (highQuality) {
            1
        } else {
            2
        }

        val bitmap = generateTargetWidthBitmap(context, imageUri, scale) ?: return null
        val filename: String = imageFilename(widgetId)

        saveFile(context, filename, bitmap)
        val imagePath = File(context.filesDir, ".")
        val storedFile = File(imagePath, filename)
        return FileProvider.getUriForFile(context, "oppen.fotoram", storedFile)
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
            return BitmapFactory.decodeStream(it, null, factoryOptions)
        }
    }

    fun imageFilename(widgetId: Int): String = "PHOTO_WIDGET_ORIGINAL_IMAGE_$widgetId"
}