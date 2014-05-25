package rel.explorer.dao;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelEntityPhoto;

@Repository
public class EntityPhotoDao extends BaseDao {
    public RelEntityPhoto getById(String id) {
        return (RelEntityPhoto) getSession().get(RelEntityPhoto.class, id);
    }
}
