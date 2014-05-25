package rel.explorer.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelPath;

@Repository
public class PathDao extends BaseDao{
	public RelPath getById(String id)
	{
		return (RelPath) getSession().get(RelPath.class, id);
	}
	
	public List<RelPath> getPath(String relEntityTypeId )
	{
		return getSession().createQuery(" from RelPath where sourceEntity = ? "
                + " or destEntity = ? ").setString(0, relEntityTypeId).setString(1,relEntityTypeId).list();
	}
	
	public List<RelPath> getPath(String relEntityTypeId, String[] ids)
	{
		if( ids ==null || ids.length < 1 ) return getPath(relEntityTypeId);
		else {
			StringBuilder sb = new StringBuilder();
			
			sb.append(" from RelPath where ( sourceEntity = ?  "
					+ " or destEntity = ?  ) ");
			for( int i=0; i<ids.length; i++)
			{
				if( i==0 ) sb.append(" and id in ( ");
				if( i > 0) sb.append(",");
				sb.append("'" + ids[i] + "'");
				if( i== ids.length -1) sb.append(" )" );
			}
			
			return getSession().createQuery(sb.toString()).setString(0,relEntityTypeId).setString(1,
                    relEntityTypeId).list();
		}	
	}
}
