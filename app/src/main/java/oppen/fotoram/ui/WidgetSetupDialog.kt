package oppen.fotoram.ui

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.setup_dialog.view.*
import oppen.fotoram.R

class WidgetSetupDialog(
  private val imageUri: Uri?,
  private val onChangeImage: () -> Unit,
  private val onCancel:() -> Unit,
  private val onBuild: (dialog: WidgetSetupDialog, uri: Uri?, highQuality: Boolean) -> Unit): BottomSheetDialogFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.setup_dialog, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    view.preview_image.setImageURI(imageUri)

    view.change_image_button.setOnClickListener {
      dismiss()
      onChangeImage.invoke()
    }

    view.cancel_widget_button.setOnClickListener {
      dismiss()
    }

    view.make_widget_button.setOnClickListener {
      onBuild(this, imageUri, view.quality_switch.isChecked)
    }
  }
}