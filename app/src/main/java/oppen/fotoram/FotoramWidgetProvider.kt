package oppen.fotoram

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import java.io.File

class FotoramWidgetProvider : AppWidgetProvider() {

  override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)

    if(context==null || appWidgetManager ==null)return

    val views = RemoteViews(context.packageName, R.layout.widget_layout)

    appWidgetIds?.forEach { wId ->
      val imagePath = File(context.filesDir, ".")
      val sourceFile = File(imagePath, ImageIO.imageFilename(wId))
      views.setImageViewUri(R.id.fotoram_widget_image, Uri.parse(""))
      views.setImageViewUri(R.id.fotoram_widget_image, FileProvider.getUriForFile(context, "oppen.fotoram", sourceFile))
      appWidgetManager.updateAppWidget(wId, views)
    }
  }

  override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    super.onDeleted(context, appWidgetIds)

    if(context == null) return

    appWidgetIds?.forEach {wId ->
      val imagePath = File(context.filesDir, ".")
      val filename: String = ImageIO.imageFilename(wId)
      val original = File(imagePath, filename)
      val originalDeleted = original.delete()
      if (!originalDeleted) Log.e("Fotoram", "Fotoram - failed to remove image while cleaning up")
    }
  }
}