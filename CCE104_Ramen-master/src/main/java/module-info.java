module com.example.cce104_ramen {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.sql;
    requires kernel;
    requires layout;

    opens com.example.cce104_ramen to javafx.fxml;
    exports com.example.cce104_ramen;
    exports com.example.cce104_ramen.menu;
    opens com.example.cce104_ramen.menu to javafx.fxml;
}