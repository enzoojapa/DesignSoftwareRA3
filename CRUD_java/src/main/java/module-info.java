module br.pucpr.crud_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires jakarta.persistence;
    requires eclipselink;

    opens br.pucpr.crud_java.models to eclipselink;
    opens br.pucpr.crud_java to javafx.fxml;

    exports br.pucpr.crud_java;
}