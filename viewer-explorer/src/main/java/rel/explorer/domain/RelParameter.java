package rel.explorer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="T_PARAM")
public class RelParameter implements Serializable  {
	
	private String id;

	private String value;
	
	@Id
	@Column(name = "PARAM_ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "PARAM_VALUE")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}
