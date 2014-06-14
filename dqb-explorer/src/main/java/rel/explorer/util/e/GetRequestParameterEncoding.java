package rel.explorer.util.e;
import java.io.UnsupportedEncodingException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rel.explorer.util.Encoding;

@Scope("prototype")
@Component
public class GetRequestParameterEncoding implements Encoding {
	private String sourceCode = "iso8859_1";
	private String destCode = "UTF-8";
	private boolean isNeed=true;

	public boolean isNeed() {
		return isNeed;
	}
	
	public void setNeed(boolean isNeed) {
		this.isNeed = isNeed;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getDestCode() {
		return destCode;
	}

	public void setDestCode(String destCode) {
		this.destCode = destCode;
	}

	public String getEnStr(String s) {
		
		if(!this.isNeed){
			return s;
		}
		if (s==null) return s;
		try {
			String ret = new String(s.getBytes(sourceCode),destCode);
			return ret;
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	
	
}
