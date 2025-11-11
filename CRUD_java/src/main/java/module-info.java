module br.pucpr.crud_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires jakarta.persistence;

    opens br.pucpr.crud_java to javafx.fxml;

    exports br.pucpr.crud_java;
}