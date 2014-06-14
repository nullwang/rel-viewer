package rel.explorer.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;



@Entity
@Table(name="T_ENTITY")
public class RelEntityType implements Serializable {	

	private String id;
	
	private String name;

	private String desc;

	private String dbid;

	private String schema;
	
	private String view; 
	
	private Integer type;
	
	private Integer theme;

	private String iconFile;
	
	private String other;

	private String exp;	
	
	private RelEntityPhoto photo;
	
	@Id
	@Column(name = "ENTITY_ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "ENTITY_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ENTITY_DESC")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "ENTITY_DBID")
	public String getDbid() {
		return dbid;
	}

	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	@Column(name = "ENTITY_SCHEMA")
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Column(name = "ENTITY_TYPE")
	public Integer getType() {
		return type;
	}
	
	@Column(name = "ENTITY_THEME")
	public Integer getTheme() {
		return theme;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public void setTheme(Integer theme)
	{
		this.theme = theme;
	}

	@Column(name = "ENTITY_ICON_FILE")
	public String getIconFile() {
		return iconFile;
	}

	public void setIconFile(String iconFile) {
		this.iconFile = iconFile;
	}

	@Column(name = "ENTITY_OTHER")
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	@Column(name = "ENTITY_EXP")
	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	@Column(name = "ENTITY_VIEW")
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	
	@OneToOne
	@PrimaryKeyJoinColumn
	public RelEntityPhoto getPhoto() {
		return photo;
	}

	public void setPhoto(RelEntityPhoto photo) {
		this.photo = photo;
	}
	
	@Transient
	public boolean isTheme()
	{
		return  theme== null ? false: theme==1;
	}
}
