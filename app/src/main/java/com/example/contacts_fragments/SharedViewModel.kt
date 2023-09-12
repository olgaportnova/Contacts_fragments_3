package com.example.contacts_fragments

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private var contactsList = mutableListOf(
        Contact(1,"Оля", "Бадосова", "+7 (904) 610-84-44"),
        Contact(2, "Иван", "Иванов", "+7 (904) 000-04-42"),
        Contact(3, "Илья", "Петров", "+7 (904) 123-45-67"),
        Contact(4,"Паша", "Смирнов", "+7 (904) 123-99-99")
    )



    fun updateContacts(newContactsList: List<Contact>) {
        contactsList.clear()
        contactsList.addAll(newContactsList)
    }

    fun getContactList() : List<Contact> {
        return contactsList
    }
}
