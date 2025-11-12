package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Contrato;
import java.io.*;
import java.util.ArrayList;

public class ArquivoContrato {
    private static final String CAMINHO_ARQUIVO = "contratos.dat";
    private static final String PROXIMO_ID_CAMINHO = "proximoId_contrato.dat";

    private static int lerProximoId(){
        try {
            File arquivoId = new File(PROXIMO_ID_CAMINHO);
            if (arquivoId.exists()){
                DataInputStream dis =
                        new DataInputStream(new FileInputStream(arquivoId));
                return dis.readInt();
            }
            return 1;
        } catch (IOException e){
            System.err.println("Erro ao ler " + PROXIMO_ID_CAMINHO + ", " +
                    "reiniciando contador. Erro: " + e.getMessage());
            return 1;
        }
    }

    private static void salvarProximoId(int id){
        try {
            File arquivoId = new File(PROXIMO_ID_CAMINHO);
            if (!arquivoId.exists()){
                arquivoId.createNewFile();
            }
            DataOutputStream dos =
                    new DataOutputStream(new FileOutputStream(arquivoId));
            dos.writeInt(id);
        } catch (IOException e){
            System.err.println("Erro ao salvar em " + PROXIMO_ID_CAMINHO + "." +
                    " Erro: " + e.getMessage());
        }
    }

    public static void salvarLista(ArrayList<Contrato> contratos){
        try {
            File arquivo = new File(CAMINHO_ARQUIVO);
            if (!arquivo.exists()){
                arquivo.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo));
            oos.writeObject(contratos);
            oos.close();
            System.out.println("Lista de contratos salva com sucesso!");
        } catch (FileNotFoundException e){
            System.err.println("Erro ao salvar lista de contratos: " + e.getMessage());
        } catch (IOException e){
            System.err.println("Erro ao salvar lista de contratos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Contrato> lerLista(){
        ArrayList<Contrato> lista = new ArrayList<>();
        try {
            File arquivo = new File(CAMINHO_ARQUIVO);
            if (arquivo.exists() && arquivo.length() > 0) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CAMINHO_ARQUIVO));
                lista = (ArrayList<Contrato>) ois.readObject();
                ois.close();
            }
        } catch (EOFException e) {
            System.err.println("Arquivo de contratos vazio ou corrompido. Iniciando com lista vazia.");
        } catch (ClassNotFoundException e){
            System.err.println("Erro ao ler lista de contratos: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao ler lista de contratos: " + e.getMessage());
        }
        return lista;
    }

    public static void adicionarContrato(Contrato novoContrato) {
        ArrayList<Contrato> contratos = lerLista();
        int novoId = lerProximoId();

        for (Contrato c : contratos) {
            if (novoContrato.getId() == c.getId()) {
                System.out.println("ID do contrato já cadastrado. Contrato não adicionado.");
                return;
            }
        }
        contratos.add(novoContrato);
        salvarLista(contratos);

        salvarProximoId(novoId + 1);
    }


    public static void removerContrato(Long contratoId){
        ArrayList<Contrato> contratos = lerLista();
        boolean removido = false;
        for (Contrato c : contratos){
            if (c.getId() == contratoId) {
                contratos.remove(c);
                removido = true;
                break;
            }
        }
        if (removido){
            salvarLista(contratos);
            System.out.println("Contrato removido com sucesso!");
        } else {
            System.err.println("Contrato não encontrado. Nenhuma remoção foi " +
                    "feita");
        }
    }

    public static void atualizarContrato(Contrato contratoAtualizado) {
        ArrayList<Contrato> contratos = lerLista();
        for (int i = 0; i < contratos.size(); i++) {
            if (contratos.get(i).getId() == contratoAtualizado.getId()) {
                contratos.set(i, contratoAtualizado);
                break;
            }
        }
        salvarLista(contratos);
    }

}