package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Espaco;
import br.pucpr.crud_java.persistencias.ArquivoEspaco;
import javafx.geometry.Insets;
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

public class ModalEspacoEdit {

    private Stage stage;
    private Espaco espaco;

    public ModalEspacoEdit(Stage stageOwner, Espaco espaco) {
        this.stage = new Stage();
        stage.initOwner(stageOwner);
        stage.initModality(Modality.WINDOW_MODAL);

        this.espaco = espaco;
    }

    public void mostrar() {
        criarUI();
        stage.showAndWait();
    }

    private void criarUI() {
        stage.setTitle("Editar Espaço - ID: " + espaco.getId());

        VBox painelCampos = new VBox(10);
        painelCampos.setStyle("-fx-padding: 20;");

        Label labelId = new Label("ID do Espaço");
        TextField txtId = new TextField(String.valueOf(espaco.getId()));
        txtId.setEditable(false);

        Label labelArea = new Label("Área do espaço (m²)");
        TextField txtArea = new TextField(String.format("%.2f", espaco.getArea()));
        txtArea.setPromptText("Digite a nova área");
        Pattern pattern = Pattern.compile("\\d*([,.]\\d{0,2})?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        txtArea.setTextFormatter(formatter);


        Label labelPiso = new Label("Piso do espaço (1 ou 2)");
        TextField txtPiso = new TextField(String.valueOf(espaco.getPiso()));
        txtPiso.setPromptText("Digite o novo piso");

        Button btnSalvar = new Button("Salvar Alterações");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setOnAction(e -> {
            try {
                Long id = espaco.getId();
                double novaArea = Double.parseDouble(txtArea.getText().replace(',', '.'));
                int novoPiso = Integer.parseInt(txtPiso.getText());

                if (novaArea <= 0 || (novoPiso != 1 && novoPiso != 2)) {
                    Alerts.alertError("Dados Inválidos", "Preencha os campos corretamente!\n(Área > 0 e Piso = 1 ou 2)");
                    return;
                }

                ArquivoEspaco.editarEspaco(id, novoPiso, novaArea);

                Alerts.alertInfo("Sucesso", "Espaço editado com sucesso!");

                this.stage.close();

            } catch (NumberFormatException ex) {
                Alerts.alertError("Erro de Formato", "Verifique se a área e o piso são números válidos.");
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> this.stage.close());

        painelCampos.getChildren().addAll(
                labelId, txtId,
                labelArea, txtArea,
                labelPiso, txtPiso,
                btnSalvar, btnCancelar
        );

        Scene cena = new Scene(painelCampos, 400, 350);
        this.stage.setScene(cena);
    }
}