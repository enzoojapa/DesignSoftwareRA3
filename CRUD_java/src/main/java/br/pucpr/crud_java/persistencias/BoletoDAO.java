package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Boleto;
import br.pucpr.crud_java.models.Contrato;
import jakarta.persistence.EntityManager;
import java.util.List;

public class BoletoDAO {

    public void salvar(Boleto boleto, Long contratoId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // 1. Buscar a entidade Contrato do banco (essencial para o relacionamento)
            Contrato contrato = em.find(Contrato.class, contratoId);

            if (contrato == null) {
                throw new IllegalArgumentException("Contrato com ID " + contratoId + " não encontrado.");
            }

            // 2. Definir o relacionamento no objeto Boleto
            boleto.setContrato(contrato);

            // O JPA cuida do INSERT (se novo) ou UPDATE (se existente)
            em.merge(boleto);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar Boleto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Boleto> buscarPorContrato(Long contratoId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // JPQL (Java Persistence Query Language): Seleciona boletos onde a coluna 'contrato' tem o ID desejado
            String jpql = "SELECT b FROM Boleto b WHERE b.contrato.id = :idContrato";

            return em.createQuery(jpql, Boleto.class)
                    .setParameter("idContrato", contratoId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Remove o boleto usando o ID primário do JPA (Long id), não o numeroDocumento
    public void excluir(Long boletoId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            Boleto boleto = em.find(Boleto.class, boletoId);

            if (boleto != null) {
                em.remove(boleto);
                em.getTransaction().commit();
                System.out.println("Boleto removido com sucesso!");
            } else {
                em.getTransaction().rollback();
                System.err.println("Boleto não encontrado!");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir Boleto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}