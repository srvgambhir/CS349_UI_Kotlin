package ui.assignments.a4notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

/**
 * Home Fragment -> primary screen showing list of notes
 */
class HomeFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Initialize recycler view, i.e. the list of notes
         */
        val rv = view.findViewById<RecyclerView>(R.id.rv_list).apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.context)
        }

        /**
         * Access NotesViewModel
         */
        val model : NotesViewModel by activityViewModels { NotesViewModel.Factory }

        /**
         * Add observer on list of VMNotes -> listen for changes and update recycler view list
         */
        model.getNotes().observe(viewLifecycleOwner) {
            rv.adapter = NotesAdapter(it, model)
        }

        /**
         * Set action on "add" button -> navigate to Add Fragment
         */
        view.findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_addFragment)
        }

        /**
         * Action on Show Archived switch -> call function in NotesViewModel
         * Also, initialize switch with value in NotesViewModel
         */
        view.findViewById<SwitchMaterial>(R.id.show_archived_switch).apply {
            isChecked = model.getViewArchived().value!!
            setOnCheckedChangeListener { _, isChecked ->
                model.setViewArchived(isChecked)
            }
        }

    }

}