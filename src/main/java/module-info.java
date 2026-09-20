module com.example.sae_tierlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens com.example.sae_tierlist to javafx.fxml;
    exports com.example.sae_tierlist;

    exports com.example.sae_tierlist.controller;
    opens com.example.sae_tierlist.controller to javafx.fxml;

    opens com.example.sae_tierlist.api.pojo to com.fasterxml.jackson.databind;
}