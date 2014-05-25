package rel.explorer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="T_FIELD_DICT")
public class RelFieldDict implements Serializable{

	private String id;
	
	private String dbid;
	
	private String schema;
	
	private String view; 
	
	private String linkExp;
	
	private String field;

	@Id
	@Column(name = "FIELD_ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "DICT_DBID")
	public String getDbid() {
		return dbid;
	}

	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	@Column(name = "DICT_SCHEMA")
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Column(name = "DICT_VIEW")
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	@Column(name = "DICT_LINK_EXP")
	public String getLinkExp() {
		return linkExp;
	}

	public void setLinkExp(String linkExp) {
		this.linkExp = linkExp;
	}

	@Column(name = "DICT_FIELD")
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}	
}
