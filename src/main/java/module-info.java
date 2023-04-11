
module multisocketserverfx {
    requires com.jtconnors.socket;
    requires java.base;
    requires java.logging;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    exports com.jtconnors.multisocketserverfx;
    opens com.jtconnors.multisocketserverfx to javafx.fxml;
}
