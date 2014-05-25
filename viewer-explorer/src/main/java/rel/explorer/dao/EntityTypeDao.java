package rel.explorer.dao;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelEntityType;

@Repository
public class EntityTypeDao extends BaseDao {

    public RelEntityType getById(String id) {
        return (RelEntityType) getSession().get(RelEntityType.class, id);

    }
}
