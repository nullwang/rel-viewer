package rel.explorer.domain.e;

import java.util.ArrayList;
import java.util.List;

public class Rkxx implements PropertyList{
	private String id;
	
	private String sex;
	
	private String no;
	
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List getProperties() {
		List lst = new ArrayList();
		lst.add(id);
		lst.add(name);
		lst.add(no);
		return lst;
	}


}