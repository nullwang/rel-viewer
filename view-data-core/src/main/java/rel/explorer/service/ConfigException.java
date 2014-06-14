package rel.explorer.service;

public class ConfigException extends Exception {

	public ConfigException() {
		super();
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}
	
	public static ConfigException entityNoKey(String e)
	{
		ConfigException ce = new ConfigException("REL-10003 T_ENTITY no key : " + e);
				
		return ce;		
	}
	
	public static ConfigException propNoField(String e)
	{
		ConfigException ce = new ConfigException("REL-10004 property has no field : " + e);
		
		return ce;
	}
	
	public static ConfigException driverClassNoFound(String e)
	{
		return new ConfigException("REL-10001 no found driver class  : " + e);
	}
	public static ConfigException noDbConfig()
	{
		return new ConfigException("REL-10002 no db config: ");
	}
	
	public static ConfigException noPropertyInEntity(String propName, String entity)
	{
		return new ConfigException("REL-10005 no this property " + propName + " in entity : " + entity);
	}
	
	public static ConfigException executeSqlError(String e)
	{
		return new ConfigException("REL-11001 execute sql error : " + e);
	}
	
	public static ConfigException e10006(String propName, String linkName)
	{	
		return new ConfigException("REL-10006 link property must exist in association table, when no middle table");
	}
	
}
