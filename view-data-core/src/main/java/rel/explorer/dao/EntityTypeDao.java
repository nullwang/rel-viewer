package rel.explorer.dao;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelEntityType;

import java.util.List;

@Repository
public class EntityTypeDao extends BaseDao {

    public RelEntityType getById(String id) {
        return (RelEntityType) getSession().get(RelEntityType.class, id);
    }

    public List<RelEntityType> getAll(){
        return getSession().createQuery(" from " + RelEntityType.class.getName()).list();
    }


}
