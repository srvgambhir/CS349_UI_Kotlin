This is a repository containing the applications I created during CS349 - User Interfaces. The first three programs were desktop applications with various functionality (a notes app, a data graphing app, and a connect 4 game clone). The final program is an Android mobile app. All programs were created using Kotlin.

Connect Four and Notes for Android included starter code from the University of Waterloo. In both programs, the Model in the MVC pattern was given.

# A1 Notes
    Sarvesh Gambhir (s3gambhi 20841905)
 
    ## Setup
    * Ubuntu 22.04.1 LTS
    * IntelliJ IDEA 2022.2.1 (Community Edition)
    * kotlin.jvm 1.7.10
    * Java SDK 17.0.4
 
    ## Enhancement 
    I added extra sorting options
    * Most recent: sorts by most recently added notes
    * Least recent: sorts by least recently added notes
    * These options are available in the "Order By" menu bar

    ## Additional Note
    I used the JavaFX generator when creating the new project. Therefore my source code is in the following file:
    /src/main/kotlin/com/example/basic/HelloApplication.kt

# A2 Graphs

    Sarvesh Gambhir (s3gambhi 20841905)

    ## Setup
    * Ubuntu 22.04.1 LTS
    * IntelliJ IDEA 2022.2.1 (Community Edition)
    * kotlin.jvm 1.7.10
    * Java SDK 17.0.4
    * JavaFX version 17.0.2

    ## Enhancement/Bonus
    I implemented the Bonus functionality (i.e. regarding negative value handling)


    ## Additional Note
    My source code is in the following file:
    /src/main/kotlin/com/example/basic/GrahApplication.kt

# A3 Connect Four

    Sarvesh Gambhir (s3gambhi 20841905)

    ## Setup
    * Ubuntu 22.04.1 LTS
    * IntelliJ IDEA 2022.2.1 (Community Edition)
    * kotlin.jvm 1.7.10
    * Java SDK 17.0.4
    * JavaFX version 17.0.2

    ## Enhancement/Bonus
    I added a "Game Reset" functionality:
    - At the bottom left of the screen, there is a "Reset" button
    - Pressing it removes all tokens and brings the game to a fresh replayable state


    ## Additional Note
    My UI source code is split into three separate class files: View.kt, Piece.kt, PlayerLabel.kt
    These files are stored in the following folder: /src/main/kotlin/ui/assignments/connectfour/ui

# A4 Notes for Android

    Sarvesh Gambhir (s3gambhi 20841905)

    ## Setup
    * Ubuntu 22.04.1 LTS
    * IntelliJ IDEA 2022.2.1 (Community Edition)
    * kotlin.jvm 1.7.20
    * Java SDK 11.0.17 (temurin)
    * Android 11.0 (R) API Level 32

    ## Enhancement/Bonus
    * I added a welcome screen to the app
    * When the app opens, click anywhere on the welcome screen to enter the main app

