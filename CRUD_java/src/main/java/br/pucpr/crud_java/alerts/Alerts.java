package br.pucpr.crud_java.alerts;

import javafx.scene.control.Alert;

public class Alerts {
    public static void alertInfo(String titulo, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("Sucesso");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void alertError(String titulo, String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText("Erro");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void alertWarning(String titulo, String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText("Aviso");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void alertConfirm(String titulo, String msg){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("Tem certeza?");
        alert.setContentText(msg);
        alert.showAndWait();
    }

}