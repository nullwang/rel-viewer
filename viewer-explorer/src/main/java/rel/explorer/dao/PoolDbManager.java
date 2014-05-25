package rel.explorer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PoolDbManager {
	Log log = LogFactory.getLog(this.getClass());
	
	private static PoolDbManager dbcpm = null;
    private Connection con = null;
    private List<String> dbList = new ArrayList<String>();
      
    private Connection regDb(String aliasName, String driverClass, String strUrl, Properties info) throws SQLException, ClassNotFoundException {
    	Connection conn = null;
    	info.setProperty("proxool.maximum-connection-count", "40");
    	info.setProperty("proxool.alias", aliasName);
    	   Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
    	   //Class.forName(driverClass);
    	   //ProxoolFacade.registerConnectionPool(strUrl);
    	   String url = "proxool." + aliasName + ":" + driverClass + ":" + strUrl;
    	   
    	   conn = DriverManager.getConnection(url, info);
           log.debug("db driver " + aliasName +  " has been registed ! ");
      
       return conn;
    }
    
    static synchronized public PoolDbManager getInstance(){
       if(null==dbcpm)
            dbcpm =new PoolDbManager();
        return dbcpm;
      }
    
    public Connection getConnection(String strDbID,String driverClass, String strUrl, String userName,
    		String password) throws SQLException, ClassNotFoundException{
    	Properties info = new Properties();
    	
    	info.setProperty("user", userName);
		info.setProperty("password", password);
		
		return getConnection(strDbID,driverClass, strUrl, info);
    }
    
    public Connection getConnection(String strDbID,String driverClass, String strUrl, Properties info) throws SQLException, ClassNotFoundException{
    	log.info(strDbID+"-"+strUrl);
      
        	if(dbList.indexOf(strDbID)<0){        		
				con = regDb(strDbID, driverClass, strUrl, info);				
        		dbList.add(strDbID);
        	}else{
        	  con = DriverManager.getConnection("proxool."+strDbID);
        	}
        	
          return con;
      }    
}
