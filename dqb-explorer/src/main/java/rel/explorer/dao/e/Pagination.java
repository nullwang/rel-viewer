package rel.explorer.dao.e;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class Pagination extends JdbcDaoSupport {
	Log log = LogFactory.getLog(getClass());
	
	Page page = new Page();
	
	private List resultList;

	// JdbcTemplate jTemplate
	private JdbcTemplate jTemplate;

	public Pagination() {

	}

	public Pagination(String sql) {
		if (jTemplate == null) {
			throw new IllegalArgumentException(
					"jTemplate is null,please initial it first. ");
		} else if (sql.equals("")) {
			throw new IllegalArgumentException(
					"sql is empty,please initial it first. ");
		}
		new Pagination(sql, page, jTemplate, null);
	}

	public Pagination(String sql, Page page,
			JdbcTemplate jTemplate, RowMapper rowMapper) {
		if (jTemplate == null) {
			throw new IllegalArgumentException(
					"jTemplate is null,please initial it first. ");
		} else if (sql == null || sql.equals("")) {
			throw new IllegalArgumentException(
					" Pagination empty,please initial it first. ");
		}
		
		StringBuffer totalSQL = new StringBuffer(" SELECT count(*) FROM ( ");
		totalSQL.append(sql);
		totalSQL.append(" ) totalTable ");
		
		setJdbcTemplate(jTemplate);
		
		int totalRows = (getJdbcTemplate().queryForInt(totalSQL.toString()));
		page.setTotalRows(totalRows);
		
		int startIndex = page.getStartIndex();
		int lastIndex = page.getLastIndex();
		
		//System.out.println("lastIndex=" + lastIndex);// ////////////////

		StringBuffer paginationSQL = new StringBuffer(" SELECT * FROM ( ");
		paginationSQL.append(" SELECT temp.* ,ROWNUM num FROM ( ");
		paginationSQL.append(sql);
		paginationSQL.append(" ) temp where ROWNUM <= " + lastIndex);
		paginationSQL.append(" ) WHERE num > " + startIndex);
		log.info(paginationSQL.toString());
		//System.out.println("sql:" + paginationSQL.toString());
		if( rowMapper == null) 
			setResultList(getJdbcTemplate().queryForList(paginationSQL.toString()));
		else {
			setResultList(getJdbcTemplate().query(paginationSQL.toString(),
				rowMapper));
		}
	}

	public JdbcTemplate getJTemplate() {
		return jTemplate;
	}

	public void setJTemplate(JdbcTemplate template) {
		jTemplate = template;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
