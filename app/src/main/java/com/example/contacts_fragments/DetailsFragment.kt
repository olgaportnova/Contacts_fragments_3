package com.example.contacts_fragments

import ContactsFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.contacts_fragments.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    companion object {
        const val ARGS_ID = "id"
        const val ARGS_NAME = "name"
        const val ARGS_SURNAME = "surname"
        const val ARGS_NUMBER = "number"
        const val ARGS_IMAGEURL = "imageUrl"
        const val ARGS_STATE = "state"

        const val DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT_KEY"
        const val DETAILS_FRAGMENT_ID = 2


        fun newInstance(
            id: Int,
            name: String,
            surname: String,
            number: String,
            state: Int,
            imageUrl: String
        ): DetailsFragment {
            return DetailsFragment().apply {
                arguments = bundleOf(
                    ARGS_ID to id,
                    ARGS_NAME to name,
                    ARGS_SURNAME to surname,
                    ARGS_NUMBER to number,
                    ARGS_IMAGEURL to imageUrl,
                    ARGS_STATE to state
                )
            }
        }
    }

    private lateinit var binding: FragmentDetailsBinding
    private var contactId: Int = 0
    private var originalName: String = ""
    private var originalSurname: String = ""
    private var originalNumber: String = ""
    private var imageUrl: String = ""
    private var state: Int = 0

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            originalName = savedInstanceState.getString(ARGS_NAME) ?: ""
            originalSurname = savedInstanceState.getString(ARGS_SURNAME) ?: ""
            originalNumber = savedInstanceState.getString(ARGS_NUMBER) ?: ""
        } else {
            retrieveArguments()
        }
        bindArguments()
        setClickListeners()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(DETAILS_FRAGMENT_TAG, DETAILS_FRAGMENT_ID)
        outState.putString(ARGS_NAME, binding.name.text.toString())
        outState.putString(ARGS_SURNAME, binding.surname.text.toString())
        outState.putString(ARGS_NUMBER, binding.number.text.toString())
        outState.putString(ARGS_IMAGEURL, imageUrl)
        outState.putInt(ARGS_STATE, state)
        outState.putInt(ARGS_ID, contactId)

    }

    private fun retrieveArguments() {
        contactId = requireArguments().getInt(ARGS_ID)
        originalName = requireArguments().getString(ARGS_NAME) ?: ""
        originalSurname = requireArguments().getString(ARGS_SURNAME) ?: ""
        originalNumber = requireArguments().getString(ARGS_NUMBER) ?: ""
        imageUrl = requireArguments().getString(ARGS_IMAGEURL) ?: ""
        state = requireArguments().getInt(ARGS_STATE)
    }

    private fun bindArguments() {
        setRightFragmentVisibility(state)
        setContactDetails(originalName, originalSurname, originalNumber, imageUrl)
    }

    private fun setClickListeners() {
        binding.btEdit.setOnClickListener {
            enableEditing(true)
        }
        binding.btSave.setOnClickListener {
            saveContactChangesAndRefresh()
        }
    }

    private fun enableEditing(isEnabled: Boolean) {
        listOf(binding.name, binding.surname, binding.number).forEach { it.isEnabled = isEnabled }
    }

    private fun setContactDetails(name: String, surname: String, number: String, imageUrl: String) {
        binding.name.setText(name)
        binding.surname.setText(surname)
        binding.number.setText(number)
        Glide.with(binding.root.context)
            .load(imageUrl)
            .placeholder(R.drawable.placehoder_person)
            .circleCrop()
            .into(binding.imageContact)


    }

    private fun saveContactChangesAndRefresh() {
        val contactOldList = sharedViewModel.getContactList()
        enableEditing(false)

        if (isContactChanged()) {
            val newContact = createContactWithNewDetails(contactId, imageUrl)
            updateContactInViewModel(newContact)
            val contactNewList = sharedViewModel.getContactList()

            handleNavigationAfterSavingChanges(contactOldList, contactNewList)
        } else {
            Toast.makeText(requireContext(), R.string.nothing_changed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNavigationAfterSavingChanges(oldList: List<Contact>, newList: List<Contact>) {
        if (isSmartphoneLayout()) {
            parentFragmentManager.popBackStack()
        } else {
            refreshContactsFragment(oldList, newList)
        }
    }


    private fun refreshContactsFragment(oldList: List<Contact>, newList: List<Contact>) {
        val contactsFragment =
            parentFragmentManager.findFragmentByTag("ContactsFragmentTag") as? ContactsFragment
        contactsFragment?.refreshContactList(oldList, newList)
    }


    private fun createContactWithNewDetails(id: Int, imageUrl: String): Contact {
        return Contact(
            id,
            binding.name.text.toString(),
            binding.surname.text.toString(),
            binding.number.text.toString(),
            imageUrl
        )
    }

    private fun isContactChanged(): Boolean {
        return (originalName != binding.name.text.toString() ||
                originalSurname != binding.surname.text.toString() ||
                originalNumber != binding.number.text.toString())
    }

    private fun updateContactInViewModel(newContact: Contact) {
        val currentContacts = sharedViewModel.getContactList()
        val existingContactPosition = currentContacts.indexOfFirst { it.id == newContact.id }
        if (existingContactPosition != -1) {
            val updatedContactList = currentContacts.toMutableList()
            updatedContactList[existingContactPosition] = newContact
            sharedViewModel.updateContacts(updatedContactList)
        }


    }

    private fun setRightFragmentVisibility(visibility: Int) {
        with(binding) {
            val isPlaceholderVisible = visibility == ContactsFragment.STATE_SHOW_PLACEHOLDER

            imagePlacholder.visibility = if (isPlaceholderVisible) View.VISIBLE else View.GONE
            textPlaceholder.visibility = if (isPlaceholderVisible) View.VISIBLE else View.GONE
            contactDetails.visibility = if (isPlaceholderVisible) View.GONE else View.VISIBLE
            imageContact.visibility = if (isPlaceholderVisible) View.GONE else View.VISIBLE
        }
    }

    private fun isSmartphoneLayout(): Boolean {
        return resources.configuration.smallestScreenWidthDp < 600
    }
}
