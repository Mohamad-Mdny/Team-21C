module org.example.merchapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jakarta.mail;
    requires javafx.graphics;
    requires java.desktop;
    requires itextpdf;
    //requires org.example.merchapp;

    exports backend;
    opens backend to javafx.fxml;
    exports backend.controllers;
    opens backend.controllers to javafx.fxml;
    exports backend.models;
    opens backend.models to javafx.fxml;
}