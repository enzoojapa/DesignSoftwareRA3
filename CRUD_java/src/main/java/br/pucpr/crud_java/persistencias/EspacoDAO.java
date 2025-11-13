package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Espaco;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EspacoDAO {

    public void salvar(Espaco espaco) {
        // Obtém a sessão de trabalho
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // Se espaco.getId() for null (novo), o JPA insere.
            // Se espaco.getId() tiver valor (existente), o JPA atualiza.
            em.merge(espaco);

            em.getTransaction().commit();
            System.out.println("Espaço salvo/atualizado com sucesso! ID: " + espaco.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar Espaço: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Espaco buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Em.find busca o objeto pelo ID primário
            return em.find(Espaco.class, id);
        } finally {
            em.close();
        }
    }


    @SuppressWarnings("unchecked")
    public List<Espaco> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // JPQL: busca todos os objetos da entidade Espaco
            return em.createQuery("SELECT e FROM Espaco e").getResultList();
        } finally {
            em.close();
        }
    }

    public void excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // 1. Encontra a entidade no estado gerenciado
            Espaco espaco = em.find(Espaco.class, id);

            if (espaco != null) {
                // 2. Remove a entidade
                em.remove(espaco);
                em.getTransaction().commit();
                System.out.println("Espaço removido com sucesso!");
            } else {
                em.getTransaction().rollback();
                System.out.println("O ID do espaço não foi encontrado.");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover Espaço: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}