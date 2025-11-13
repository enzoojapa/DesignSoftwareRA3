module br.pucpr.crud_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;


    // JavaFX
    opens br.pucpr.crud_java to javafx.fxml;
    opens br.pucpr.crud_java.views to javafx.fxml;

    opens br.pucpr.crud_java.models;

    // Exportações normais
    exports br.pucpr.crud_java;
    exports br.pucpr.crud_java.models;
    exports br.pucpr.crud_java.views;
}
