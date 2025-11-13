package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Locatario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class LocatarioDAO {

    // O JPA substitui a lógica manual de verificar CNPJ e salvar lista.
    public void salvar(Locatario locatario) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // em.merge() faz o INSERT (se locatario.getId() for null) ou UPDATE (se existir)
            em.merge(locatario);

            em.getTransaction().commit();
            System.out.println("Locatário salvo/atualizado com sucesso! CNPJ: " + locatario.getLocatarioCnpj());
        } catch (Exception e) {
            em.getTransaction().rollback();
            // Exceção de CNPJ duplicado será capturada aqui (DataIntegrityViolationException)
            throw new RuntimeException("Erro ao salvar Locatário: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // --- Método para buscar pelo CNPJ (Substitui a busca manual por loop) ---
    public Locatario buscarPorCnpj(String cnpj) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // JPQL: Busca a Entidade pelo CNPJ, que é o campo de negócio único
            String jpql = "SELECT l FROM Locatario l WHERE l.locatarioCnpj = :cnpjParam";

            return em.createQuery(jpql, Locatario.class)
                    .setParameter("cnpjParam", cnpj)
                    // getSingleResult() garante que apenas um resultado é retornado
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Retorna null se não encontrar (equivalente ao loop manual)
        } finally {
            em.close();
        }
    }


    public List<Locatario> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Locatario l", Locatario.class).getResultList();
        } finally {
            em.close();
        }
    }


    public void remover(String cnpj) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // 1. Encontra a entidade usando o método buscarPorCnpj
            Locatario locatario = buscarPorCnpj(cnpj);

            if (locatario != null) {
                // 2. Remove o objeto encontrado (é necessário buscar novamente na mesma sessão ou usar em.merge)
                Locatario locatarioGerenciado = em.getReference(Locatario.class, locatario.getId());
                em.remove(locatarioGerenciado);
                em.getTransaction().commit();
                System.out.println("Locatário removido com sucesso!");
            } else {
                em.getTransaction().rollback();
                System.out.println("CNPJ não encontrado. Nenhuma remoção feita.");
            }
        } catch (NoResultException e) {
            em.getTransaction().rollback(); // Caso a busca por CNPJ falhe
            System.out.println("CNPJ não encontrado. Nenhuma remoção feita.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover Locatário: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}