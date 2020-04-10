package oppen.fotoram

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import oppen.fotoram.ui.WidgetSetupDialog

private const val SELECT_PICTURE_REQ = 0

class WidgetSetupActivity : AppCompatActivity() {

  private var widgetId: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: -1
    openImageIntent()
  }

  private fun openImageIntent() {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_REQ)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when {
      resultCode == RESULT_OK && requestCode == SELECT_PICTURE_REQ -> {
        val dialog = WidgetSetupDialog(data?.data, {
          //onChangeImage
          openImageIntent()
        }, {
          //onCancel
          finish()
        }) { dialog, uri, highQuality ->
          //onBuild
          buildWidget(dialog, uri, highQuality)
        }
        dialog.show(supportFragmentManager.beginTransaction(), "widget_setup")
      }
      else -> {
        setResult(Activity.RESULT_CANCELED)
        finish()
      }
    }
  }

  private fun buildWidget(dialog: WidgetSetupDialog, uri: Uri?, highQuality: Boolean){
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val views = RemoteViews(packageName, R.layout.widget_layout)

    val osUri = ImageIO.storeImage(this, widgetId, uri, highQuality)
      views.setImageViewUri(R.id.fotoram_widget_image, Uri.parse(""))
      views.setImageViewUri(R.id.fotoram_widget_image, osUri)
      appWidgetManager.updateAppWidget(widgetId, views)

      val resultValue = Intent()
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
      setResult(Activity.RESULT_OK, resultValue)

      finish()
      dialog.dismiss()
  }
}
