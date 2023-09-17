import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts_fragments.Contact
import com.example.contacts_fragments.ContactAdapter
import com.example.contacts_fragments.DetailsFragment
import com.example.contacts_fragments.R
import com.example.contacts_fragments.SharedViewModel
import com.example.contacts_fragments.databinding.FragmentContactsBinding
import com.example.contacts_fragments.utils.ContactDiffCallback
import java.util.Locale

class ContactsFragment : Fragment(), ContactAdapter.Listener {

    companion object {
        const val STATE_SHOW_DETAILS = 1
        const val STATE_SHOW_PLACEHOLDER = 0
        const val SEARCH_DELAY_MS = 2000L
    }

    private lateinit var binding: FragmentContactsBinding
    private lateinit var adapter: ContactAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var searchRunnable: Runnable
    private val searchHandler = Handler()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        initializeSearchFunctionality()
    }

    private fun initializeSearchFunctionality() {
        binding.editTextSearch.addTextChangedListener {
            setupSearchRunnable()
            searchHandler.removeCallbacks(searchRunnable)
            searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS)
        }
    }

    private fun setupSearchRunnable() {
        searchRunnable = Runnable {
            val searchText = binding.editTextSearch.text.toString().lowercase(Locale.getDefault())
            val foundContacts = sharedViewModel.getContactList().filter {
                it.name.lowercase(Locale.getDefault()).contains(searchText) ||
                        it.surname.lowercase(Locale.getDefault()).contains(searchText)
            }
            adapter.getFilteredList(foundContacts)
        }
    }

    private fun setupRecyclerView() {
        val contacts = sharedViewModel.getContactList().toMutableList()
        adapter = ContactAdapter(contacts, this)
        val itemDecoration = ItemDecoration(requireContext())


        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.addItemDecoration(itemDecoration)
            adapter = this@ContactsFragment.adapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (::searchRunnable.isInitialized) {
            searchHandler.removeCallbacks(searchRunnable)
        }
    }

    override fun onClick(contact: Contact) {
        if (isSmartphoneLayout()) {
            showDetailsForSmartphone(contact)
        } else {
            showDetailsForTablet(contact)
        }
    }

    override fun onLongClick(contact: Contact): Boolean {
        showConfirmationDialog(contact)
        return true
    }

    private fun showConfirmationDialog(contact: Contact) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.contact_delete_title)
            .setMessage(R.string.contact_delete_message)
            .setPositiveButton(R.string.contact_delete_yes) { _, _ ->
                removeContactAndRefresh(contact)
            }
            .setNegativeButton(R.string.contact_delete_no, null)
            .show()

        customizeDialogButtons(alertDialog)
    }

    private fun removeContactAndRefresh(contact: Contact) {
        val contactOldList = sharedViewModel.getContactList()
        val contactMutableList = contactOldList.toMutableList()
        contactMutableList.remove(contact)
        val contactNewList = contactMutableList.toList()

        refreshContactList(contactOldList, contactNewList)
        sharedViewModel.updateContacts(contactNewList)

        Toast.makeText(requireContext(), R.string.contact_deleted, Toast.LENGTH_SHORT).show()
    }

    fun refreshContactList(oldList: List<Contact>, newList: List<Contact>) {
        val diffResult = DiffUtil.calculateDiff(ContactDiffCallback(oldList, newList))
        adapter.updateData(newList)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun customizeDialogButtons(alertDialog: AlertDialog) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(requireContext(), R.color.pink_main))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(requireContext(), R.color.green))
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
                    state = STATE_SHOW_DETAILS,
                    imageUrl = contact.imageUrl
                ),
                DetailsFragment.DETAILS_FRAGMENT_TAG
            )
            addToBackStack(DetailsFragment.DETAILS_FRAGMENT_TAG)
        }
    }

    private fun showDetailsForTablet(contact: Contact) {
        val detailsFragment = DetailsFragment.newInstance(
            id = contact.id,
            name = contact.name,
            surname = contact.surname,
            number = contact.number,
            state = STATE_SHOW_DETAILS,
            imageUrl = contact.imageUrl
        )
        parentFragmentManager.commit {
            replace(
                R.id.rightFragment,
                detailsFragment,
                DetailsFragment.DETAILS_FRAGMENT_TAG
            )
        }
    }

}
