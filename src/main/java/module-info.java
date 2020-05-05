module playerfx {
    requires java.base;
    requires java.logging;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires FX.BorderlessScene;
    requires java.desktop;

    opens com.vinberts.playerfx to javafx.fxml;
    exports com.vinberts.playerfx;
}
