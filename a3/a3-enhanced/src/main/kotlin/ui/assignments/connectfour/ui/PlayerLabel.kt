package ui.assignments.connectfour.ui

import javafx.beans.Observable
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import ui.assignments.connectfour.model.Model
import ui.assignments.connectfour.model.Player

/**
 * Player label: i.e. Player #{1,2}
 */
class PlayerLabel(player: Player): Label() {
    init {
        text = "Player #${player}"
        font = Font.font(25.0)
        textFill = Color.GREY

        /**
         * Change color on player switch to indicate whose turn it is
         */
        val playerListener = { _: Observable, _: Player, new: Player ->
            textFill = if (new == player) {
                Color.BLACK
            } else {
                Color.GREY
            }
        }
        val drawListener = { _: Observable, _: Boolean, _: Boolean ->
            textFill = Color.GREY
        }
        val winListener = { _: Observable, _: Player, _: Player ->
            textFill = Color.GREY
        }
        Model.onNextPlayer.addListener(playerListener)
        Model.onGameDraw.addListener(drawListener)
        Model.onGameWin.addListener(winListener)
    }
}