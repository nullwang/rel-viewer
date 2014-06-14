package rel.explorer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="T_PATH")
public class RelPath implements Serializable{	
	
	private String id;	
	
	private String name;

	private String desc;

	private Integer type;

	private String sourceEntity;

	private String destEntity;
	
	private String linkExp;	
	
	private String linkSourcExp;
	
	private String linkDestExp;
	
	private String dbid;
	
	private String view; 

	private String schema;
	
	private Integer direction;
	
	
	@Id
	@Column(name = "PATH_ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "PATH_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PATH_DESC")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Column(name = "PATH_TYPE")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "PATH_SOURCE_ENTITY")
	public String getSourceEntity() {
		return sourceEntity;
	}

	public void setSourceEntity(String sourceEntity) {
		this.sourceEntity = sourceEntity;
	}
	
	@Column(name = "PATH_DEST_ENTITY")
	public String getDestEntity() {
		return destEntity;
	}

	public void setDestEntity(String destEntity) {
		this.destEntity = destEntity;
	}
	
	@Column(name = "PATH_DBID")
	public String getDbid() {
		return dbid;
	}

	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	@Column(name = "PATH_SCHEMA")
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	@Column(name = "PATH_VIEW")
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}	
	
	@Column(name = "PATH_LINK_SOURCE_EXP")
	public String getLinkSourcExp() {
		return linkSourcExp;
	}

	public void setLinkSourcExp(String linkSourcExp) {
		this.linkSourcExp = linkSourcExp;
	}

	@Column(name = "PATH_LINK_DEST_EXP")
	public String getLinkDestExp() {
		return linkDestExp;
	}

	public void setLinkDestExp(String linkDestExp) {
		this.linkDestExp = linkDestExp;
	}

	@Column(name = "PATH_EXP")
	public String getLinkExp() {
		return linkExp;
	}

	public void setLinkExp(String linkExp) {
		this.linkExp = linkExp;
	}
	
	@Column(name = "PATH_DIRECTION")
	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}
	
	@Transient
	public boolean hasMiddle()
	{
		return direction == null ? false : type == 1;
	}
	
	@Transient
	public boolean isForward()
	{
		return direction == null ? false : direction == 1;
	}
	
	@Transient
	public boolean isReverse()
	{
		return direction ==null ? false : direction == 2;
	}
	
	@Transient
	public boolean isBidi()
	{
		return direction ==null ? false : direction == 3;
	}
}
