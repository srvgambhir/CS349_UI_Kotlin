module com.example.basic {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;

    opens com.example.basic to javafx.fxml;
    exports com.example.basic;
}