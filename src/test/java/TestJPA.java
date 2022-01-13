import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.junit.Test;
import restful.database.EM;
import restful.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * @author lwz
 * @create 2021-11-20 3:28
 * @description:
 */
public class TestJPA {
    @Test
    public void myEntityManagerFactory() {
        //创建EntityManagerFactory
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("DM8");
        //创建EntityManager
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        //开启事务
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //持久化操作
        List<User> userList = entityManager.createQuery("from User").getResultList();

        for (User user : userList) {
            System.out.println(user);
        }

        User User = entityManager.find(User.class, 1);
        System.out.println(User);

        //提交事务
        transaction.commit();
        //关闭entityManager
        entityManager.close();
    }

    @Test
    public void myHibernate() {
        //1.读取hibernate.cfg.xml文件
        Configuration cfg = new Configuration();
        cfg.configure();
        //2.创建sessionFactory工厂
        SessionFactory factory = cfg.buildSessionFactory();
        //3.创建session对象
        Session session = factory.openSession();
        //4.开启事务
        Transaction tx = session.beginTransaction();
        //5.执行添加操作
        User user = session.get(User.class, 1);
        System.out.println(user);

        List<User> userList = session.createQuery("from User").list();
        for (User u : userList) {
            System.out.println(u);
        }
        //6.提交事务
        tx.commit();
        //7.关闭资源
        session.close();
    }

    @Test
    public void testEM() {
        EM em = new EM();
//        List<User> result = em.getEntityManager()
//                .createNamedQuery("findAll", User.class)
//                .getResultList();
//        for (User user : result) {
//            System.out.println(user.toString());
//        }
        em.begin();

        User user = new User();
        user.setName("李四");
        List<User> re = em.getEntityManager()
                .createNamedQuery("findUserByName", User.class)
                .setParameter("name", "%" + user.getName() + "%")
                .getResultList();
        for (User u : re) {
            System.out.println(u.toString());
        }

        em.commit();
        em.close();
    }

    @Test
    public void testEM01() {
        EM em = new EM();
        EntityManager entityManager = em.getEntityManager();
        em.begin();


        User user = entityManager.find(User.class, 1);
        user.setName("王五");
        entityManager.persist(user);
        em.commit();
        em.close();
    }

    @Test
    public void testEM02() {
        EM em = new EM();
        EntityManager entityManager = em.getEntityManager();
        em.begin();
        User modifyUser = entityManager.createNamedQuery("findUserByName", User.class)
                .setParameter("name", "张三")
                .getSingleResult();

        System.out.println(modifyUser);
        em.commit();
        em.close();
    }

    @Test
    public void testNewTable() {
        Table table = new Table();

        EM em = new EM();
        EntityManager entityManager = em.getEntityManager();
        em.begin();
        User modifyUser = entityManager.createNamedQuery("findUserByName", User.class)
                .setParameter("name", "张三")
                .getSingleResult();

        System.out.println(modifyUser);
        em.commit();
        em.close();
    }

}
