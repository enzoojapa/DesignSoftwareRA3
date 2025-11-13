package br.pucpr.crud_java.views;

import br.pucpr.crud_java.alerts.Alerts;
import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Contrato;
import br.pucpr.crud_java.persistencias.BoletoDAO; // O BoletoDAO JPA
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality; // Importante para modais
import javafx.stage.Stage;

import java.time.LocalDate;

public class ModalBoletoEdit {
    private Stage stage;
    private Boleto boleto;
    private final BoletoDAO boletoDAO; // NOVO: Instância do DAO JPA

    // O Contrato não é estritamente necessário para edição do Boleto no JPA,
    // mas o mantemos para consistência do construtor
    // private Contrato contrato;

    public ModalBoletoEdit(Stage stageOwner, Boleto boleto, Contrato contrato){
        // Criar um novo Stage (janela) para o modal
        this.stage = new Stage();
        this.boleto = boleto;
        this.boletoDAO = new BoletoDAO(); // Inicializa o DAO

        // Define o modal como bloco (bloqueia o stage pai)
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.initOwner(stageOwner); // Define o proprietário
    }

    public void mostrar(){
        criarUI();
        // Usa showAndWait para que o fluxo de código pare até o modal fechar
        this.stage.showAndWait();
    }

    public void criarUI() {
        this.stage.setTitle("Editar Boleto Nº: " + boleto.getId()); // Usa o ID do JPA

        VBox camposBol = new VBox();
        camposBol.setStyle("-fx-padding: 10;");
        camposBol.setSpacing(5);

        Label labelNmrDoc = new Label("ID/Nº Documento (JPA ID)");
        // Usa o ID primário do JPA para referência
        TextField txtNmrDoc = new TextField(String.valueOf(boleto.getId()));
        txtNmrDoc.setPromptText("ID");
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
                // Não precisamos mais do numDoc, pois usamos o this.boleto.getId()

                double valor = Double.parseDouble(txtVal.getText());
                LocalDate vencimento = datePickerVenc.getValue();
                String cedente = txtCedente.getText();
                String banco = txtBanco.getText();
                String linhaDig = txtLinhaDig.getText();

                // Validação mínima
                if (valor >= 0 && vencimento != null && !linhaDig.isEmpty() && linhaDig.matches("\\d{1,13}")){

                    // 1. ATUALIZA o OBJETO em memória
                    this.boleto.setValor(valor);
                    this.boleto.setVencimento(vencimento);
                    this.boleto.setCedente(cedente);
                    this.boleto.setBanco(banco);
                    this.boleto.setLinhaDigitavel(linhaDig);

                    // 2. CORRIGIDO: Chama o DAO JPA para salvar. O merge detecta o ID e faz o UPDATE.
                    // Passamos o objeto Boleto atualizado. Não precisamos passar o contrato ID aqui,
                    // pois o objeto Boleto já tem a referência Contrato (se foi carregado corretamente).
                    boletoDAO.salvar(this.boleto, this.boleto.getContrato().getContratoId());

                    Alerts.alertInfo("Editado", "Boleto editado com sucesso");

                    this.stage.close();
                } else {
                    Alerts.alertError("Erro", "Preencha os campos corretamente");
                }
            } catch (NumberFormatException ex) {
                Alerts.alertError("Erro", "Insira dados válidos para o valor!");
            } catch (RuntimeException ex) {
                // Captura exceções do JPA (ex: falha na transação, unicidade)
                Alerts.alertError("Erro de Persistência", "Falha ao editar: " + ex.getMessage());
            }
        });

        Button btnVoltar = new Button("Cancelar");
        btnVoltar.setOnAction(e -> this.stage.close());

        camposBol.getChildren().addAll(labelNmrDoc, txtNmrDoc, labelVal,
                txtVal, labelDataVenc, datePickerVenc, labelCedente,
                txtCedente, labelBanco, txtBanco, labelLinhaDig, txtLinhaDig,
                btnEditar, btnVoltar);

        Scene cena = new Scene(camposBol, 400, 400);
        this.stage.setScene(cena);
    }
}