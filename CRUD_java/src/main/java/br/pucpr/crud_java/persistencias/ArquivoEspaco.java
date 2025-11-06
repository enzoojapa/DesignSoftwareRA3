package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Espaco;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ArquivoEspaco {


    private static final Path ARQUIVO_DADOS_PATH = Paths.get("espacos.dat");
    private static final Path PROXIMO_ID_PATH = Paths.get("proximoId_espaco.dat"); // Nome específico para evitar conflito


    private static int lerProximoId() {
        if (Files.exists(PROXIMO_ID_PATH)) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(PROXIMO_ID_PATH.toFile()))) {
                return dis.readInt();
            } catch (IOException e) {
                System.err.println("Erro ao ler " + PROXIMO_ID_PATH + ", reiniciando contador. Erro: " + e.getMessage());
                return 1;
            }
        }
        return 1;
    }

    private static void salvarProximoId(int proximoId) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(PROXIMO_ID_PATH.toFile()))) {
            dos.writeInt(proximoId);
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + PROXIMO_ID_PATH + ": " + e.getMessage());
        }
    }

    private static void salvarLista(ArrayList<Espaco> espacos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS_PATH.toFile()))) {
            oos.writeObject(espacos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + ARQUIVO_DADOS_PATH + ": " + e.getMessage());
        }
    }

    public static ArrayList<Espaco> lerLista() {
        if (Files.notExists(ARQUIVO_DADOS_PATH)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DADOS_PATH.toFile()))) {
            return (ArrayList<Espaco>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Erro ao ler " + ARQUIVO_DADOS_PATH + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void adicionarEspaco(int piso, double area) {
        ArrayList<Espaco> espacos = lerLista();
        int novoId = lerProximoId();

        espacos.add(new Espaco(novoId, piso, area));
        salvarLista(espacos);

        salvarProximoId(novoId + 1);
    }

    public static void excluirEspaco(int id) {
        ArrayList<Espaco> espacos = lerLista();
        boolean removido = espacos.removeIf(espaco -> espaco.getId() == id);

        if (removido) {
            salvarLista(espacos);
            System.out.println("Espaço removido com sucesso!");
        } else {
            System.out.println("O ID do espaço não foi encontrado.");
        }
    }
    public static void editarEspaco(int id, int novoPiso, double novaArea){
        ArrayList<Espaco> espacos = lerLista();
        boolean encontrado = false;
        for (Espaco espaco : espacos) {
            if (espaco.getId() == id) {
                espaco.setPiso(novoPiso);
                espaco.setArea(novaArea);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            salvarLista(espacos);
            System.out.println("Espaço editado com sucesso!");
        } else {
            System.out.println("O ID do espaço não foi encontrado.");
        }
    }
}