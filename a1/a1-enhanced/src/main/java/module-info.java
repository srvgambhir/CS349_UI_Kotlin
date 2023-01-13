module com.example.enhanced {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;

    opens com.example.enhanced to javafx.fxml;
    exports com.example.enhanced;
}