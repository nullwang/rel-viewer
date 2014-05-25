package rel.explorer.service;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rel.explorer.dao.MenuDao;
import rel.explorer.domain.RelMenu;

@Scope("prototype")
@Service
@Transactional
public class RelMenuService {

    private MenuDao menuDao;

    @Transactional(readOnly = true)
    public String getMenu(String masterId) throws IOException {
        List<RelMenu> menus = menuDao.getByMasterId(masterId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(menus);
    }

    public MenuDao getMenuDao() {
        return menuDao;
    }

    @Autowired
    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

}
