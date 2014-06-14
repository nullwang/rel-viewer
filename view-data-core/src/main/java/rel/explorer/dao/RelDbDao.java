package rel.explorer.dao;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelDb;

@Repository
public class RelDbDao extends BaseDao {
    public RelDb getById(String id) {
        return (RelDb) getSession().get(RelDb.class, id);

    }
}
