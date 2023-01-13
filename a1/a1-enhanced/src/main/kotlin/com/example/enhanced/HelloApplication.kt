package com.example.enhanced

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.Stage


// Global variables (required in class methods) ************************************************************************

val ShowArchived_checkBox = CheckBox().apply {
    padding = Insets(3.0, 0.0, 0.0, 0.0)
}
// Status bar
val status = HBox()

var insert_index = 4

// Global variables (required in class methods) ************************************************************************


// Class storing the content in each Note Box
class NoteText (val text: String) {

    // Store the text in a note
    val text_label = Label(text).apply {
        textAlignment = TextAlignment.LEFT
        isWrapText = true
    }
    val text_box = HBox(text_label)

    // Store the archived property in each note (label and checkbox)
    val archived_label = Label("Archived").apply {
        minWidth= 60.0
    }
    val archived_checkBox = CheckBox()
    val archived_box = HBox(archived_checkBox, archived_label).apply {
        spacing = 10.0
    }
}


// Class storing 2 boxes for each note, one for list view, and the other for grid view
class Note (val text: String, val order_index: Int) {

    // General Variables
    val length = text.length
    var isarchived = false

    // List View boxes *************************************************************************************************
    val listText = NoteText(text)

    // Note Box for List view
    val listBox = HBox(listText.text_box, listText.archived_box).apply {

        // formatting
        HBox.setHgrow(listText.text_box, Priority.ALWAYS)
        HBox.setHgrow(listText.archived_box, Priority.NEVER)
        this.padding = Insets(10.0)
        this.background = Background(BackgroundFill(Color.LIGHTYELLOW,CornerRadii(10.0), null))
        spacing = 10.0

        // listener for archived checkbox -> color change, interaction with showArchived checkbox, and status update
        listText.archived_checkBox.selectedProperty().addListener {
                observable, oldValue, newValue ->
            archive(newValue)
        }
    }
    // List View boxes *************************************************************************************************


    // Grid View boxes *************************************************************************************************
    val gridText = NoteText(text)

    // Note Box for Grid view
    val gridBox = VBox(gridText.text_box, gridText.archived_box).apply {

        //formatting
        VBox.setVgrow(gridText.text_box, Priority.ALWAYS)
        VBox.setVgrow(gridText.archived_box, Priority.NEVER)
        this.padding = Insets(10.0)
        this.background = Background(BackgroundFill(Color.LIGHTYELLOW,CornerRadii(10.0), null))
        prefWidth = 225.0
        prefHeight = 225.0
        spacing = 10.0

        // listener for archived checkbox -> color change, interaction with showArchived checkbox, and status update
        gridText.archived_checkBox.selectedProperty().addListener {
                observable, oldValue, newValue ->
            archive(newValue)
        }
    }
    // Grid View boxes *************************************************************************************************


    // Function to archive notes
    fun archive(newValue: Boolean) {
        if (newValue) {
            // set variables
            listText.archived_checkBox.isSelected = true
            gridText.archived_checkBox.isSelected = true
            isarchived = true

            // change background color and interact with Show_archived box
            listBox.background = Background(BackgroundFill(Color.LIGHTGREY,CornerRadii(10.0), null))
            gridBox.background = Background(BackgroundFill(Color.LIGHTGREY,CornerRadii(10.0), null))
            if (!ShowArchived_checkBox.isSelected) {
                listBox.isManaged = false
                listBox.isVisible = false
                gridBox.isManaged = false
                gridBox.isVisible = false
            }
        }
        else {
            // set variables
            listText.archived_checkBox.isSelected = false
            gridText.archived_checkBox.isSelected = false
            isarchived = false

            // change background color
            listBox.background = Background(BackgroundFill(Color.LIGHTYELLOW,CornerRadii(10.0), null))
            gridBox.background = Background(BackgroundFill(Color.LIGHTYELLOW,CornerRadii(10.0), null))
        }

        // update status bar
        status.fireEvent(MouseEvent(MouseEvent.MOUSE_MOVED, 0.0,
            0.0, 0.0, 0.0, MouseButton.PRIMARY, 1, true, true,
            true, true, true, true, true,
            true, true, true, null))
    }
}

class HelloApplication : Application() {

    override fun start(stage: Stage) {

        // Scene layout
        val Notes_App = BorderPane()


        // ToolBar Formatting ******************************************************************************************

        // Group 1: View options
        val View_label = Label("View:").apply {
            padding = Insets(3.0, 0.0, 0.0, 0.0)
        }
        val List_button = Button("List").apply {
            prefWidth = 50.0
            // Initially the app starts in LIst view
            isDisable = true
        }
        val Grid_button = Button("Grid").apply {
            prefWidth = 50.0
        }
        val View_group = HBox(View_label, List_button, Grid_button).apply {
            spacing = 10.0
            padding = Insets(10.0, 10.0, 10.0, 5.0)
        }


        // Group 2: Show Archived
        val ShowArchived_label = Label("Show archived:").apply {
            padding = Insets(3.0, 0.0, 0.0, 0.0)
        }
        val ShowArchived_group = HBox(ShowArchived_label, ShowArchived_checkBox).apply {
            spacing = 10.0
            padding = Insets(10.0, 10.0, 10.0, 5.0)
        }


        // Group 3: Order by
        val OrderBy_label = Label("Order by:").apply {
            padding = Insets(3.0, 0.0, 0.0, 0.0)
        }
        val OrderBy_choiceBox = ChoiceBox(FXCollections.observableArrayList("Length (asc)",
            "Length (desc)",
            "Most recent",
            "Least recent"))
        val OrderBy_group = HBox(OrderBy_label, OrderBy_choiceBox).apply {
            spacing = 10.0
            padding = Insets(10.0, 10.0, 10.0, 5.0)
        }


        // Clear button
        val Clear_button = Button("Clear").apply {
            minWidth = 50.0
        }
        val Clear_group = HBox(Clear_button).apply {
            padding = Insets(10.0, 5.0, 10.0, 10.0)
        }


        // Toolbar separators
        val separator1 = Separator(Orientation.VERTICAL)
        val separator2 = Separator(Orientation.VERTICAL)


        // Putting the toolbar together - along with a temp Pane to help with spacing
        val tmpPane = Pane()
        HBox.setHgrow(tmpPane, Priority.ALWAYS)

        Notes_App.top = ToolBar(
            View_group, separator1, ShowArchived_group, separator2, OrderBy_group, tmpPane, Clear_group
        )

        // Toolbar Formatting ******************************************************************************************


        // Center Area Formatting **************************************************************************************

        // Initial notes
        val note1 = Note("This is a note.", 0)

        // Set note2 to archived
        val note2 = Note("This is another note.", 1)
        note2.listText.archived_checkBox.isSelected = true

        val note3 = Note("Oh wow, this is yet another note.", 2)

        val note4 = Note("Would you believe it! This is a new note.", 3)

        val Notes = mutableListOf(note1, note2, note3, note4)


        // LIST VIEW
        // Text Area and Create Button
        val List_input = TextArea().apply {
            isWrapText = true
        }
        val List_create = Button("Create").apply {
            minWidth = 75.0
            prefHeight = 42.0
        }
        // Single HBox to store Text Area and Create Button
        val List_NoteCreate = HBox(List_input, List_create).apply {
            HBox.setHgrow(List_input, Priority.ALWAYS)
            HBox.setHgrow(List_create, Priority.NEVER)
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.LIGHTSALMON, CornerRadii(10.0), null))
            prefHeight = 62.0
            spacing = 10.0
        }
        // Initializing the list of notes
        val List_Notes = VBox().apply {
            children.add(List_NoteCreate)
            Notes.forEach() {
                children.add(it.listBox)
            }
            VBox.setVgrow(this, Priority.ALWAYS)
            this.alignment = Pos.TOP_CENTER
            this.spacing = 10.0
            this.padding = Insets(10.0)
        }
        // Putting list of notes into scroll pane
        val List_scroll = ScrollPane(List_Notes).apply {
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            this.isFitToWidth = true
        }


        // GRID VIEW
        // Text Area and Create Button
        val Grid_input = TextArea().apply {
            isWrapText = true
        }
        val Grid_create = Button("Create").apply {
            prefWidth = 205.0
        }
        // Single HBox to store Text Area and Create Button
        val Grid_NoteCreate = VBox(Grid_input, Grid_create).apply {
            VBox.setVgrow(Grid_input, Priority.ALWAYS)
            VBox.setVgrow(Grid_create, Priority.NEVER)
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.LIGHTSALMON,CornerRadii(10.0), null))
            spacing = 10.0
            prefWidth = 225.0
            prefHeight = 225.0
        }
        // Initializing the list of notes
        val Grid_Notes = TilePane(Orientation.HORIZONTAL).apply {
            children.add(Grid_NoteCreate)
            Notes.forEach() {
                children.add(it.gridBox).apply {
                }
            }
            alignment = Pos.TOP_LEFT
            hgap = 10.0
            vgap = 10.0
            padding = Insets(10.0)
        }
        // Putting list of notes into scroll pane
        val Grid_scroll = ScrollPane(Grid_Notes).apply {
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            this.isFitToWidth = true
        }

        // Start in List View
        Notes_App.center = List_scroll

        // Center Area Formatting **************************************************************************************


        // Status Bar **************************************************************************************************

        val stats = Label("${Notes.count()} notes, ${Notes.filter{it.isarchived == false}.count()} of which are active")
        status.children.add(stats)
        status.apply {
            status.padding = Insets(2.5)
            setOnMouseMoved {
                children.clear()
                children.add(Label("${Notes.count()} notes, ${Notes.filter{it.isarchived == false}.count()} of which are active"))
                Notes_App.bottom = this
            }
        }

        Notes_App.bottom = status

        // Status Bar **************************************************************************************************


        // Logic -> Action Events, Triggers, Functionality *************************************************************

        // Switching between views
        List_button.apply {
            setOnAction {
                isDisable = true
                Grid_button.isDisable = false
                Notes_App.center = List_scroll
            }
        }
        Grid_button.apply {
            setOnAction {
                isDisable = true
                List_button.isDisable = false
                Notes_App.center = Grid_scroll
            }
        }

        // Logic on create buttons to add a new note
        val enter: (TextArea) -> Unit =
            { input: TextArea ->
                // Create note and add to appropriate lists
                val new_note = Note(input.text, insert_index)
                Notes.add(new_note)
                List_Notes.children.add(new_note.listBox)
                Grid_Notes.children.add(new_note.gridBox)

                // Clear text area and increment global index
                input.clear()
                ++insert_index

                // fire event triggers
                OrderBy_choiceBox.fireEvent(ActionEvent())
                status.fireEvent(MouseEvent(MouseEvent.MOUSE_MOVED, 0.0,
                    0.0, 0.0, 0.0, MouseButton.PRIMARY, 1, true, true,
                    true, true, true, true, true,
                    true, true, true, null
                )
                )
            }
        List_create.apply {
            setOnAction {
                enter(List_input)
            }
        }
        Grid_create.apply {
            setOnAction {
                enter(Grid_input)
            }
        }

        // Logic for Show Archived checkbox
        ShowArchived_checkBox.selectedProperty().addListener {
                observable, oldValue, newValue ->
            for (note in Notes) {
                if (note.isarchived) {
                    if (!newValue) {
                        note.listBox.isManaged = false
                        note.listBox.isVisible = false
                        note.gridBox.isManaged = false
                        note.gridBox.isVisible = false
                    }
                    else {
                        note.listBox.isManaged = true
                        note.listBox.isVisible = true
                        note.gridBox.isManaged = true
                        note.gridBox.isVisible = true
                    }
                }
            }
        }

        // Logic for sorting and filtering -> through OrderBy menu
        OrderBy_choiceBox.apply {
            setOnAction {
                if (value == "Length (asc)") {
                    Notes.sortWith(compareBy{it.length})
                }
                else if (value == "Length (desc)") {
                    Notes.sortWith(compareByDescending{it.length})
                }
                else if (value == "Most recent") {
                    Notes.sortWith(compareByDescending{it.order_index})
                }
                else if (value == "Least recent") {
                    Notes.sortWith(compareBy{it.order_index})
                }

                // Clear LIst and Grid views
                List_Notes.children.clear()
                Grid_Notes.children.clear()

                // Re-enter input boxes
                List_Notes.children.add(List_NoteCreate)
                Grid_Notes.children.add(Grid_NoteCreate)

                // Add sorted notes
                for (note in Notes) {
                    List_Notes.children.add(note.listBox)
                    Grid_Notes.children.add(note.gridBox)
                }
            }
        }

        // LOgic for clearing notes (Clear button)
        Clear_button.apply {
            setOnAction {
                // Clear lists
                Notes.clear()
                List_Notes.children.clear()
                Grid_Notes.children.clear()
                List_Notes.children.add(List_NoteCreate)
                Grid_Notes.children.add(Grid_NoteCreate)

                // Reset global index
                insert_index = 0

                // Fire event triggers
                status.fireEvent(MouseEvent(MouseEvent.MOUSE_MOVED, 0.0,
                    0.0, 0.0, 0.0, MouseButton.PRIMARY, 1, true, true,
                    true, true, true, true, true,
                    true, true, true, null))
            }
        }

        // Logic -> Action Events, Triggers, Functionality *************************************************************


        // Scene Formatting ********************************************************************************************

        val scene = Scene(Notes_App)

        stage.title = "CS349 - A1 Notes - s3gambhi"
        stage.isResizable = true

        stage.width = 800.0
        stage.minWidth = 640.0

        stage.height = 600.0
        stage.minHeight = 480.0

        stage.scene = scene
        stage.show()

        // Scene Formatting ********************************************************************************************

    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}