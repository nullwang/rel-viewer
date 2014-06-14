package rel.explorer.service.e;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rel.explorer.dao.FieldTypeDao;
import rel.explorer.dao.ParameterDao;
import rel.explorer.domain.RelFieldType;
import rel.explorer.e.Entity;
import rel.explorer.e.Property;
import rel.explorer.service.ConfigException;
import rel.explorer.service.RelDataService;
import rel.explorer.util.Common;

@Scope("prototype")
@Service
@Transactional
public class PropertyService {
	
	private RelDataService dataService;
	private ParameterDao parameterDao;
	private FieldTypeDao fieldTypeDao;
		
	public RelDataService getDataService() {
		return dataService;
	}

	@Autowired
	public void setDataService(RelDataService dataService) {
		this.dataService = dataService;
	}

	@Transactional(readOnly = true)
	public Entity loadEntity(String entType, String entId) throws ConfigException
	{	
		List<RelFieldType> relFieldTypes = fieldTypeDao.getFieldsByEntTypeId(entType);
		String idSplit = parameterDao.getEntityIdSplitStr();
		StringBuilder sb = new StringBuilder();
		String[] key = entId.split(idSplit);
		List<String> keyProp = Common.getKeyPropNames(relFieldTypes);
		for (int i = 0; i < keyProp.size() && i < key.length; i++) {
			if (i > 0)
				sb.append(" and ");
			if (Common.isTimeProp(keyProp.get(i), relFieldTypes))
				sb.append("to_char( " + keyProp.get(i)
						+ ", 'yyyy-MM-dd HH:mm:ss')" + " = " + "'" + key[i]
						+ "'");
			else
				sb.append(keyProp.get(i) + " = " + "'" + key[i] + "'");
		}
		
		List<Entity> lst = dataService.loadEntitiesEx(entType, sb.toString(), false, true);
		Entity entity = new Entity();
		if( lst.size() > 0) entity = lst.get(0);
		
		for(int i=0; i<relFieldTypes.size(); i++){
			RelFieldType fieldType = relFieldTypes.get(i);			
			Property property = entity.getProperty(fieldType.getId());
			if( property !=null){
				Property np = new Property();
				np.setName(fieldType.getDesc());
				np.setText(property.getText());
				 entity.getProperties().remove(fieldType.getId());				 
				 entity.addProperty(np);
			}
		}	
		
		return entity;
	}

	public ParameterDao getParameterDao() {
		return parameterDao;
	}

	@Autowired
	public void setParameterDao(ParameterDao parameterDao) {
		this.parameterDao = parameterDao;
	}

	public FieldTypeDao getFieldTypeDao() {
		return fieldTypeDao;
	}

	@Autowired
	public void setFieldTypeDao(FieldTypeDao fieldTypeDao) {
		this.fieldTypeDao = fieldTypeDao;
	}

}
