package ui.assignments.connectfour

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import ui.assignments.connectfour.ui.View
import ui.assignments.connectfour.ui.sceneHeight
import ui.assignments.connectfour.ui.sceneWidth


class ConnectFourApp : Application() {
    override fun start(stage: Stage) {
        val scene = Scene(View(), sceneWidth, sceneHeight)
        stage.title = "CS349 - A3 Connect Four - s3gambhi"
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }
}