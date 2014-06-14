package rel.explorer.util.e;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonResult {
	
	private boolean success = true;
	private String desc;	
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static String ok()
	{
		return ok(null);
	}
	
	public static String fail()
	{
		return fail(null);
	}
	
	public static String ok(String desc)
	{
		return t(true,desc);
	}
	
	public static String fail(String desc)
	{
		return t(false,desc);		
	}
	
	private static String t(boolean b, String desc)  {
		JsonResult jr = new JsonResult();
		jr.setSuccess(b);
		jr.setDesc(desc);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(jr);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return "";
    }

}
