package rel.explorer.controller.e;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import rel.explorer.service.e.RelResultService;
import rel.explorer.util.Encoding;
import rel.explorer.util.e.JsonResult;

@Scope("prototype")
@Controller(value = "relResultController")
public class RelResultController extends AbstractController {

	private RelResultService resultService;

	private Encoding encoding;

	public Encoding getEncoding() {
		return encoding;
	}

	@Autowired
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	public RelResultService getResultService() {
		return resultService;
	}

	@Autowired
	public void setResultService(RelResultService resultService) {
		this.resultService = resultService;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = null;

		String a = request.getParameter("a");
		String t = request.getParameter("t");
		String user = request.getParameter("user");
		String desc = request.getParameter("desc");
		String s = request.getParameter("s");
		String i = request.getParameter("i");

		a = encoding != null ? encoding.getEnStr(a) : a;
		t = encoding != null ? encoding.getEnStr(t) : t;
		user = encoding != null ? encoding.getEnStr(user) : user;
		desc = encoding != null ? encoding.getEnStr(desc) : desc;
		s = encoding != null ? encoding.getEnStr(s) : s;
		i = encoding != null ? encoding.getEnStr(i) : i;

		try {
			if ("s".equals(a)) {
				resultService.save(t, user, request.getRemoteHost(), desc, s);
				writeString(response,JsonResult.ok());
			} else if ("r".equals(a)) {
				String result = resultService.retrive(i);
				writeString(response,result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mav = new ModelAndView("msg");
			mav.addObject("Message", e.getMessage());
		}

		return mav;
	}
	
	private  void writeString(HttpServletResponse response, String str) throws IOException
	{
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		response.getWriter().write(str);
		response.flushBuffer();				
	}
}
