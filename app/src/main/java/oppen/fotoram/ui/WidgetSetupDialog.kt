package oppen.fotoram.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.setup_dialog.view.*
import oppen.fotoram.R

class WidgetSetupDialog(
  private val imageUri: Uri?,
  private val onChangeImage: () -> Unit,
  private val onCancel:() -> Unit,
  private val onBuild: (dialog: WidgetSetupDialog, uri: Uri?, highQuality: Boolean) -> Unit): BottomSheetDialogFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    //return inflater.inflate(R.layout.setup_dialog, container, false)
    val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
    return inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.setup_dialog, container, false)

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    view.preview_image.setImageURI(imageUri)

    view.change_image_button.setOnClickListener {
      dismiss()
      onChangeImage.invoke()
    }

    view.cancel_widget_button.setOnClickListener {
      onCancel.invoke()
      dismiss()
    }

    view.make_widget_button.setOnClickListener {

      val highQuality = view.quality_switch.isChecked

      view.progress.visibility = View.VISIBLE
      view.make_widget_button.isEnabled = false
      view.change_image_button.isEnabled = false
      view.cancel_widget_button.isEnabled = false
      view.quality_switch.isEnabled = false

      onBuild(this, imageUri, highQuality)
    }
  }
}