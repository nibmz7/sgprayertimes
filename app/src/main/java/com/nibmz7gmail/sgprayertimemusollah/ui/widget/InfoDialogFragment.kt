package com.nibmz7gmail.sgprayertimemusollah.ui.widget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import kotlinx.android.synthetic.main.dialog_info.*

class InfoDialogFragment:  AppCompatDialogFragment() {

	companion object {
		const val DIALOG_INFO = "dialog_info"
		private const val TITLE = "my_title"
		private const val DETAILS = "my_details"

		fun newInstance(title: String, details: String) = InfoDialogFragment().apply {
			arguments = Bundle(1).apply {
				putString(TITLE, title)
				putString(DETAILS, details)
			}
		}
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return CustomDimDialog(context, true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.dialog_info, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


		isCancelable = true

		button_dismiss.setOnClickListener { dismiss() }

		info_title.text = arguments?.getString(TITLE)
		info_details.text = arguments?.getString(DETAILS)
	}



}