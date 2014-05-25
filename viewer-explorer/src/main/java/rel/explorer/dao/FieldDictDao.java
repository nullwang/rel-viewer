package rel.explorer.dao;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelFieldDict;

@Repository
public class FieldDictDao extends BaseDao {

    public synchronized RelFieldDict getById(String id) {
        return (RelFieldDict) getSession().get(RelFieldDict.class, id);

    }
}
