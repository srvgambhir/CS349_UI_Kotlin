package com.example.basic

import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Affine
import javafx.scene.transform.Transform
import javafx.stage.Stage
import java.text.DecimalFormat
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


// Helper class for Model -> stores information for a single dataset
class Dataset {
    val textfields = mutableListOf<Point>()
    val deletebuttons = mutableListOf<DeletePointWidget>()
    val data = mutableListOf<HBox>()
    var hasNegative = false

    // method to check if dataset has negative value
    fun updateNegative(): Boolean {
        hasNegative = false
        textfields.forEach {
            if (it.valid) {
                if (it.text.toDouble() < 0) {
                    hasNegative = true
                }
            }
        }
        return hasNegative
    }
}

// Model Object --------------------------------------------------------------------------------------------------------
object Model: Observable {
    // Setting up listener funcions
    private val listeners =
        mutableListOf<InvalidationListener?>()

    override fun addListener(listener:InvalidationListener?) {
        listeners.add(listener)
    }
    override fun removeListener(listener: InvalidationListener?) {
        listeners.remove(listener)
    }

    // Variables to be used in Model to alter back-end state
    val datasets = mutableMapOf<String, Dataset>()

    // added variable to update choicebox
    var added = ReadOnlyBooleanWrapper(false)
    val Added: ReadOnlyBooleanProperty = added.readOnlyProperty

    var currentDataset: String? = null
    var currentHasNegative = false

    // used to update graph view (canvas)
    var currentGraph = 1

    var stageHeight = 0.0
    var stageWidth = 0.0


    // initialize application with quadratic dataset
    init {
        createDataset("quadratic", "0.1")
        listOf(1.0, 4.0, 9.0, 16.0).forEach {
            addPoint("$it")
        }
    }

    // method to create a new dataset
    fun createDataset(name: String, init: String) {
        datasets[name] = Dataset()
        currentDataset = name
        currentHasNegative = false
        currentGraph = 1
        added.value = !added.value
        addPoint(init)
    }

    // method to change current dataset
    fun changeDataset(name: String) {
        currentDataset = name
        currentHasNegative = datasets[currentDataset]!!.hasNegative
        if (currentHasNegative && (currentGraph == 3 || currentGraph == 4)) {
            currentGraph = 1
        }
        broadcast()
    }

    // method to add a new point to current dataset
    fun addPoint(num: String) {
        if (num.toDouble() < 0.0) {
            datasets[currentDataset]?.hasNegative = true
        }
        val p = Point(num)
        val d = DeletePointWidget()
        val b = HBox(p, d)
        datasets[currentDataset]?.textfields?.add(p)
        datasets[currentDataset]?.deletebuttons?.add(d)
        datasets[currentDataset]?.data?.add(b)
        broadcast()
    }

    // method to delete point from current dataset (based on "X" button)
    fun deletePoint() {
        val iter = datasets[currentDataset]?.deletebuttons?.iterator()
        var ind: Int = -1
        for ((index, value) in iter!!.withIndex()) {
            if (value.isDisable) {
                iter.remove()
                ind = index
            }
        }
        datasets[currentDataset]?.data?.removeAt(ind)
        datasets[currentDataset]?.textfields?.removeAt(ind)
        currentHasNegative = datasets[currentDataset]?.updateNegative() ?: false
        broadcast()
    }

    // function to update status of app based on point edit
    fun editPoint(num: Double, valid: Boolean) {
        val tempBool = currentHasNegative
        currentHasNegative = datasets[currentDataset]?.updateNegative() ?: false
        if (valid && !tempBool) {
            if (num < 0) {
                if (currentGraph != 1/*(currentGraph == 3 || currentGraph == 4)*/) {
                    currentGraph = 1
                }
            }
        }
        broadcast()
    }

    // Change graph view
    fun changeView(view: Int) {
        currentGraph = view
        broadcast()
    }

    // Methods for keeping track of stage size changes -> reflecting change on canvas size
    fun changeHeight(newh: Double) {
        stageHeight = newh
        broadcast()
    }
    fun changeWidth(newW: Double) {
        stageWidth = newW
        broadcast()
    }

    // function to broadcast to all invalidation listeners
    fun broadcast() {
        listeners.forEach { it?.invalidated(this)}
    }
}
// _____________________________________________________________________________________________________________________


// Views and Controllers for DataPoint (Point Text Field, Delete Button, Add Button ------------------------------------

// Delete Button "X"-> View and Controller
class DeletePointWidget: Button("X"), InvalidationListener {
    init {
        onAction = EventHandler{
            isDisable = true
            Model.deletePoint()
        }
        Model.addListener(this)
        invalidated(null)
    }

    override fun invalidated(observable: Observable?) {
        isDisable = Model.datasets[Model.currentDataset]?.data?.size == 1
    }
}

// Add Button -> Controller
class AddPointWidget: Button("Add Entry") {
    init {
        setOnAction {
            Model.addPoint("0.0")
        }
        maxWidth = Double.MAX_VALUE
    }
}

// Point Text Field -> Controller
class Point(tex: String): TextField() {
    var value: Double = tex.toDouble()
    var valid = true
    init {
        text = tex
        textProperty().addListener {
            _, _, newValue ->
            if (newValue != "") {
                text.toDoubleOrNull()?.let {
                    value = text.toDouble()
                    valid = true
                } ?: run {
                    valid = false
                }
            }
            else {
                valid = false
            }
            Model.editPoint(value, valid)
        }
    }
}

// ---------------------------------------------------------------------------------------------------------------------


// View for Data Section (Left side) of the of application -------------------------------------------------------------
class DataSection: VBox(), InvalidationListener {
    init {
        Model.addListener(this)
        invalidated(null)
        padding = Insets(10.0)
        spacing = 10.0
    }

    // Create tob label and add all noteboxes
    override fun invalidated(observable: Observable?) {
        children.clear()
        children.add(Label("Dataset name: ${Model.currentDataset}").apply {
            background = Background(BackgroundFill(Color.LIGHTGRAY, CornerRadii(5.0), Insets(0.0)))
            textAlignment = TextAlignment.LEFT
            isWrapText = true
            prefWidth = 265.0
            padding = Insets(10.0)
        })
        var counter = 0
        Model.datasets[Model.currentDataset]?.data?.forEach {
            val label = Label("Entry #${counter}").apply {
                padding = Insets(4.0, 0.0, 0.0, 0.0)
            }
            children.add( HBox(label, it).apply { spacing = 10.0 } )
            ++counter
        }
        children.add(AddPointWidget())
    }
}
//----------------------------------------------------------------------------------------------------------------------


// View for Visual Section/Canvas (Right Side) for Application ---------------------------------------------------------
class VisualSection(initialHeight: Double, initialWidth: Double): Canvas(), InvalidationListener {

    init {
        height = initialHeight
        width = initialWidth
        Model.addListener(this)
        invalidated(null)

        // initialize the opacity
        graphicsContext2D.apply {
            globalAlpha = 0.5
        }
    }

    // helper method for calculating SEM value -> for Bar_SEM
    fun calculateSEM(list: List<Double>, mean: Double): Double {
        var sd = 0.0
        list.forEach {
            sd += (it-mean).pow(2)
        }
        return sqrt(sd/list.size.toDouble()) / sqrt(list.size.toDouble())
    }

    override fun invalidated(observable: Observable?) {

        // Update current canvas height and width
        height = Model.stageHeight
        width = Model.stageWidth

        // Determine valid points array and minimum and maximum values
        val points = mutableListOf<Double>()
        var min: Double = Double.POSITIVE_INFINITY
        var max: Double = Double.NEGATIVE_INFINITY
        Model.datasets[Model.currentDataset]?.textfields?.forEach {
            if (it.valid) {
                points.add(it.value)
                if (it.value < min) {
                    min = it.value
                }
                if (it.value > max) {
                    max = it.value
                }
            }
        }

        // Initialize color List -> used for alternating between colors for bar, bar_sem, and pie chart
        val colorList = listOf (
            Color.PALEVIOLETRED, Color.SKYBLUE,  Color.LAWNGREEN, Color.ORANGE, Color.MEDIUMPURPLE,
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGERED, Color.PURPLE,
            Color.CRIMSON, Color.ROYALBLUE, Color.FORESTGREEN, Color.DARKORANGE, Color.VIOLET,
            Color.DARKRED, Color.DARKBLUE, Color.DARKGREEN, Color.INDIANRED, Color.DARKVIOLET
        )

        // Draw List Graph
        if (Model.currentGraph == 1) {

            // height scalar for scaling all values vertically
            var heightScalar = 1.0
            if (max != min) {
                heightScalar = (height) / (max - min)
            }

            // width increment for scaling all values horizontally
            var widthIncrement = width
            if (points.size != 1) {
                widthIncrement = (width) / (points.size-1)
            }

            graphicsContext2D.apply {
                // clear canvas
                clearRect(0.0, 0.0, width + 20, height + 20)

                // add padding to canvas
                val transform =
                    Affine(Transform.scale((width - 2 * 10.0) / width, (height - 2 * 10.0) / height)).apply {
                        appendTranslation(10.0, 10.0)
                    }
                setTransform(transform)

                if (points.size > 1) {
                    for (i in 0 until points.size - 1) {

                        // setting endpoint of line from this point to the next
                        val x1 = widthIncrement*i
                        val y1 = height - ((points[i] - min) * heightScalar)
                        val x2 = (i + 1) * (widthIncrement)
                        val y2 = height - ((points[i + 1] - min) * heightScalar)

                        // creating line
                        lineWidth = 2.0
                        stroke = Color.BLACK
                        strokeLine(x1, y1, x2, y2)

                        // adding marker for point
                        fill = Color.RED
                        fillOval(x1 - 5, y1 - 5, 10.0, 10.0)
                    }
                }

                fill = Color.RED
                // need to fill canvas so put single point in the middle of the canvas if size is 1
                if (points.size == 1) {
                    fillOval(widthIncrement/2  - 5, height - ((points[points.size - 1] - min) * heightScalar) - 5, 10.0, 10.0)
                }
                // put the remaining marker
                else if (points.size > 0) {
                    fillOval(widthIncrement * (points.size - 1)  - 5, height - ((points[points.size - 1] - min) * heightScalar) - 5, 10.0, 10.0)
                }
                // clear padding areas
                setTransform(Affine())
            }
        }

        // Draw Bar Graph
        else if (Model.currentGraph == 2) {

            // determining height multiplier -> based on nature of max and min
            val heightScalar: Double
            if (max == min && max == 0.0) {
                heightScalar = 1.0
            } else if (max < 0 && min < 0) {
                heightScalar = height / (-1 * min)
            } else if (max >= 0 && min >= 0) {
                heightScalar = height / max
            } else {
                heightScalar = height / (max + (-1 * min))
            }

            // determine how to increment width
            val widthIncrement = (width) / (points.size)

            // Determine where baseline (x-axis) goes
            var baseline = height
            // move midline up if there is a negative value
            if (min < 0) {
                baseline = height + (min * heightScalar)
            }

            graphicsContext2D.apply {
                // clear canvas
                clearRect(0.0, 0.0, width + 20, height + 20.0)

                // add padding to canvas
                val transform =
                    Affine(Transform.scale((width - 2 * 10.0) / width, (height - 2 * 10.0) / height)).apply {
                        appendTranslation(10.0, 10.0)
                    }
                setTransform(transform)

                // cycle through points and create the bar
                var colorIndex = 0
                for (i in 0 until points.size) {
                    fill = colorList[colorIndex]
                    if (colorIndex == 19) {
                        colorIndex = 0
                    } else {
                        colorIndex += 1
                    }

                    // current place on the x-axis
                    val currentX = (i * widthIncrement) + (widthIncrement / 2) - 5

                    // draw rectangle differently for negative and positive values -> negative grows down, positive grows up
                    if (points[i] < 0.0) {
                        fillRect(currentX, baseline, 10.0, -(points[i] * heightScalar))
                    }
                    else {
                        fillRect(currentX, baseline - (points[i] *  heightScalar), 10.0, points[i] * heightScalar)
                    }
                }

                // draw x-axis
                lineWidth = 3.0
                stroke = Color.BLACK
                strokeLine(0.0, baseline, width, baseline)

                // clear padded areas
                setTransform(Affine())
            }
        }


        else if (Model.currentGraph == 3) {

            // determining height multiplier -> based on nature of max and min
            val heightScalar: Double
            if (max == min && max == 0.0) {
                heightScalar = 1.0
            } else if (max < 0 && min < 0) {
                heightScalar = height / (-1 * min)
            } else if (max >= 0 && min >= 0) {
                heightScalar = height / max
            } else {
                heightScalar = height / (max + (-1 * min))
            }

            // determine how to increment width
            val widthIncrement = (width) / (points.size)

            // determine where baseline goes
            var baseline = height
            // if there is a negative value, move midline up
            if (min < 0) {
                baseline = height + (min * heightScalar)
            }

            graphicsContext2D.apply {
                // clear canvas
                clearRect(0.0, 0.0, width + 20, height + 20.0)

                // add padding to canvas
                val transform =
                    Affine(Transform.scale((width - 2 * 10.0) / width, (height - 2 * 10.0) / height)).apply {
                        appendTranslation(10.0, 10.0)
                    }
                setTransform(transform)

                // cycle through points and draw bars
                var colorIndex = 0
                for (i in 0 until points.size) {
                    fill = colorList[colorIndex]
                    if (colorIndex == 19) {
                        colorIndex = 0
                    } else {
                        colorIndex += 1
                    }

                    // determine place on axis to draw bar
                    val currentX = (i * widthIncrement) + (widthIncrement / 2) - 5

                    // draw rectangle differently based on positive or negative -> negative grows down, positive grows up
                    if (points[i] < 0.0) {
                        fillRect(currentX, baseline, 10.0, -(points[i] * heightScalar))
                    }
                    else {
                        fillRect(currentX, baseline - (points[i] *  heightScalar), 10.0, points[i] * heightScalar)
                    }
                }

                // Calculate mean and sem values
                var sum = 0.0
                points.forEach {
                    sum += it
                }
                val avg = sum / points.size
                val sem = calculateSEM(points, avg)

                // draw x-axis line and mean line
                lineWidth = 3.0
                stroke = Color.BLACK
                strokeLine(0.0, baseline, width, baseline)
                strokeLine(0.0, height - (avg) * heightScalar, width, height - (avg) * heightScalar)

                // set dashes and draw SEM range
                lineWidth = 2.0
                setLineDashes(8.0)
                strokeLine(0.0, height - (avg + sem) * heightScalar, width, height - (avg + sem) * heightScalar)
                setLineDashes(8.0)
                strokeLine(0.0, height - (avg - sem) * heightScalar, width, height - (avg - sem) * heightScalar)
                setLineDashes(0.0)

                // fill in text for stats -> create box around these for better clarity
                fill = Color.LIGHTYELLOW
                fillRect(0.0, 0.0, 105.0, 50.0)
                // increase opacity to write text
                globalAlpha = 1.0
                fill = Color.BLACK
                fillText("mean: ${DecimalFormat("#.##").format(avg)}\n" + "SEM: ${DecimalFormat("#.##").format(sem)}", 10.0, 20.0)
                // reset opacity after text
                globalAlpha = 0.5

                // clear padding areas
                setTransform(Affine())
            }
        }

        // Draw Pie Graph
        else if (Model.currentGraph == 4) {

            graphicsContext2D.apply {
                // clear canvas
                clearRect(0.0, 0.0, width + 20, height + 20.0)

                // determining radius of pie chart
                // determine x and y of the pie chart
                val radius  = (min(width, height) * 0.4)
                val x = (width/2) - radius
                val y = (height/2) - radius

                // calculate sum of values
                var sum = 0.0
                points.forEach {
                    sum += it
                }

                // cycle through points and draw corresponding arc
                var colorIndex = 0
                var curangle = 0.0
                points.forEach{
                    fill = colorList[colorIndex]
                    if (colorIndex == 19) {
                        colorIndex = 0
                    }
                    else {
                        ++colorIndex
                    }
                    // multiply proportion of value by 360 for angle extent
                    fillArc(x, y, 2*radius, 2*radius, curangle, (it/sum)*360, ArcType.ROUND)
                    // increment starting angle
                    curangle += (it/sum)*360
                }
            }
        }

    }

}
// ---------------------------------------------------------------------------------------------------------------------


// Views and Controllers for ToolBar buttons ---------------------------------------------------------------------------

// Choicebox for selecting dataset -> View and Controller, ChangeListener
class DatasetSelectGroup: HBox(), ChangeListener<Boolean> {
    val datasetChoice = ChoiceBox(FXCollections.observableArrayList("")).apply{
        prefWidth = 150.0
        setOnAction {
            Model.changeDataset(value)
        }
    }
    init {
        datasetChoice.items.clear()
        apply {
            children.add(datasetChoice)
            padding = Insets(10.0)
        }
        Model.added.addListener(this)
        changed(null, null, Model.Added.value)
    }

    // changed added variable -> add option to menu
    override fun changed(observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean?) {
        datasetChoice.items.add(Model.currentDataset)
        datasetChoice.value = Model.currentDataset
    }

}

// Group for creating dataset -> Controller
class DatasetCreatorGroup: HBox() {
    // textfield
    val dataSetName = TextField().apply{
        promptText = "Data set name"
        prefWidth = 150.0
    }
    // create button
    val create = Button("Create").apply {
        setOnAction {
            Model.createDataset(dataSetName.text, "0.0")
            dataSetName.clear()
        }
    }

    init {
        apply {
            children.add(dataSetName)
            children.add(create)
            padding = Insets(10.0)
        }
    }
}

// Toggle Group for selecting visualization -> View and Controller
class VisualSelectGroup: HBox(), InvalidationListener {
    val group = ToggleGroup()

    // Line graph
    val line = ToggleButton("Line").apply {
        setOnAction {
            Model.changeView(1)
        }
        toggleGroup = group
    }

    // bar graph
    val bar = ToggleButton("Bar").apply {
        setOnAction {
            Model.changeView(2)
        }
        toggleGroup = group
    }

    // stats bar graph
    val barSEM = ToggleButton("Bar (SEM)").apply {
        setOnAction {
            Model.changeView(3)
        }
        toggleGroup = group
    }

    // pie graph
    val pie = ToggleButton("Pie").apply {
        setOnAction {
            Model.changeView(4)
        }
        toggleGroup = group
    }

    // map buttons to ints -> able to switch easily
    val buttonMap = mapOf(1 to line, 2 to bar, 3 to barSEM, 4 to pie)

    init {
        apply {
            children.add(line)
            children.add(bar)
            children.add(barSEM)
            children.add(pie)
            padding = Insets(10.0)
        }
        Model.addListener(this)
        invalidated(null)
    }

    // for consistency -> if negative value disable certain options
    override fun invalidated(observable: Observable?) {
        if (Model.currentHasNegative) {
            barSEM.isDisable = true
            pie.isDisable = true
        }
        else {
            barSEM.isDisable = false
            pie.isDisable = false
        }
        buttonMap[Model.currentGraph]?.isSelected = true
    }
}
//----------------------------------------------------------------------------------------------------------------------


// Stage Controller -> send changes in height and width to Model to update canvas
class StageView(stage: Stage)  {

    // Toolbar setup
    val group1 = DatasetSelectGroup()
    val group2 = DatasetCreatorGroup()
    val group3 = VisualSelectGroup()
    val sep1 = Separator(Orientation.VERTICAL)
    val sep2 = Separator(Orientation.VERTICAL)
    val toolbar = ToolBar(
        group1, sep1, group2, sep2, group3
    )

    val dataentry = ScrollPane(DataSection()).apply {
        vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
    }

    // scene setup using borderpane
    val root = BorderPane().apply {
        // add listeners and width and height changes
        heightProperty().addListener {
            _, _, newValue ->
            Model.changeHeight(newValue.toDouble()-toolbar.height-20)
        }
        widthProperty().addListener {
                _, _, newValue ->
            Model.changeWidth(newValue.toDouble()-dataentry.width-20)
        }
    }

    init {
        stage.title = "CS349 - A2 Graphs - s3gambhi"
        stage.width = 800.0
        stage.minWidth = 600.0
        stage.height = 600.0
        stage.minHeight = 480.0
        stage.scene = Scene(root.apply{
            top = toolbar
            left = dataentry
        })
        stage.show()
        // after updating size values from stage.show, give remaining space to canvas
        root.center = VisualSection(root.height-toolbar.height, root.width - dataentry.width)
    }
}
//----------------------------------------------------------------------------------------------------------------------


class GraphApplication : Application() {
    override fun start(stage: Stage) {

        // create stage
        StageView(stage)

        // Creating a few initial datasets
        Model.createDataset("negative quadratic", "-0.1")
        listOf(-1.0, -4.0, -9.0, -16.0).forEach {
            Model.addPoint("$it")
        }
        Model.createDataset("alternating", "-1.0")
        listOf(3.0, -1.0, 3.0, -1.0, 3.0).forEach {
            Model.addPoint("$it")
        }
        Model.createDataset("inflation ‘90-‘22", "4.8")
        listOf(5.6, 1.5, 1.9, 0.2, 2.1, 1.6, 1.6, 1.0, 1.7, 2.7, 2.5,
            2.3, 2.8, 1.9, 2.2, 2.0, 2.1, 2.4, 0.3, 1.8, 2.9, 1.5, 0.9,
            1.9, 1.1, 1.4, 1.6, 2.3, 1.9, 0.7, 3.4, 6.8).forEach{
            Model.addPoint("$it")
        }
    }
}

fun main() {
    Application.launch(GraphApplication::class.java)
}