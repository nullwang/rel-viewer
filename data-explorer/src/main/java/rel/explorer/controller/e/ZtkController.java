package rel.explorer.controller.e;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import rel.explorer.service.e.ZtkService;
import rel.explorer.util.Encoding;
import rel.explorer.util.e.JsonRecords;

@Scope("prototype")
@Controller(value = "ztkController")
public class ZtkController  extends AbstractController{

	private Encoding encoding;

	public Encoding getEncoding() {
		return encoding;
	}

	@Autowired
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	private ZtkService ztkService;

	public ZtkService getZtkService() {
		return ztkService;
	}

	@Autowired
	public void setZtkService(ZtkService ztkService) {
		this.ztkService = ztkService;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView mav = null;

		String t = request.getParameter("t");
		String rybh = request.getParameter("rybh");
		String zjh = request.getParameter("zjh");
		String xm = request.getParameter("xm");

		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		
		t = encoding != null ? encoding.getEnStr(t) : t;
		rybh = encoding != null ? encoding.getEnStr(rybh) : rybh;
		zjh = encoding != null ? encoding.getEnStr(zjh) : zjh;
		xm = encoding != null ? encoding.getEnStr(xm) : xm;
		
		int iPage = 1;
		int iRows = 0;
		
		try {
			iPage = Integer.parseInt(page);
			iRows = Integer.parseInt(rows);
		}catch(NumberFormatException e){}
		
		
		
		Map map = null;
		try {
			if ("r".equals(t)) {
				map = ztkService.getRkxx(rybh, zjh, xm, iPage,
						iRows);
			}
			
			writeData(response,map);

		} catch (Exception e) {
			e.printStackTrace();
			mav = new ModelAndView("msg");
			mav.addObject("Message", e.getMessage());
		}

		return mav;
	}
	
	private  void writeData(HttpServletResponse response, Map map) throws IOException
	{
		if( map == null )return;
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		
		String str = JsonRecords.JsonStringFrom(map);
		
		response.getWriter().write(str);
		response.flushBuffer();	
	}
}
