package br.pucpr.crud_java; // Se você quiser mantê-la aqui

import br.pucpr.crud_java.views.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TelaInicial {
    private final Stage stage; // Alterado para final

    public TelaInicial(Stage stage) {
        this.stage = stage;
    }

    public void mostrar() {
        criarUI();
        this.stage.show();
    }

    private void criarUI() {
        stage.setTitle("Tela Inicial - Sistema de Gestão");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-padding: 10;");

        HBox navBar = criarMenuNavegacao();
        borderPane.setTop(navBar);

        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Bem-vindo ao Sistema de Gestão");
        welcomeLabel.setFont(new Font("Montserrat", 32));

        Label infoLabel = new Label("Utilize o menu acima para navegar entre as seções.");
        infoLabel.setFont(new Font("Arial", 16));

        centerContent.getChildren().addAll(welcomeLabel, infoLabel);
        borderPane.setCenter(centerContent);

        Scene cena = new Scene(borderPane, 900, 600);
        this.stage.setScene(cena);
    }

    private HBox criarMenuNavegacao() {
        HBox navBar = new HBox(15);
        navBar.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-background-color: lightgrey;");
        String styleBtn = "-fx-background-color: transparent; -fx-font-weight: bold;";

        Button btnHome = new Button("Home");
        btnHome.setStyle(styleBtn);
        btnHome.setOnAction(e -> this.mostrar());

        Button btnLocatarios = new Button("Locatários");
        btnLocatarios.setStyle(styleBtn);
        btnLocatarios.setOnAction(e -> new LocatarioView(stage).mostrar());

        Button btnContratos = new Button("Contratos");
        btnContratos.setStyle(styleBtn);
        btnContratos.setOnAction(e -> new ContratoView(stage).mostrar());

        Button btnLojas = new Button("Lojas");
        btnLojas.setStyle(styleBtn);
        btnLojas.setOnAction(e -> new LojaView(stage).mostrar());

        Button btnEspacos = new Button("Espaços");
        btnEspacos.setStyle(styleBtn);
        btnEspacos.setOnAction(e -> new EspacoView(stage).mostrar());

        Button btnFuncionarios = new Button("Funcionários");
        btnFuncionarios.setStyle(styleBtn);
        btnFuncionarios.setOnAction(e -> new FuncionarioView(stage).mostrar());

        Button btnCargos = new Button("Cargos");
        btnCargos.setStyle(styleBtn);
        btnCargos.setOnAction(e -> new CargoView(stage).mostrar());

        navBar.getChildren().addAll(btnHome, btnLocatarios, btnContratos, btnLojas, btnEspacos, btnFuncionarios, btnCargos);
        return navBar;
    }
}