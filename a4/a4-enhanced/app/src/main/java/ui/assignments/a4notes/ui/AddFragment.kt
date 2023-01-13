package ui.assignments.a4notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.material.switchmaterial.SwitchMaterial
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

/**
 * Scene for setting note info and adding note
 */
class AddFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Only action in fragment is on the create button -> call add function in NotesViewModel with info entered
         */
        view.findViewById<Button>(R.id.create_button).setOnClickListener {
            /**
             * Initialize view objects
             */
            val importantSwitch = view.findViewById<SwitchMaterial>(R.id.add_important_switch)
            val title = view.findViewById<EditText>(R.id.add_note_title)
            val content = view.findViewById<EditText>(R.id.add_note_content)

            /**
             * Access NotesViewModel
             */
            val model : NotesViewModel by activityViewModels { NotesViewModel.Factory }

            /**
             * Call add function in model
             */
            model.addNote(title.text.toString(), content.text.toString(), importantSwitch.isChecked)

            /**
             * Navigate back to home screen
             */
            Navigation.findNavController(it).navigate(R.id.action_addFragment_to_homeFragment)
        }
    }

}