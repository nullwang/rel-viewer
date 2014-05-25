package rel.explorer.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.Resource;

/**
 * Created by www on 2014/5/24.
 */
public class BaseDao {
    /**
     * 向DAO层注入SessionFactory
     */
    @Resource
    private SessionFactory sessionFactory;

    /**
     * 获取当前工作的Session
     */
    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

}
