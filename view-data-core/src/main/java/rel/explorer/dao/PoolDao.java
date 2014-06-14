package rel.explorer.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Scope("prototype")
@Repository
public class PoolDao {
	Log log = LogFactory.getLog(this.getClass());

	private PoolDbManager dbc = null;
	private Connection con = null;
	private PreparedStatement ps = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	private BasicDataSource ds;	
	
	public BasicDataSource getDs() {
		return ds;
	}

	@Resource(name="dataSource")
	public void setDs(BasicDataSource ds) {
		this.ds = ds;
	}

	public PoolDao() {
		dbc = PoolDbManager.getInstance();
	}
	
	public void setLocal() throws SQLException, ClassNotFoundException
	{
		if( ds != null ){
			setDbInfo("_dataSource",ds.getDriverClassName(),ds.getUrl(),ds.getUsername(),ds.getPassword());		
		}
		else {
			log.error("error set local connection datasource is null");
		}
	}

	public void setDbInfo(String strDbID, String driverClass, String strUrl, String name,
			String password) throws SQLException, ClassNotFoundException {
		con = dbc.getConnection(strDbID, driverClass,strUrl, name, password);
	}
	

	private PreparedStatement prepareStatement(String strSql)
			throws SQLException {
		if (!(con == null)) {
			//log.info(strSql);
			ps = con.prepareStatement(strSql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			return ps;
		} else {
			return null;
		}
	}

	private Statement createStatement() throws SQLException {
		if (!(con == null)) {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			return stmt;
		} else {
			return null;
		}
	}

	public ResultSet resultSet(String strsql) throws SQLException {
		try {
			rs = createStatement().executeQuery(strsql);
		} finally {
			closecon();
		}
		return rs;
	}

	private void closecon() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
		}
	}
	
	public List<Map<Object,Object>> getResult(String schema,String tableName, 
			String fieldName, String where)throws SQLException
	{
		return getResult(schema, tableName, fieldName, where, Integer.MAX_VALUE);
	}
	
	public List<Map<Object,Object>> getResult(String schema,String tableName, 
			String fieldName, String where, int total)throws SQLException
	{
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
		return getResult(schema, tableName, fieldNames, where, Integer.MAX_VALUE);
	}
	
	public List<Map<Object,Object>> getResult(String schema, String tableName,
			List<String> fieldNames, String where) throws SQLException {
		
		return getResult(schema,tableName, fieldNames, where, Integer.MAX_VALUE);
	}

	public List<Map<Object,Object>> getResult(String schema, String tableName,
			List<String> fieldNames, String where, int total) throws SQLException {
		List<Map<Object,Object>> lst = new ArrayList<Map<Object,Object>>();
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		StringBuilder sbPropName = new StringBuilder();
		for (int i = 0; i < fieldNames.size(); i++) {
			String fieldName = fieldNames.get(i);
			if ( i > 0 )sbPropName.append(",");
			sbPropName.append(fieldName );
		}
		if(sbPropName.length()<1) 
			{
				log.error("execute sql has no field in:" + schema + "." + tableName );
				return lst;
			}
		sb.append(sbPropName);
		
		sb.append(" from ");

		if (schema != null)
			sb.append(schema + ".");

		sb.append(tableName + " " + tableName);

		if( where != null && where.length() > 0) 
			sb.append( " where ");
		
		sb.append(where);
		int count = 0;
		try {
			prepareStatement(sb.toString());
			if (ps != null) {
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = null;
				if( rs != null){
					rsmd = rs.getMetaData();
				}
				while (rs != null && rsmd !=null && rs.next()) {
					if(count >= total) break;
					count ++;
					Map<Object,Object> m = new HashMap<Object,Object>();
					for (int j = 1; j <= fieldNames.size(); j++) {
						switch (rsmd.getColumnType(j)) {
						case Types.NULL:
							m.put(fieldNames.get(j-1), null);
							break;
						case Types.CHAR:
						case Types.VARCHAR:
							m.put(fieldNames.get(j-1), rs.getString(j));
							break;
						case Types.NUMERIC:
						case Types.DECIMAL:
							m.put(fieldNames.get(j-1), rs.getBigDecimal(j));
							break;
						case Types.INTEGER:
							m.put(fieldNames.get(j-1), rs.getInt(j));
							break;
						case Types.SMALLINT:
							m.put(fieldNames.get(j-1), rs.getShort(j));
							break;
						case Types.BIGINT:
							m.put(fieldNames.get(j-1), rs.getLong(j));
							break;
						case Types.REAL:
							m.put(fieldNames.get(j-1),rs.getFloat(j));
							break;
						case Types.FLOAT:
						case Types.DOUBLE:
							m.put(fieldNames.get(j-1), rs.getDouble(j));
							break;
						case Types.DATE:
							m.put(fieldNames.get(j-1), rs.getTimestamp(j));
							break;
						case Types.TIME:
							m.put(fieldNames.get(j-1), rs.getTimestamp(j));
							break;
						case Types.TIMESTAMP:
							m.put(fieldNames.get(j-1), rs.getTimestamp(j));
							break;
						case Types.LONGVARCHAR:	
							StringBuilder sbBuffer = new StringBuilder();
							Reader rd = rs.getCharacterStream(j);
							char[] buffer = new char[1024];
							try {
								int len = 0;
								while((len = rd.read(buffer) )> 0)
								{
									sbBuffer.append(buffer, 0, len);
								}
								rd.close();
							} catch (IOException e) {
								log.error(" read long raw error");
								e.printStackTrace();
							}
							m.put(fieldNames.get(j-1),sbBuffer.toString());
							break;
						case Types.BLOB:
							Blob blob = rs.getBlob(j);
							byte[] bytes = blob.getBytes(1, (int) blob.length());
							m.put(fieldNames.get(j-1), bytes);
						break;
						case Types.CLOB:
							Clob clob  = rs.getClob(j);
							rd = clob.getCharacterStream();
							buffer = new char[1024];
							sbBuffer = new StringBuilder();
							try {
								int len = 0;
								while((len = rd.read(buffer) )> 0)
								{
									sbBuffer.append(buffer, 0, len);
								}
								rd.close();
							} catch (IOException e) {
								log.error(" read clob error");
								e.printStackTrace();
							}
							m.put(fieldNames.get(j-1),sbBuffer.toString());
							break;
						case Types.BINARY:
						case Types.VARBINARY:
						case Types.LONGVARBINARY:
							m.put(fieldNames.get(j-1), rs.getBytes(j));
							break;							
						default:
						}
					}
					lst.add(m);
				}
			}
			
		} 
		catch( SQLException e) 
		{			
			throw new SQLException ( e.getMessage() + " execute sql " + sb.toString() ) ;
		}finally {
			log.debug("execute sql :" + sb.toString() );
			closecon();
		}
		return lst;

	}

	public  List<Map<Object,Object>> getResult(String schema, String tableName,
			List<String> fieldNames, Map<String, String> conditions)
			throws SQLException {
		
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for (String condition : conditions.keySet()) {			
			if (i > 0)
				sb.append(" and ");
			sb.append(condition + " = '" + conditions.get(condition) + "'");
			i++;
		}

		return this.getResult(schema, tableName, fieldNames, sb.toString());
	}
	
	public  List<Map<Object,Object>> getResult(String schema, String tableName,
			String fieldName, Map<String, String> conditions)
			throws SQLException {
		
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
		
		return getResult(schema,tableName,fieldNames,conditions);		
	}
	
	public  List<Map<Object,Object>> getAndResult(String schema,String tableName, List<String> fieldNames, List<String> conditions)throws SQLException 
	{
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for (String condition : conditions) {		
			if (i > 0)
				sb.append(" and ");
			sb.append(condition );
			i++;
		}

		return this.getResult(schema, tableName, fieldNames, sb.toString());
	}
	
	public  List<Map<Object,Object>> getAndResult(String schema,String tableName, String fieldName, List<String> conditions)throws SQLException 
	{
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
		return getAndResult(schema,tableName,fieldNames,conditions);		
	}
	
	public  List<Map<Object,Object>> getOrResult(String schema,String tableName, List<String> fieldNames, List<String> conditions)throws SQLException 
	{
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for (String condition : conditions) {
			if (i > 0)
				sb.append(" or ");
			sb.append(condition );
			i++;
		}

		return this.getResult(schema, tableName, fieldNames, sb.toString());
	}
	
	public  List<Map<Object,Object>> getOrResult(String schema,String tableName, String fieldName, List<String> conditions)throws SQLException 
	{
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
		return getOrResult(schema,tableName,fieldNames,conditions);		
	}
}
