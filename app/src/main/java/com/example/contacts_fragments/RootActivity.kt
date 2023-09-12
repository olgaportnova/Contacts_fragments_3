package com.example.contacts_fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class RootActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        if (isSmartphoneLayout()) {
            setSmartphoneLayout()
        } else {
            setTabletLayout()
        }
    }

    fun getSharedViewModel(): SharedViewModel {
        return sharedViewModel
    }

    private fun isSmartphoneLayout(): Boolean {
        return resources.configuration.smallestScreenWidthDp < 600 }

    private fun setSmartphoneLayout() {
        setContentView(R.layout.activity_root_tablet)
        val fragment = ContactsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun setTabletLayout() {
        setContentView(R.layout.activity_root_tablet)
        val fragment1 = ContactsFragment()
        val fragment2 = createDetailsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.leftFragment, fragment1, "ContactsFragmentTag")
            .replace(R.id.rightFragment, fragment2, "DetailsFragmentTag")
            .commit()
    }

    private fun createDetailsFragment(): DetailsFragment {
        return DetailsFragment.newInstance(0, "", "", "", ContactsFragment.STATE_SHOW_PLACEHOLDER)
    }
}
