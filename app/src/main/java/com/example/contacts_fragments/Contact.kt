package com.example.contacts_fragments

import com.example.contacts_fragments.data.Names
import kotlin.random.Random

data class Contact(
    val id:Int,
    val name: String,
    val surname:String,
    val number:String,
    val imageUrl:String
)

private fun generateRandomContact(id: Int): Contact {
    val randomName = Names.names.random()
    val randomSurname = Surnames.surnames.random()
    val number = generateRandomPhoneNumber()
    val imageUrl = "https://picsum.photos/160/160?random=$id"

    return Contact(id, randomName, randomSurname, number, imageUrl)
}

fun generateListContacts(): List<Contact> {
    return List(150) { index -> generateRandomContact(index + 1) }
}

private fun generateRandomPhoneNumber(): String {
    return "+7${(900..999).random()}${Random.nextInt(10, 99)}${Random.nextInt(1000, 9999)}"
}


