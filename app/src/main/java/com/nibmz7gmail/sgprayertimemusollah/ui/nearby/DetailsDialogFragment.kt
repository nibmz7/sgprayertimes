package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.ui.dialogs.CustomDimDialog
import kotlinx.android.synthetic.main.dialog_mosque_details.*
import timber.log.Timber
import java.io.IOException

class DetailsDialogFragment:  AppCompatDialogFragment() {

	companion object {
		private const val DETAILS = "details"

		fun newInstance(mosque: Mosque) = DetailsDialogFragment().apply {
			arguments = Bundle(1).apply {
				putParcelable(DETAILS, mosque)
			}
		}
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return CustomDimDialog(context, true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.dialog_mosque_details, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


		isCancelable = true

		val mosque = arguments?.getParcelable(DETAILS) as Mosque
		mosque_name.text = mosque.name
		address.text = mosque.address
		val srcFile = "masjid_" + mosque.id + "-min.jpg"
		requireContext().loadImage(imgMosque, srcFile)
		wc_friendly.visibility = if(mosque.wcFriendly == 1) View.VISIBLE else View.INVISIBLE

		directions.setOnClickListener {
			val address = mosque.name + " " + mosque.address
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$address"))
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
			try {
				context!!.startActivity(intent)

			} catch (ex: ActivityNotFoundException) {
				try {
					val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=$address"))
					requireContext().startActivity(unrestrictedIntent)
				} catch (innerEx: ActivityNotFoundException) {

				}

			}

		}

		facebook.setOnClickListener {
			try {
				requireContext().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mosque.fbPage)))
			} catch (ignored: Exception) {

			}
		}

		share.setOnClickListener {
			val shareText = "Masjid " + mosque.name + "\n" + mosque.address
			val sharingIntent = Intent(Intent.ACTION_SEND)
			sharingIntent.type = "text/plain"
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SG Masjids & prayer times")
			sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText)
			requireContext().startActivity(Intent.createChooser(sharingIntent, "Share via"))
		}
	}

	private fun Context.loadImage(imageView: ImageView, src: String) {

		try {
			val ims = assets.open(src)
			val d = Drawable.createFromStream(ims, null)
			imageView.setImageDrawable(d)
			ims.close()
		} catch (ex: IOException) {
			Timber.e(ex)
		}

	}


}