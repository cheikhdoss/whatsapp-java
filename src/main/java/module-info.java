module com.example.chatapp.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.media;
    requires org.hibernate.orm.core;

    requires lombok;
    requires jakarta.persistence;


    opens com.example.chatapp.demo to javafx.fxml;
    exports com.example.chatapp.demo;

    opens com.example.chatapp.demo.client to javafx.fxml;
    exports com.example.chatapp.demo.client;

    opens com.example.chatapp.demo.entities to org.hibernate.orm.core;
    exports com.example.chatapp.demo.entities;
}