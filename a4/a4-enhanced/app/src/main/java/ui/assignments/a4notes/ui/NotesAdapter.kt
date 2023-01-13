package ui.assignments.a4notes.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

/**
 * Class for maintaining RecyclerView
 */
class NotesAdapter(val notes: MutableList<LiveData<NotesViewModel.VMNote>>, val model: NotesViewModel) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    /**
     * ViewHolder class -> maintains each note box in the view
     * Used internally by Adapter
     */
    class ViewHolder(private val noteView: View): RecyclerView.ViewHolder(noteView) {

        /**
         * Binds the note view to the note itself (i.e. the VMNote)
         */
        fun bind(note: LiveData<NotesViewModel.VMNote>, model: NotesViewModel) {
            /**
             * set title
             */
            noteView.findViewById<TextView>(R.id.Title).text = note.value!!.title
            /**
             * set content
             */
            noteView.findViewById<TextView>(R.id.Content).text = note.value!!.content

            /**
             * Set background colors of notes depending on status
             * important -> yellow
             * archived -> LTGRAY
             * default -> white
             */
            if(note.value!!.important) {
                noteView.findViewById<CardView>(R.id.note_box).setCardBackgroundColor(Color.YELLOW)
            }
            else if(note.value!!.archived) {
                noteView.findViewById<CardView>(R.id.note_box).setCardBackgroundColor(Color.LTGRAY)
            }

            /**
             * Add action on archive button in note box
             */
            noteView.findViewById<Button>(R.id.archive_button).setOnClickListener {
                /**
                 * send in opposite of current archived status -> be able to archive and un-archive
                 */
                model.updateNoteArchived(note.value!!.id, !note.value!!.archived)
            }

            /**
             * Add action on delete button in note box
             */
            noteView.findViewById<Button>(R.id.delete_button).setOnClickListener {
                model.removeNote(note.value!!.id)
            }

            /**
             * Action on note click -> redirect to Edit Fragment
             */
            noteView.findViewById<CardView>(R.id.note_box).setOnClickListener {
                /**
                 * Update editNoteId -> for the Edit Fragment to find which note to change
                 */
                model.editNoteId = note.value!!.id
                Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_editFragment)
            }

        }
    }

    /**
     * Following all functions used by the Adapter internally
     * Create View Holder (i.e. the previously defined ViewHolder class
     * Bind View Holder -> Bind to note
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val noteView = inflater.inflate(R.layout.note_box, parent, false)
        return ViewHolder(noteView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position], model)
    }

    override fun getItemCount(): Int {
        return notes.size
    }



}