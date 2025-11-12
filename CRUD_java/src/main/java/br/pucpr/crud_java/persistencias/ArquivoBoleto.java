package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Contrato;
import br.pucpr.crud_java.models.Locatario;
import br.pucpr.crud_java.models.Loja;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class ArquivoBoleto {
    private static final String PROXIMO_NUM_CAMINHO = "proximoNum_Boleto" +
            ".dat";

    private static int lerProximoNum(){
        try {
            File arquivoNumDoc = new File(PROXIMO_NUM_CAMINHO);
            if (arquivoNumDoc.exists()){
                DataInputStream dis =
                        new DataInputStream(new FileInputStream(arquivoNumDoc));
                return dis.readInt();
            }
            return 1;
        } catch (IOException e){
            System.err.println("Erro ao ler " + PROXIMO_NUM_CAMINHO + ", " +
                    "reiniciando contador. Erro: " + e.getMessage());
            return 1;
        }
    }

    private static void salvarProximoNumDoc(int proximoNumDoc){
        try {
            File arquivoNumDoc = new File(PROXIMO_NUM_CAMINHO);
            if(!arquivoNumDoc.exists()){
                arquivoNumDoc.createNewFile();
            }
            DataOutputStream dos =
                    new DataOutputStream(new FileOutputStream(arquivoNumDoc));
            dos.writeInt(proximoNumDoc);
        } catch (IOException e){
            System.err.println("Erro ao salvar em " + PROXIMO_NUM_CAMINHO +
                    ". Erro: " + e.getMessage());
        }
    }

    public static ArrayList<Boleto> lerLista(Long contratoId){
        ArrayList<Contrato> contratos = ArquivoContrato.lerLista();
        ArrayList<Boleto> boletosContrato = new ArrayList<>();
        try {
            for (Contrato c : contratos){
                if (c.getId() == contratoId){
                    boletosContrato = c.getBoletos();
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Erro ao ler lista: " + e.getMessage());
        }
        return boletosContrato;
    }

    public static void adicionarBoleto(Boleto novoBoleto, Long contratoId) {
        ArrayList<Contrato> contratos = ArquivoContrato.lerLista();
        int novoNumDoc = lerProximoNum();


        for (Contrato c : contratos) {
            if (c.getId() == contratoId){
                for (Boleto b : c.getBoletos()){
                    if (novoBoleto.getNumeroDocumento() == b.getNumeroDocumento()){
                        System.out.println("Boleto já existente!");
                        return;
                    }
                }
                novoBoleto.setNumeroDocumento(novoNumDoc);
                c.getBoletos().add(novoBoleto);
                salvarProximoNumDoc(novoNumDoc + 1);
                break;
            }
        }
        ArquivoContrato.salvarLista(contratos);
    }

    public static void removerBoleto(int numeroDocumento, Long contratoId) {
        try {
            ArrayList<Contrato> contratos = ArquivoContrato.lerLista();
            boolean removido  = false;
            for (Contrato c : contratos) {
                if (c.getId() == contratoId) {
                    c.getBoletos().removeIf(
                            b -> b.getNumeroDocumento() == numeroDocumento);
                    removido = true;
                    break;
                }
            }
            if (removido) {
                ArquivoContrato.salvarLista(contratos);
                System.out.println("Boleto removido com sucesso!");
            } else {
                System.err.println("Boleto não encontrado, nenhum boleto foi " +
                        "apagado");
            }
        } catch (NullPointerException e){
            System.err.println("Não foi possível ler lista de contratos. " +
                    "Erro: " + e.getMessage());
        }
    }

    public static void editarBoleto(int numeroDocumento,
                                    double valor,
                                    LocalDate vencimento, String cedente,
                                    String banco, String linhaDigitavel,
                                    Long contratoId){
        try {
            ArrayList<Contrato> contratos = ArquivoContrato.lerLista();

            for (Contrato c : contratos) {
                if (c.getId() == contratoId) {
                    ArrayList<Boleto> boletos = c.getBoletos();
                    for (Boleto b : boletos) {
                        if (b.getNumeroDocumento() == numeroDocumento) {
                            b.setValor(valor);
                            b.setVencimento(vencimento);
                            b.setCedente(cedente);
                            b.setBanco(banco);
                            b.setLinhaDigitavel(linhaDigitavel);
                            ArquivoContrato.salvarLista(contratos);
                            System.out.println(
                                    "Boleto atualizado com sucesso!");
                            return;
                        }
                    }
                    System.err.println("Boleto não encontrado, nenhuma " +
                            "alteração foi feita!");
                }
            }
        } catch (NullPointerException e){
            System.err.println("Não foi possivel ler lista de contratos. " +
                    "Erro: " + e.getMessage());
        }
        }
}