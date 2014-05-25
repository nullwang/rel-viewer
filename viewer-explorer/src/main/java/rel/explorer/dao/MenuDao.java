package rel.explorer.dao;

import java.util.List;
import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelMenu;

@Repository
public class MenuDao extends BaseDao {

    public RelMenu getById(String id) {
        return (RelMenu) getSession().get(RelMenu.class, id);
    }

    public List<RelMenu> getByMasterId(String id) {
        return getSession().createQuery("from RelMenu where masterId = ? or type = ? order by parentId, order").setString(0, id).setInteger(1, new Integer(1)).list();
    }

    public List<RelMenu> getByParentId(String id) {
        return getSession().createQuery("from RelMenu where parentId = ? order by order").setString(0, id).list();
    }
}
