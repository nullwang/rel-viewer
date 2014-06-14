package rel.explorer.dao.e;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobHandler;

import rel.explorer.domain.e.Rkxx;

public class ZtkDao extends JdbcDaoSupport{

	Log log = LogFactory.getLog(getClass());
	
	private LobHandler lobHandler;

	public LobHandler getLobHandler() {
		return lobHandler;
	}

	@Autowired
	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}
	
	public List getRkxx(String whereSql,Page page)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT RYBH,XM,ZJH,XBDM FROM T10_RKXX ");
		if( whereSql != null && ! "".equals(whereSql))
		{
			sb.append(" where ");
			sb.append(whereSql);
		}
		
		JdbcTemplate daoTmplt = this.getJdbcTemplate();
		Pagination pagination = new Pagination(sb.toString(), page,
				daoTmplt, new RkxxRowMapper());
		return pagination.getResultList();
	}	
}

class RkxxRowMapper implements RowMapper {

	  public Object mapRow(ResultSet resultSet, int row) throws SQLException {
	    Rkxx rkxx = new Rkxx();
	    rkxx.setId(resultSet.getString("RYBH"));
	    rkxx.setName(resultSet.getString("XM"));
	    rkxx.setNo(resultSet.getString("ZJH"));
	    rkxx.setSex(resultSet.getString("XBDM"));
	    return rkxx;
	  }
}
