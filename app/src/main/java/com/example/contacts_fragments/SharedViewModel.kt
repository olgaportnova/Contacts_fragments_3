package com.example.contacts_fragments

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private var contactsList: MutableList<Contact> = generateListContacts().toMutableList()

    fun updateContacts(newContactsList: List<Contact>) {
        contactsList.clear()
        contactsList.addAll(newContactsList)
    }

    fun getContactList() : List<Contact> {
        return contactsList.toList()
    }

}
