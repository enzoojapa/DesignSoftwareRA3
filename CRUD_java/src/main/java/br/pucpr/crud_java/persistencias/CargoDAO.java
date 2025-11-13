package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Cargo;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CargoDAO {
    public Cargo salvar(Cargo cargo) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {

            Cargo salvo = em.merge(cargo);

            em.getTransaction().commit();
            System.out.println("Cargo salvo com sucesso! ID: " + salvo.getId());
            return salvo;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erro ao salvar cargo: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar cargo", e);
        } finally {
            em.close();
        }
    }


    public List<Cargo> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cargo c", Cargo.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public Cargo buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Cargo.class, id);
        } finally {
            em.close();
        }
    }


    public void excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            Cargo cargo = em.find(Cargo.class, id);

            if (cargo != null) {
                em.remove(cargo);
                em.getTransaction().commit();
                System.out.println("Cargo removido com sucesso! ID: " + id);
            } else {
                em.getTransaction().rollback();
                System.out.println("Cargo com ID " + id + " não encontrado.");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erro ao remover cargo: " + e.getMessage());
            throw new RuntimeException("Erro ao remover cargo", e);
        } finally {
            em.close();
        }
    }
}
