package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Loja;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class ArquivoLoja {
    private static final String CAMINHO_ARQUIVO = "lojas.dat";


    public static void salvarLista(ArrayList<Loja> lojas) {
        try {
            File arquivo = new File(CAMINHO_ARQUIVO);
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo));
            oos.writeObject(lojas);
            oos.close();
            System.out.println("Lista de lojas salva com sucesso!");
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar lista de lojas:  " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de lojas: " + e.getMessage());
        }
    }


    public static ArrayList<Loja> lerLista() {
        ArrayList<Loja> lista = new ArrayList<>();
        try {
            File arquivo = new File(CAMINHO_ARQUIVO);
            if (arquivo.exists() && arquivo.length() > 0) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CAMINHO_ARQUIVO));
                lista = (ArrayList<Loja>) ois.readObject();
                ois.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao ler lista de lojas:  - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao ler lista de lojas:   " + e.getMessage());
        }
        return lista;
    }

    public static void adicionarLoja(Loja novaLoja) {
        ArrayList<Loja> lojas = lerLista();

        for (Loja l : lojas) {
            if (novaLoja.getLojaNome().equalsIgnoreCase(l.getLojaNome()) || novaLoja.getLojaTelefone().equals(l.getLojaTelefone())) {
                System.out.println("Loja já existente! Loja não cadastrada!");
                return;
            }
        }
        lojas.add(novaLoja);
        salvarLista(lojas);
    }

    public static void editarLoja(String nomeOriginalLoja, String novoNome, String novoTelefone, String novoTipo) throws IllegalArgumentException {
        ArrayList<Loja> lojas = lerLista();
        Loja lojaParaEditar = null;

        for (Loja l : lojas) {
            if (Objects.equals(l.getLojaNome(), nomeOriginalLoja)) {
                lojaParaEditar = l;
                break;
            }
        }

        if (lojaParaEditar == null) {
            throw new IllegalArgumentException("Loja com o nome '" + nomeOriginalLoja + "' não encontrada. Não foi possível atualizar.");
        }

        for (Loja l : lojas) {
            if (l != lojaParaEditar) {
                if (novoNome.equalsIgnoreCase(l.getLojaNome())) {
                    throw new IllegalArgumentException("Já existe outra loja com o nome '" + novoNome + "'. A edição não foi salva.");
                }
                if (novoTelefone.equals(l.getLojaTelefone())) {
                    throw new IllegalArgumentException("Já existe outra loja com o telefone '" + novoTelefone + "'. A edição não foi salva.");
                }
            }
        }
        lojaParaEditar.setLojaNome(novoNome);
        lojaParaEditar.setLojaTelefone(novoTelefone);
        lojaParaEditar.setLojaTipo(novoTipo);
        salvarLista(lojas);
    }


    public static void removerLoja(String lojaNome) {
        ArrayList<Loja> lojas = lerLista();
        for (Loja l : lojas) {
            if (Objects.equals(l.getLojaNome(), lojaNome)) {
                lojas.remove(l);
                salvarLista(lojas);
                System.out.println("Loja removida com sucesso!");
                return;
            }
        }
        System.out.println("O nome da loja não foi encontrado, não foi possível excluir!");
    }
}