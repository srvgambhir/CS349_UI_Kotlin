package ui.assignments.a4notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.switchmaterial.SwitchMaterial
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

/**
 * Edit screen -> called in Adapter
 */
class EditFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Cll NotesViewModel
         */
        val model : NotesViewModel by activityViewModels { NotesViewModel.Factory }

        /**
         * Initialize note and view variables
         * the VMNote with id = editNoteId
         * the view abjects (i.e. the switches and text)
         */
        val editNote = model.getNotes().value!!.filter{it.value!!.id == model.editNoteId}[0].value!!
        val importantSwitch = view.findViewById<SwitchMaterial>(R.id.edit_important_switch)
        val archivedSwitch = view.findViewById<SwitchMaterial>(R.id.edit_archived_switch)
        val title = view.findViewById<EditText>(R.id.edit_note_title)
        val content = view.findViewById<EditText>(R.id.edit_note_content)


        /**
         * Note: In each of the following actions, we are also initializing the view objects with the values from the VMNote
         * e.g., setting Important switch to clicked if VMote is important
         */

        /**
         * Action on Important switch
         * Call function in NotesViewModel -> make sure to uncheck Archived switch
         */
        importantSwitch.apply {
            isChecked = editNote.important
            setOnCheckedChangeListener { _, isChecked ->
                model.updateNoteImportant(editNote.id, isChecked)
                if (isChecked) {
                    archivedSwitch.isChecked = false
                }
            }
        }

        /**
         * Action on Archived switch
         * Call function in NotesViewModel -> make sure to uncheck Important switch
         */
        archivedSwitch.apply {
            isChecked = editNote.archived
            setOnCheckedChangeListener { _, isChecked ->
                model.updateNoteArchived(editNote.id, isChecked)
                if (isChecked) {
                    importantSwitch.isChecked = false
                }
            }
        }

        /**
         * Action on title change
         */
        title.apply {
            setText(editNote.title)
            doOnTextChanged { text, _, _, _ ->
                 model.updateNoteTitle(editNote.id, text.toString())
            }
        }

        /**
         * Action on content change
         */
        content.apply {
            setText(editNote.content)
            doOnTextChanged { text, _, _, _ ->
                model.updateNoteContent(editNote.id, text.toString())
            }
        }

    }
}