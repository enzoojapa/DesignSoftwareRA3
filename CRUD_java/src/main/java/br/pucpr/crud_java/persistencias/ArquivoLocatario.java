package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Locatario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoLocatario {

    public static void salvarLista(List<Locatario> locatarios){
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaGestaoPU");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(locatarios);
            em.getTransaction().commit();
            em.close();
            emf.close();
            System.out.println("Lista de locatarios salva com sucesso!");
    }

    public static List<Locatario> lerLista(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaGestaoPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select l from Locatario l");
        List<Locatario> lista = query.getResultList();
        em.close();
        emf.close();
        return lista;
    }


    public static boolean adicionarLocatario(Locatario novoLocatario) {
        List<Locatario> locatarios = lerLista();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaGestaoPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        for (Locatario loc : locatarios) {
            if (loc.getLocatarioCnpj().equals(novoLocatario.getLocatarioCnpj())) {
                System.out.println("CNPJ já cadastrado. Locatário não adicionado.");
                return false;
            }
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
        return true;
    }

    public static void editarLocatario(String cnpj, String novoNome, String novoEmail, String novoTelefone) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaGestaoPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery("select l from Locatario l where l.locatarioCnpj = :cnpj");
        query.setParameter("cnpj", cnpj);
        Locatario locatario = (Locatario) query.getSingleResult();
        locatario.setLocatarioNome(novoNome);
        locatario.setLocatarioEmail(novoEmail);
        locatario.setLocatarioTelefone(novoTelefone);

        em.getTransaction().commit();
        em.close();
        emf.close();
    }


    public static void removerLocatario(String cnpj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaGestaoPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery("select l from Locatario l where l.locatarioCnpj = :cnpj");
        query.setParameter("cnpj", cnpj);
        Locatario locatario = (Locatario) query.getSingleResult();
        em.remove(locatario);

        em.getTransaction().commit();
        em.close();
        emf.close();
    }
}
