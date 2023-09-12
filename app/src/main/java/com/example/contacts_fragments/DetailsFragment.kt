package com.example.contacts_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.contacts_fragments.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    companion object {
        private const val ARGS_ID = "id"
        private const val ARGS_NAME = "name"
        private const val ARGS_SURNAME = "surname"
        private const val ARGS_NUMBER = "number"
        private const val ARGS_STATE = "state"
        const val TAG = "com.example.contacts_fragments.DetailsFragment"

        fun newInstance(id: Int, name: String, surname: String, number: String, state: Int): DetailsFragment {
            return DetailsFragment().apply {
                arguments = bundleOf(
                    ARGS_ID to id,
                    ARGS_NAME to name,
                    ARGS_SURNAME to surname,
                    ARGS_NUMBER to number,
                    ARGS_STATE to state
                )
            }
        }
    }

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentDetailsBinding
    private var contactId: Int = 0
    private var originalName: String = ""
    private var originalSurname: String = ""
    private var originalNumber: String = ""
    private var state: Int = 0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RootActivity) {
            sharedViewModel = context.getSharedViewModel()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveArguments()
        bindArguments()
        setClickListeners()
    }

    private fun retrieveArguments() {
        contactId = requireArguments().getInt(ARGS_ID)
        originalName = requireArguments().getString(ARGS_NAME) ?: ""
        originalSurname = requireArguments().getString(ARGS_SURNAME) ?: ""
        originalNumber = requireArguments().getString(ARGS_NUMBER) ?: ""
        state = requireArguments().getInt(ARGS_STATE)
    }

    private fun bindArguments() {
        setRightFragmentVisibility(state)
        setContactDetails(originalName, originalSurname, originalNumber)
    }

    private fun setClickListeners() {
        binding.btEdit.setOnClickListener {
            enableEditing(true)
        }
        binding.btSave.setOnClickListener {
            saveContactChanges()
        }
    }

    private fun enableEditing(isEnabled: Boolean) {
        listOf(binding.name, binding.surname, binding.number).forEach { it.isEnabled = isEnabled }
    }

    private fun setContactDetails(name: String, surname: String, number: String) {
        binding.name.setText(name)
        binding.surname.setText(surname)
        binding.number.setText(number)
    }

    private fun saveContactChanges() {

        if (isContactChanged()) {
            val newContact = createContactWithNewDetails(contactId)
            updateContactInViewModel(newContact)
        } else if (binding.name.isEnabled) {
            Toast.makeText(requireContext(), R.string.nothing_changed, Toast.LENGTH_SHORT).show()
        }
        enableEditing(false)

        if (isSmartphoneLayout()) {
            parentFragmentManager.popBackStack()
        } else {
            val contactsFragment = parentFragmentManager.findFragmentByTag("ContactsFragmentTag") as? ContactsFragment
            contactsFragment?.refreshData()
        }

    }



    private fun createContactWithNewDetails(id: Int): Contact {
        return Contact(id,
            binding.name.text.toString(),
            binding.surname.text.toString(),
            binding.number.text.toString())
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
        }
    }

    private fun isSmartphoneLayout(): Boolean {
        return resources.configuration.smallestScreenWidthDp < 600
    }
}
