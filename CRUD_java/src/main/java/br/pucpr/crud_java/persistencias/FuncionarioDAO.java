package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Funcionario;
import jakarta.persistence.EntityManager;
import java.util.List;

public class FuncionarioDAO {
    public Funcionario salvar(Funcionario funcionario) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {

            Funcionario salvo = em.merge(funcionario);

            em.getTransaction().commit();
            System.out.println("Funcionário salvo com sucesso! ID: " + salvo.getId());
            return salvo;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erro ao salvar funcionário: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar funcionário", e);
        } finally {
            em.close();
        }
    }


    public List<Funcionario> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT f FROM Funcionario f", Funcionario.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public Funcionario buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Funcionario.class, id);
        } finally {
            em.close();
        }
    }


    public void excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            Funcionario funcionario = em.find(Funcionario.class, id);

            if (funcionario != null) {
                em.remove(funcionario);
                em.getTransaction().commit();
                System.out.println("Funcionário removido com sucesso! ID: " + id);
            } else {
                em.getTransaction().rollback();
                System.out.println("Funcionário com ID " + id + " não encontrado.");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erro ao remover funcionário: " + e.getMessage());
            throw new RuntimeException("Erro ao remover funcionário", e);
        } finally {
            em.close();
        }
    }
}
