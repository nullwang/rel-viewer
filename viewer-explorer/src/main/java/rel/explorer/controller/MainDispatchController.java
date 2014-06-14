package rel.explorer.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import rel.explorer.e.EntityAndLink;
import rel.explorer.e.TypeCatalogue;
import rel.explorer.service.RelDataService;
import rel.explorer.service.RelMenuService;
import rel.explorer.service.RelTypeService;
import rel.explorer.util.Encoding;

@Scope("prototype")
@Controller(value = "dispatch")
//@SuppressWarnings("unused")
public class MainDispatchController extends MultiActionController {

    private RelTypeService typeService;
    private RelDataService dataService;
    private RelMenuService menuService;
    private Encoding encoding;

    public RelDataService getDataService() {
        return dataService;
    }

    @Autowired
    public void setDataService(RelDataService dataService) {
        this.dataService = dataService;
    }

    public RelTypeService getTypeService() {
        return typeService;
    }

    @Autowired
    public void setTypeService(RelTypeService typeService) {
        this.typeService = typeService;
    }

    public RelMenuService getMenuService() {
        return menuService;
    }

    @Autowired
    public void setMenuService(RelMenuService menuService) {
        this.menuService = menuService;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    @Autowired(required = false)
    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public ModelAndView init(HttpServletRequest request,
                             HttpServletResponse response) {
        TypeCatalogue typeCatalogue;
        ModelAndView mav;
        try {
            typeCatalogue = typeService.getCatalogue();
            mav = new ModelAndView("init", "typeCatalogue", typeCatalogue);

        } catch (Exception e) {
            e.printStackTrace();
            mav = new ModelAndView("msg");
            mav.addObject("Message", e.getMessage());
        }
        return mav;
    }

    public ModelAndView load(HttpServletRequest request,
                             HttpServletResponse response) {
        EntityAndLink entityAndLink = null;
        ModelAndView mav = null;

        String s[] = request.getParameterValues("c");
        String t = request.getParameter("t");
        String entType = (t == null ? "" : t);
        entType = encoding != null ? encoding.getEnStr(entType) : entType;

        List<String> ids = new ArrayList<String>();
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                String es = (encoding != null ? encoding.getEnStr(s[i]) : s[i]);
                ids.add(es);
            }
        }

        try {
            entityAndLink = dataService.loadEntities(entType, ids);
            mav = new ModelAndView("data", "entityAndLink", entityAndLink);

        } catch (Exception e) {
            e.printStackTrace();
            mav = new ModelAndView("msg");
            mav.addObject("Message", e.getMessage());
        }
        return mav;
    }

    public ModelAndView expand(HttpServletRequest request,
                               HttpServletResponse response) {
        EntityAndLink entityAndLink = null;
        ModelAndView mav = null;

        String p = request.getParameter("p");
        String i = request.getParameter("i");
        String t = request.getParameter("t");

        p = (p == null ? "" : p);
        p = p.equals("") ? "," : p;

        p = encoding != null ? encoding.getEnStr(p) : p;
        i = encoding != null ? encoding.getEnStr(i) : i;
        t = encoding != null ? encoding.getEnStr(t) : t;

        try {
            if (i != null && t != null) {
                entityAndLink = dataService.expandEntities(t, p.split(","), i.split(","));
                mav = new ModelAndView("data", "entityAndLink", entityAndLink);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mav = new ModelAndView("msg");
            mav.addObject("Message", e.getMessage());
        }
        return mav;
    }

    public ModelAndView loadMenu(HttpServletRequest request,
                                 HttpServletResponse response) {
        ModelAndView mav = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        try {
            String masterId = request.getParameter("t");
            masterId = encoding != null ? encoding.getEnStr(masterId) : masterId;

            String result = menuService.getMenu(masterId);
            response.getWriter().write(result);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            mav = new ModelAndView("msg");
            mav.addObject("Message", e.getMessage());
        }

        return null;
    }

    public ModelAndView notFound(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("msg");
        mav.addObject("Message", "您访问的页面不存在");
        return mav;
    }

    public ModelAndView errorPage(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("msg");
        mav.addObject("Message", "发生了一个数据载入错误");
        return mav;
    }
}
