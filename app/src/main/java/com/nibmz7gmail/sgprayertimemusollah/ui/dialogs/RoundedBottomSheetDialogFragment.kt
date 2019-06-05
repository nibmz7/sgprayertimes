package com.nibmz7gmail.sgprayertimemusollah.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nibmz7gmail.sgprayertimemusollah.R



open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

	override fun getTheme(): Int = R.style.BottomSheetDialogTheme

	//https://stackoverflow.com/questions/47909119/bottomsheetcallback-from-modal-bottomsheetfragment
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialog = BottomSheetDialog(requireContext(), theme)

		dialog.setOnShowListener { dialog2 ->

			val d = dialog2 as BottomSheetDialog

			val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

			val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

			// change the state of the bottom sheet
			bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

			// set callback for changes
			bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
				override fun onStateChanged(bottomSheet: View, newState: Int) {
					// Called every time when the bottom sheet changes its state.
					if (newState == BottomSheetBehavior.STATE_HIDDEN) {
						dismiss()
					}
				}

				override fun onSlide(bottomSheet: View, slideOffset: Float) {
					//For visual appeal
					if(slideOffset.isNaN() || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P)
						return
					d.window?.setDimAmount((slideOffset + 1) * 0.6f)

				}
			})
		}

		return dialog

	}

	override fun onActivityCreated(arg0: Bundle?) {
		super.onActivityCreated(arg0)

		dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
	}


}