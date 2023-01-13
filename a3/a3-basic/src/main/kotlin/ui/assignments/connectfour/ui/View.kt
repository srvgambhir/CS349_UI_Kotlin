package ui.assignments.connectfour.ui

import javafx.beans.Observable
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.text.Font
import ui.assignments.connectfour.model.Model
import ui.assignments.connectfour.model.Player

/**
 * Setting up grid and scene size
 */
const val squareSize = 100.0
const val topSideSpace = squareSize * 2
const val bottomSpace = squareSize / 2
const val sceneWidth = squareSize * (Model.width + 4)
const val sceneHeight = squareSize * (Model.height + 2) + 50
const val pieceRadius = squareSize / 2 - 5


/**
View for Connect 4
 */
class View: Pane() {

    /**
     * Converting sizes to Integers for use in iteration loops
     */
    private val squareSizeInt = squareSize.toInt()
    private val topSideSpaceInt = topSideSpace.toInt()
    private val bottomSpaceInt = bottomSpace.toInt()

    init {
        /**
         * Creating grid and drawing tile shapes
         */
        for (i in topSideSpaceInt until (Model.width)*squareSizeInt + topSideSpaceInt step squareSizeInt) {
            for (j in topSideSpaceInt until Model.height*squareSizeInt + topSideSpaceInt - bottomSpaceInt step squareSizeInt) {
                val r = Rectangle().apply{
                    width = squareSize
                    height = squareSize
                }
                val c = Circle().apply{
                    centerX = squareSize / 2
                    centerY = squareSize / 2
                    radius = squareSize / 2 - 15
                }

                /**
                 * Subtract circle from square to create tile
                 */
                val tile = Shape.subtract(r, c).apply {
                    layoutY = j.toDouble()
                    layoutX = i.toDouble()
                    fill = Color.DEEPSKYBLUE
                    stroke = Color.BLACK
                    strokeWidth = 3.0
                }
                children.add(tile)
            }
        }

        /**
         * Adding "Start" button
         */
        children.add(Button("Press to Start Game!").apply{
            background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii(10.0), Insets(0.0)))
            prefWidth = 300.0
            prefHeight = 100.0
            layoutX = sceneWidth / 2 - 150
            layoutY = 50.0
            font = Font.font(25.0)
            /**
             * Action for starting game and removing button once pressed
             */
            onAction = EventHandler {
                this@View.children.remove(this)
                Model.startGame()
            }
        })

        /**
         * Adding player labels
         */
        children.add(PlayerLabel(Player.ONE).apply {
            layoutX = 35.0
            layoutY = 35.0
        })
        children.add(PlayerLabel(Player.TWO).apply {
            layoutX = sceneWidth - 165.0
            layoutY = 35.0
        })

        /**
         * Listeners for Player-switch, draw, and win sequences
         */
        val playerListener = { _: Observable, _: Player, new: Player ->
            switchPlayer(new)
        }
        val drawListener = { _: Observable, _: Boolean, _: Boolean ->
            draw()
        }
        val winListener = { _: Observable, _: Player, new: Player ->
            win(new)
        }
        Model.onNextPlayer.addListener(playerListener)
        Model.onGameDraw.addListener(drawListener)
        Model.onGameWin.addListener(winListener)
    }

    /**
     * Function for presenting "draw" message
     */
    private fun draw() {
        children.add(Label("DRAW").apply {
            font = Font.font(60.0)
            alignment = Pos.CENTER
            layoutX = sceneWidth / 2 - 95.0
            layoutY = 60.0
        })
    }

    /**
     * Function for presenting "win" message
     */
    private fun win(p: Player) {
        children.add(Label("Player $p Won!!!").apply {
            font = Font.font(60.0)
            alignment = Pos.CENTER
            layoutX = sceneWidth / 2 - 237.0
            layoutY = 60.0
        })
    }

    /**
     * Function for switching player
     */
    private fun switchPlayer(new: Player) {
        children.add(Piece(new))
    }
}