package com.nibmz7gmail.sgprayertimemusollah.ui.widget

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.nibmz7gmail.sgprayertimemusollah.R
import kotlinx.android.synthetic.main.frag_bottomsheet_menu.*

class MenuFragment: RoundedBottomSheetDialogFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.frag_bottomsheet_menu, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


		navigation_view.setNavigationItemSelectedListener {
			// Bottom Navigation Drawer menu item clicks
			when(it.itemId) {
				R.id.rate -> {
					try {
						startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nibmz7gmail.sgprayertimemusollah")))
					} catch (anfe: android.content.ActivityNotFoundException) {
						startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.nibmz7gmail.sgprayertimemusollah")))
					}
				}
				R.id.share -> {
					val sharingIntent = Intent(Intent.ACTION_SEND)
					sharingIntent.type = "text/plain"
					sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SG Masjids & prayer times ")
					sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.nibmz7gmail.sgprayertimemusollah")
					startActivity(Intent.createChooser(sharingIntent, "Share via"))
				}
				R.id.githublink -> {
					startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nurilyas7/sgprayertimes")))
				}
				R.id.credits -> {

				}
			}
			true
		}

	}

}