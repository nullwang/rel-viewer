package rel.explorer.service.e;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import rel.explorer.dao.e.ResultDao;
import rel.explorer.domain.RelResult;

@Scope("prototype")
@Service
@Transactional
public class RelResultService {
	
	private ResultDao resultDao;

	public ResultDao getResultDao() {
		return resultDao;
	}
	
	@Autowired
	public void setResultDao(ResultDao resultDao) {
		this.resultDao = resultDao;
	}
	
	@Transactional(readOnly = true)
	public String retrive(String id)
	{
		RelResult relResult = resultDao.getById(id);
		if(relResult != null ){
			byte[] b= Base64.decode(relResult.getContent());
			return new String(b);
		}
		return null;
	}
	
	@Transactional
	public void save(String title, String user, 
			String host, String desc, String s )
	{
		RelResult relResult = new RelResult();
		relResult.setId(UUID.randomUUID().toString());
		relResult.setTitle(title);
		relResult.setUser(user);
		relResult.setHost(host);
		relResult.setDesc(desc);
		
		if( s!=null)
		s = Base64.encode(s.getBytes());
		
		relResult.setContent(s);
		resultDao.save(relResult);
	}
}
