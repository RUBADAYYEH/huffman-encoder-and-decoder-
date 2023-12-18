module com.example.huffmanproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.huffmanproject to javafx.fxml;
    exports com.example.huffmanproject;
}