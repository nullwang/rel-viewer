package rel.explorer.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="T_RESULT")
public class RelResult implements Serializable{

	private String id;
	
	private String title;
	
	private String desc;
	
	private String content;
	
	private String host;
	
	private String user;
	
	private Date createTime;

	@Id
	@Column(name = "RESULT_ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name="RESULT_TITLE")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="RESULT_DESC")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name="RESULT_CONTENT")
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	@Column(name="FROM_HOST")
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	@Column(name="FROM_USER")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Column(name="CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
