package rel.explorer.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelEntityType;
import rel.explorer.domain.RelFieldType;
import rel.explorer.domain.RelPath;

@Repository
public class FieldTypeDao extends BaseDao{

	public RelFieldType getById(String id)
	{
		return (RelFieldType) getSession().get(RelFieldType.class, id);

	}
	
	public List<RelFieldType> getFieldsByPathId(String masterId)
	{
		return getSession().createQuery("from RelFieldType where masterId=? order by keySort").setString(0, masterId).list();
	}
	
	public List<RelFieldType> getFieldsByEntTypeId(String masterId)
	{
		return getSession().createQuery("from RelFieldType where masterId=? or masterView=? order by keySort").setString(0,masterId).setString(1, masterId).list();
	}
	
	public List<RelFieldType> getFieldsByEntityType(RelEntityType relEntityType)
	{
		return getFieldsByEntTypeId(relEntityType.getId());
	}
	
	public List<RelFieldType> getFieldsByPath(RelPath relPath)
	{
		return getFieldsByPathId(relPath.getId());
	}
	}
