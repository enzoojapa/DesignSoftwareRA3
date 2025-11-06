package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Contrato;
import br.pucpr.crud_java.persistencias.ArquivoBoleto;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ModalBoletoEdit {
    private Stage stage;
    private Scene cena;
    private Boleto boleto;
    private Contrato contrato;

    private Stage stageOwner;

    public ModalBoletoEdit(Stage stageOwner, Boleto boleto,
                           Contrato contrato){
        this.stage = stageOwner;
        this.boleto = boleto;
        this.contrato = contrato;
    }

    public void mostrar(){
        criarUI();
        stage.showAndWait();
    }

    public void criarUI() {
        this.stage = new Stage();

        VBox camposBol = new VBox();
        camposBol.setStyle("-fx-padding: 10;");
        camposBol.setSpacing(5);

        Label labelNmrDoc = new Label("Número do Documento");
        TextField txtNmrDoc = new TextField(String.valueOf(boleto.getNumeroDocumento()));
        txtNmrDoc.setPromptText("Digite o nº");
        txtNmrDoc.setEditable(false);

        Label labelVal = new Label("Valor");
        TextField txtVal = new TextField(String.valueOf(boleto.getValor()));
        txtVal.setPromptText("Digite o valor");

        Label labelDataVenc = new Label("Data de vencimento");
        DatePicker datePickerVenc = new DatePicker(boleto.getVencimento());
        datePickerVenc.setPromptText("Escolha a data");

        Label labelCedente = new Label("Cedente");
        TextField txtCedente = new TextField("Tijucas Open");
        txtCedente.setEditable(false);

        Label labelBanco = new Label("Banco");
        TextField txtBanco = new TextField("Banco do Brasil");
        txtBanco.setEditable(false);

        Label labelLinhaDig = new Label("Linha digitável");
        TextField txtLinhaDig = new TextField(boleto.getLinhaDigitavel());
        txtLinhaDig.setPromptText("Preencha a linha digitável");
        txtLinhaDig.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.matches("\\d{0,13}")) {
                txtLinhaDig.setText(oldText);
            }
        });

        Button btnEditar = new Button("Salvar alterações");
        btnEditar.setOnAction(e -> {
                    try {
                        int numDoc = Integer.parseInt(txtNmrDoc.getText());
                        double valor = Double.parseDouble(txtVal.getText());
                        LocalDate vencimento = datePickerVenc.getValue();
                        String cedente = txtCedente.getText();
                        String banco = txtBanco.getText();
                        String linhaDig = txtLinhaDig.getText();
                        if (contrato != null) {
                            if (numDoc >= 0 && valor >= 0 &&
                                    vencimento != null && !linhaDig.isEmpty() && linhaDig.matches("\\d{1,13}")){

                                ArquivoBoleto.editarBoleto(numDoc, valor,
                                        vencimento, cedente, banco, linhaDig,
                                        contrato.getContratoId());

                                Alerts.alertInfo("Editado",
                                        "Boleto editado com sucesso");

                                this.stage.close();
                            } else {
                                Alerts.alertError("Erro",
                                        "Preencha os campos " +
                                                "corretamente");
                            }
                        }
                    } catch (NumberFormatException ex) {
                        Alerts.alertError("Erro", "Insira dados válidos!");
                    }
                }
        );

        Button btnVoltar = new Button("Cancelar");
        btnVoltar.setOnAction(e -> this.stage.close());

        camposBol.getChildren().addAll(labelNmrDoc, txtNmrDoc, labelVal,
                txtVal, labelDataVenc, datePickerVenc, labelCedente,
                txtCedente, labelBanco, txtBanco, labelLinhaDig, txtLinhaDig,
                btnEditar, btnVoltar);

        this.cena = new Scene(camposBol, 400, 400);
        this.stage.setScene(this.cena);
    }
}