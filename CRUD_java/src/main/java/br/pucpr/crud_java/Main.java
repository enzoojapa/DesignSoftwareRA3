package br.pucpr.crud_java;

import br.pucpr.crud_java.persistencias.JPAUtil;
import javafx.application.Application;
import javafx.stage.Stage;

import jakarta.persistence.EntityManagerFactory;

public class Main extends Application {

    public static void main(String[] args) {
        // 1. GARANTIR O FECHAMENTO DO JPA
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Executando Shutdown Hook: Fechando EntityManagerFactory...");
            try {
                EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            } catch (Exception e) {
                System.err.println("Erro ao fechar o EntityManagerFactory: " + e.getMessage());
            }
        }));

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // 2. INICIALIZAÇÃO DO JPA (Antes de carregar qualquer UI)
        System.out.println("Iniciando JPA e configurando a conexão com o banco...");
        try {
            System.out.println("JPA inicializado com sucesso!");
        } catch (Exception e) {
            System.err.println("ERRO DE CONEXÃO: Falha ao iniciar o JPA. Verifique o persistence.xml e o banco.");
            e.printStackTrace();
            System.exit(1);
            return;
        }

        // TELA INICIAL
        TelaInicial telaInicial = new TelaInicial(primaryStage);
        telaInicial.mostrar();
    }
}