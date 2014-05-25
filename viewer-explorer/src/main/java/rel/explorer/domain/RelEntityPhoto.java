package rel.explorer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="T_ENTITY_PHOTO")
public class RelEntityPhoto implements Serializable {

	
	private String entityId;

	private String photoField;
	
	private Integer sourceType;

	private String dbid;

	private String schema;

	private String view; 

	private String linkExp;
	
	@Id
	@Column(name = "ENTITY_ID")
	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Column(name = "ENTITY_PHOTO_FIELD")
	public String getPhotoField() {
		return photoField;
	}

	public void setPhotoField(String photoField) {
		this.photoField = photoField;
	}

	@Column(name = "PHOTO_SOURCE_TYPE")
	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "PHOTO_DBID")
	public String getDbid() {
		return dbid;
	}

	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	@Column(name = "PHOTO_SCHEMA")
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Column(name = "PHOTO_VIEW")
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	@Column(name = "PHOTO_LINK_EXP")
	public String getLinkExp() {
		return linkExp;
	}

	public void setLinkExp(String linkExp) {
		this.linkExp = linkExp;
	}
	
	@Transient
	public boolean isInMaster()
	{
		return  sourceType== null ? false: sourceType == 0;
	}
	
}
