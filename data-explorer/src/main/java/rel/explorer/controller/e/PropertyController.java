package rel.explorer.controller.e;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import rel.explorer.e.Entity;
import rel.explorer.service.e.PropertyService;
import rel.explorer.util.Encoding;

@Scope("prototype")
@Controller(value = "propertyController")
public class PropertyController extends AbstractController {
	
	private PropertyService propertyService;
	
	private Encoding encoding;

	public PropertyService getPropertyService() {
		return propertyService;
	}

	@Autowired
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	@Autowired
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		
		String t = request.getParameter("t");
		String i = request.getParameter("i");
		
		i = ( i == null ? "": i); 
		t = (t == null) ? "": t;
		
		t = encoding != null ? encoding.getEnStr(t) : t;
		i = encoding != null ? encoding.getEnStr(i) : i;
		
		try {
			Entity entity = propertyService.loadEntity(t, i);
			mav = new ModelAndView("property");
			mav.addObject("entity",entity);
			
		}catch (Exception e) {
			e.printStackTrace();
			mav = new ModelAndView("msg");
			mav.addObject("Message", e.getMessage());
		}

		return mav;
	}

}
