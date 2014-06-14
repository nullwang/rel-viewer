package rel.explorer.dao.e;

import java.util.List;

import org.springframework.stereotype.Repository;

import rel.explorer.dao.BaseDao;
import rel.explorer.domain.RelResult;

@Repository
public class ResultDao extends BaseDao {

    public RelResult getById(String id) {
        return (RelResult) getSession().get(RelResult.class, id);
    }

    public List<RelResult> list() {
        return getSession().createQuery(" from RelResult order by createTime desc").list();

    }
}
