package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Contrato;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ContratoDAO {

    // O JPA faz a inserção ou atualização com base no ID
    public void salvar(Contrato contrato) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // em.merge() faz o INSERT se o ID for novo (nulo) ou UPDATE se o ID existir.
            em.merge(contrato);

            em.getTransaction().commit();
            System.out.println("Contrato salvo/atualizado com sucesso! ID: " + contrato.getContratoId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar Contrato: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Contrato buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // em.find busca a entidade pelo ID primário
            return em.find(Contrato.class, id);
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Contrato> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Contrato c").getResultList();
        } finally {
            em.close();
        }
    }

    public void remover(Long contratoId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // 1. Encontra a entidade no estado gerenciado
            Contrato contrato = em.find(Contrato.class, contratoId);

            if (contrato != null) {
                // 2. Remove a entidade
                em.remove(contrato);
                em.getTransaction().commit();
                System.out.println("Contrato removido com sucesso!");
            } else {
                em.getTransaction().rollback();
                System.err.println("Contrato não encontrado. Nenhuma remoção foi feita.");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover Contrato: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}