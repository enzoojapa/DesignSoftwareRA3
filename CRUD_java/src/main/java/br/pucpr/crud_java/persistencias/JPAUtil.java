package br.pucpr.crud_java.persistencias;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final String UNIDADE_PERSISTENCIA = "crud-java-pu";
    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory(UNIDADE_PERSISTENCIA);

    public static EntityManagerFactory getEntityManagerFactory() {
        return FACTORY;
    }
}