package rel.explorer.service.e;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import rel.explorer.dao.e.Page;
import rel.explorer.dao.e.ZtkDao;

@Scope("prototype")
@Service
public class ZtkService {
	ZtkDao ztkDao;

	public ZtkDao getZtkDao() {
		return ztkDao;
	}

	@Autowired
	public void setZtkDao(ZtkDao ztkDao) {
		this.ztkDao = ztkDao;
	}
	
	private String likeValueOf(String str)
	{
		String s =  ( str == null ? "": str );
		return "'"  + s + "%'"; 
	}
	
	public Map getRkxx(String rybh,  String zjh, String name, int currentPage, int rows	)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(" RYBH like " + likeValueOf(rybh));
		sb.append(" and ZJH like " + likeValueOf(zjh));
		sb.append(" and XM like " + likeValueOf(name));
		
		Page page = new Page();
		
		page.setCurrentPage(currentPage);
		page.setNumPerPage(rows);
		
		List lst = ztkDao.getRkxx(sb.toString(), page);
		
		Map map = new HashMap();
		map.put("page", page);
		map.put("data", lst);
		
		return map;
	}
	
	
}
