package restful.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EM {
    static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MYSQL");
    private static EntityManager entityManager = emf.createEntityManager();

    public EM() {
        synchronized (this) {
            if (!entityManager.isOpen()) {
                entityManager = emf.createEntityManager();
            }
        }
    }

    public EntityManager getEntityManager() {
        synchronized (this) {
            if (!entityManager.isOpen()) {
                entityManager = emf.createEntityManager();
            }
            return entityManager;
        }

    }

    public void begin() {
        synchronized (this) {
            try {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void commit() {
        synchronized (this) {
            try {
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void close() {
        synchronized (this) {
            try {
                entityManager.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}