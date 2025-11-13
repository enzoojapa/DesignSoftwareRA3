package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Cargo;
import br.pucpr.crud_java.persistencias.CargoDAO;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ModalCargoEdit {

    private final Stage stage;
    private final Cargo cargo;
    private final CargoDAO cargoDAO;

    public ModalCargoEdit(Stage stageOwner, Cargo cargo) {
        this.stage = new Stage();
        this.stage.initOwner(stageOwner);
        this.stage.initModality(Modality.WINDOW_MODAL);

        this.cargo = cargo;
        this.cargoDAO = new CargoDAO();
    }

    public void mostrar() {
        criarUI();
        stage.showAndWait();
    }

    private void criarUI() {
        stage.setTitle("Editar Cargo - ID: " + cargo.getId());

        VBox painelCampos = new VBox(10);
        painelCampos.setStyle("-fx-padding: 20;");

        Label labelId = new Label("ID do Cargo");
        TextField txtId = new TextField(String.valueOf(cargo.getId()));
        txtId.setEditable(false);

        Label labelNome = new Label("Nome do cargo");
        TextField txtNome = new TextField(cargo.getNome());
        txtNome.setPromptText("Digite o novo nome");

        Label labelDescricao = new Label("Descrição do cargo");
        TextField txtDescricao = new TextField(cargo.getDescricao());
        txtDescricao.setPromptText("Digite a nova descrição");

        Button btnSalvar = new Button("Salvar Alterações");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setOnAction(e -> {
            try {
                String novoNome = txtNome.getText();
                String novaDescricao = txtDescricao.getText();

                cargo.setNome(novoNome);
                cargo.setDescricao(novaDescricao);

                cargoDAO.editar(cargo.getId(), cargo);

                Alerts.alertInfo("Sucesso", "Cargo editado com sucesso!");

                this.stage.close();

            } catch (RuntimeException ex) {
                Alerts.alertError("Erro de Persistência", "Falha ao salvar a edição: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> this.stage.close());

        painelCampos.getChildren().addAll(
                labelId, txtId,
                labelNome, txtNome,
                labelDescricao, txtDescricao,
                btnSalvar, btnCancelar
        );

        Scene cena = new Scene(painelCampos, 400, 350);
        this.stage.setScene(cena);
    }
}