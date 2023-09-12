package com.example.contacts_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts_fragments.databinding.FragmentContactsBinding

class ContactsFragment : Fragment(), ContactAdapter.Listener {

    companion object {
        const val STATE_SHOW_DETAILS = 1
        const val STATE_SHOW_PLACEHOLDER = 0
    }

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentContactsBinding
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RootActivity) {
            sharedViewModel = context.getSharedViewModel()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contacts = sharedViewModel.getContactList().toMutableList()
        adapter = ContactAdapter(contacts, this)

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = this@ContactsFragment.adapter
        }
    }


    override fun onClick(contact: Contact) {
        if (isSmartphoneLayout()) {
            showDetailsForSmartphone(contact)
        } else {
            showDetailsForTablet(contact)
        }
    }

    private fun isSmartphoneLayout(): Boolean {
        return resources.configuration.smallestScreenWidthDp < 600
    }

    private fun showDetailsForSmartphone(contact: Contact) {
        parentFragmentManager.commit {
            replace(
                R.id.fragmentContainer,
                DetailsFragment.newInstance(
                    id = contact.id,
                    name = contact.name,
                    surname = contact.surname,
                    number = contact.number,
                    state = STATE_SHOW_DETAILS
                ),
                DetailsFragment.TAG
            )
            addToBackStack(DetailsFragment.TAG)
        }
    }

    private fun showDetailsForTablet(contact: Contact) {
        val detailsFragment = DetailsFragment.newInstance(
            id = contact.id,
            name = contact.name,
            surname = contact.surname,
            number = contact.number,
            state = STATE_SHOW_DETAILS
        )
        parentFragmentManager.commit {
            replace(
                R.id.rightFragment,
                detailsFragment,
                DetailsFragment.TAG
            )
        }
    }

    fun refreshData() {
        val newContacts = sharedViewModel.getContactList()
        adapter.updateData(newContacts)
    }
}
