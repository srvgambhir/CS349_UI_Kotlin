package ui.assignments.connectfour.ui

import javafx.animation.*
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.util.Duration
import ui.assignments.connectfour.model.Model
import ui.assignments.connectfour.model.Player

/**
 * View for each piece/token used in the game
 */
class Piece(private val player: Player): Circle(pieceRadius) {

    /**
     * Setting up piece events: drag, press, release
     */
    private val dragEvent = EventHandler<MouseEvent> { event -> dragged(event) }
    private val pressEvent = EventHandler<MouseEvent> { event -> pressed(event) }
    private val releaseEvent = EventHandler<MouseEvent> { event -> released(event) }

    /**
     * Data class for storing info and enabling drag mechanism
     */
    data class DragInfo(var anchorX: Double = 0.0, var initialX: Double = 0.0)
    private var dragInfo = DragInfo()

    init {
        /**
         * Creating pieces and placing them in appropriate positions
         */
        if (player == Player.ONE) {
            fill = Color.RED
            translateX = 100.0
            translateY = 135.0
        }
        else if (player == Player.TWO) {
            fill = Color.YELLOW
            translateX = sceneWidth - 100.0
            translateY = 135.0
        }

        /**
         * Adding events to EventFilter (enabling them)
         */
        addEventFilter(MouseEvent.MOUSE_DRAGGED, dragEvent)
        addEventFilter(MouseEvent.MOUSE_PRESSED, pressEvent)
        addEventFilter(MouseEvent.MOUSE_RELEASED, releaseEvent)

    }

    /**
     * Function for handling "press" event on piece
     */
    private fun pressed(event: MouseEvent) {
        dragInfo = DragInfo(event.sceneX, translateX)
    }

    /**
     * Function for handling "drag" event on piece
     */
    private fun dragged(event: MouseEvent) {
        var newX = dragInfo.initialX + event.sceneX - dragInfo.anchorX
        /**
         * setting up scene boundaries
         */
        if (newX >= pieceRadius && newX <= sceneWidth - pieceRadius) {
            /**
             * Handling piece "snap" on top of grid
             */
            if (newX >= topSideSpace && newX <= sceneWidth - topSideSpace) {
                /**
                 * Calculate which column to snap to
                 */
                var multiplier = (kotlin.math.floor((event.sceneX - topSideSpace) / squareSize))
                if (multiplier > Model.width - 1) {
                    multiplier = ((Model.width - 1).toDouble())
                }
                else if (multiplier < 0.0) {
                    multiplier = 0.0
                }
                newX = multiplier * squareSize + topSideSpace + squareSize / 2
            }
            /**
             * translateX -> centerX of piece
             * Set it to newX -> new position after drag
             */
            translateX = newX
        }
    }

    /**
     * Function for handling "release" event on piece
     */
    private fun released(event: MouseEvent) {
        /**
         * Ensuring piece is behind the grid
         * Move it to the "back" of the children list in Pane
         */
        toBack()

        /**
         * Reset piece position if outside grid bounds
         */
        if (translateX < topSideSpace || translateX > sceneWidth - topSideSpace) {
            animateReset()
        }
        else {
            /**
             * Calculate which column to drop on
             */
            var column = (kotlin.math.floor((event.sceneX - topSideSpace) / squareSize)).toInt()
            if (column < 0) {
                column = 0
            }
            else if (column > Model.width - 1) {
                column = Model.width - 1
            }

            /**
             * Drop piece, check if successful, and animate drop
             * Also remove Event Handlers -> do not want user to interact with piece once dropped
             */
            Model.dropPiece(column)
            if (Model.onPieceDropped.value != null) {
                val dropRow = Model.onPieceDropped.value!!.y
                removeEventFilter(MouseEvent.MOUSE_DRAGGED, dragEvent)
                removeEventFilter(MouseEvent.MOUSE_PRESSED, pressEvent)
                removeEventFilter(MouseEvent.MOUSE_RELEASED, releaseEvent)
                animateDrop(dropRow)
            }
            else {
                animateReset()
            }
        }
    }

    /**
     * Animation for moving piece back to starting position
     * Note: different case for player 1 vs player 2 piece
     */
    private fun animateReset() {
        val animation: Timeline = if (player == Player.ONE) {
            Timeline( KeyFrame(
                Duration(300.0),
                KeyValue(translateXProperty(), 100.0)
            )
            )
        } else {
            Timeline( KeyFrame(
                Duration(300.0),
                KeyValue(translateXProperty(), sceneWidth - 100.0)
            )
            )
        }
        animation.play()
    }

    /**
     * Animation for dropping piece in grid
     */
    private fun animateDrop(row: Int) {
        val animation = Timeline( KeyFrame(
            Duration(400.0),
            KeyValue(this.translateYProperty(), topSideSpace + (row * squareSize + pieceRadius + 5))
        )
        )
        animation.play()
    }

}