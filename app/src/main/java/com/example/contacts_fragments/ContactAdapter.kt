package com.example.contacts_fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts_fragments.databinding.ListItemContactBinding

class ContactAdapter(
    private val contactsList: MutableList<Contact>,
    private val listener: Listener
) : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    inner class ContactHolder(private val binding: ListItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.apply {
                name.text = contact.name
                surname.text = contact.surname
                number.text = contact.number
                root.setOnClickListener { listener.onClick(contact) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val binding = ListItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactHolder(binding)
    }

    override fun getItemCount(): Int = contactsList.size

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.bind(contactsList[position])
    }

    interface Listener {
        fun onClick(contact: Contact)
    }

    fun updateData(newContacts: List<Contact>) {
        contactsList.clear()
        contactsList.addAll(newContacts)
        notifyDataSetChanged()
    }

}
