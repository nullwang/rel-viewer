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
@Table(name="T_FIELD")
public class RelFieldType implements Serializable {	
	
	private String id;	

	private String name;
	
	private String desc;
	
	private Integer dict;
	
	private Integer flag;
	
	private Integer type;
	
	private String masterId;
	
	private String masterView;
	
	private String masterField;
	
	private Integer isMasterKey;
	
	private Integer keySort;
	
	private RelFieldDict fieldDict;
		
	@Id
	@Column(name = "FIELD_ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "FIELD_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "FIELD_DESC")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "FIELD_DICT")
	public Integer getDict() {
		return dict;
	}

	public void setDict(Integer dic) {
		this.dict = dic;
	}

	@Column(name = "FIELD_FLAG")
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Column(name = "FIELD_TYPE")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "MASTER_ID")
	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	@Column(name = "MASTER_VIEW")
	public String getMasterView() {
		return masterView;
	}

	public void setMasterView(String masterView) {
		this.masterView = masterView;
	}

	@Column(name = "MASTER_FIELD")
	public String getMasterField() {
		return masterField;
	}

	public void setMasterField(String masterField) {
		this.masterField = masterField;
	}

	@Column(name = "IS_MASTER_KEY")
	public Integer getIsMasterKey() {
		return isMasterKey;
	}

	public void setIsMasterKey(Integer isMasterKey) {
		this.isMasterKey = isMasterKey;
	}

	@Column(name = "FIELD_EXP")
	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}
	private String exp;

	@Column(name = "FIELD_KEY_SORT")
	public Integer getKeySort() {
		return keySort;
	}

	public void setKeySort(Integer keySort) {
		this.keySort = keySort;
	}

	@Transient
	private boolean isThisFlag( int i)
	{
		if( flag == null ) return false;
		
		int iFlag = flag.intValue();
		
		return  ( iFlag & i) == i;
	}
	
	@Transient
	public boolean isTimeFlag()
	{
		return isThisFlag(1);
	}

	@Transient
	public boolean isGisFlag()
	{
		return isThisFlag(2);
	}

	@Transient
	public boolean isLabelFlag()
	{
		return isThisFlag(4);
	}

	@Transient
	public boolean isTooltipFlag()
	{
		return isThisFlag(8);
	}

	@Transient
	public boolean isCharType()
	{
		return  type== null ? false: type == 0;
	}

	@Transient
	public boolean isNumberType()
	{
		return  type== null ? false: type==1;
	}

	@Transient
	public boolean isTimeType()
	{
		return  type== null ? false: type==3;
	}

	@Transient
	public boolean isMasterKey()
	{
		return  isMasterKey== null ? false: isMasterKey == 1;
	}
	
	@Transient
	public boolean isDictId()
	{
		return  dict == null ? false : dict == 1;		
	}
	
	@Transient
	public boolean isDictName()
	{
		return dict == null ? false: dict == 2;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	public RelFieldDict getFieldDict() {
		return fieldDict;
	}

	public void setFieldDict(RelFieldDict fieldDict) {
		this.fieldDict = fieldDict;
	}
	
}
