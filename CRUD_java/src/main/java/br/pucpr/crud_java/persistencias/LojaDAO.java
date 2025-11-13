package br.pucpr.crud_java.persistencias;

import br.pucpr.crud_java.models.Loja;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class LojaDAO {

    // Método Auxiliar: Busca uma loja pelo Nome (usado para validação e edição)
    public Loja buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT l FROM Loja l WHERE l.lojaNome = :nomeParam";

            return em.createQuery(jpql, Loja.class)
                    .setParameter("nomeParam", nome)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // Método Auxiliar: Busca uma loja pelo Telefone
    public Loja buscarPorTelefone(String telefone) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT l FROM Loja l WHERE l.lojaTelefone = :telParam";

            return em.createQuery(jpql, Loja.class)
                    .setParameter("telParam", telefone)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void salvar(Loja loja) throws IllegalArgumentException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // A lógica de unicidade (Nome e Telefone) precisa ser verificada no código.
            // O código antigo já fazia essa checagem, então reintroduzimos ela aqui.

            // 1. Verifica se existe outra loja com o mesmo NOME, excluindo a própria loja se estiver em edição
            Loja lojaExistenteNome = buscarPorNome(loja.getLojaNome());
            if (lojaExistenteNome != null && !lojaExistenteNome.getId().equals(loja.getId())) {
                throw new IllegalArgumentException("Já existe outra loja com o nome '" + loja.getLojaNome() + "'.");
            }

            // 2. Verifica se existe outra loja com o mesmo TELEFONE, excluindo a própria loja se estiver em edição
            Loja lojaExistenteTelefone = buscarPorTelefone(loja.getLojaTelefone());
            if (lojaExistenteTelefone != null && !lojaExistenteTelefone.getId().equals(loja.getId())) {
                throw new IllegalArgumentException("Já existe outra loja com o telefone '" + loja.getLojaTelefone() + "'.");
            }

            // Se as validações passarem, salva (INSERT ou UPDATE)
            em.merge(loja);

            em.getTransaction().commit();
            System.out.println("Loja salva/atualizada com sucesso! Nome: " + loja.getLojaNome());
        } catch (IllegalArgumentException e) {
            em.getTransaction().rollback();
            throw e; // Lança a exceção de regra de negócio
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar Loja: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Loja> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Loja l", Loja.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void remover(String lojaNome) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            // 1. Busca a loja pelo nome
            Loja loja = buscarPorNome(lojaNome);

            if (loja != null) {
                // 2. Garante que a entidade está no estado gerenciado e remove
                Loja lojaGerenciada = em.getReference(Loja.class, loja.getId());
                em.remove(lojaGerenciada);
                em.getTransaction().commit();
                System.out.println("Loja removida com sucesso!");
            } else {
                em.getTransaction().rollback();
                System.out.println("O nome da loja não foi encontrado, não foi possível excluir!");
            }
        } catch (NoResultException e) {
            em.getTransaction().rollback();
            System.out.println("O nome da loja não foi encontrado, não foi possível excluir!");
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover Loja: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}