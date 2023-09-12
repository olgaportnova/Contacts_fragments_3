package com.example.contacts_fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.contacts_fragments.databinding.ListItemContactBinding
import com.example.contacts_fragments.utils.ContactDiffCallback

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
                Glide.with(root.context)
                    .load(contact.imageUrl)
                    .placeholder(R.drawable.placehoder_person)
                    .circleCrop()
                    .into(imageContact)
                root.setOnClickListener { listener.onClick(contact)
                }
                root.setOnLongClickListener {
                    listener.onLongClick(contact)
                    true
                }
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
        fun onLongClick(contact: Contact): Boolean
    }

    fun updateData(newContacts: List<Contact>) {
        contactsList.clear()
        contactsList.addAll(newContacts)
    }

    fun getFilteredList(newContacts: List<Contact>) {
        val diffCallback = ContactDiffCallback(contactsList, newContacts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        contactsList.clear()
        contactsList.addAll(newContacts)
        diffResult.dispatchUpdatesTo(this)
    }
}
