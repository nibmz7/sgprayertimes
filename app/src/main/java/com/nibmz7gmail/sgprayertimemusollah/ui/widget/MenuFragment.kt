package com.nibmz7gmail.sgprayertimemusollah.ui.widget

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


		navigation_view.setNavigationItemSelectedListener { menuItem ->
			// Bottom Navigation Drawer menu item clicks
			Toast.makeText(context, "lol", Toast.LENGTH_LONG).show()
			true
		}

	}

}