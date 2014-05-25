package rel.explorer.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rel.explorer.dao.EntityTypeDao;
import rel.explorer.dao.FieldTypeDao;
import rel.explorer.dao.ParameterDao;
import rel.explorer.dao.PathDao;
import rel.explorer.dao.PoolDao;
import rel.explorer.dao.RelDbDao;
import rel.explorer.domain.RelDb;
import rel.explorer.domain.RelEntityPhoto;
import rel.explorer.domain.RelEntityType;
import rel.explorer.domain.RelFieldDict;
import rel.explorer.domain.RelFieldType;
import rel.explorer.domain.RelPath;
import rel.explorer.e.Entity;
import rel.explorer.e.EntityAndLink;
import rel.explorer.e.Formatting;
import rel.explorer.e.ImageURL;
import rel.explorer.e.Link;
import rel.explorer.e.Property;
import rel.explorer.util.Common;

@Scope("prototype")
@Service
@Transactional
public class RelDataService {
	private EntityTypeDao entityTypeDao;
	private FieldTypeDao fieldTypeDao;
	private PathDao pathDao;
	private RelDbDao dbDao;
	private PoolDao poolDao;
	private ParameterDao parameterDao;

	@Transactional(readOnly = true)
	public EntityAndLink loadEntities(String entType, List<String> ids)
			throws ConfigException {
		EntityAndLink entityAndLink = new EntityAndLink();
		StringBuilder sb = new StringBuilder();
		int i = 0;

		for (String id : ids) {
			String condition = id.replace(",", " and ");
			if (i > 0)
				sb.append(" or ");
			sb.append(condition);
			i++;
		}
		if (sb.length() < 1)
			return entityAndLink;

		entityAndLink.addEntities(loadEntitiesEx(entType, sb.toString(), true,
				true));
		return entityAndLink;
	}

	@Transactional(readOnly = true)
	public EntityAndLink expandEntities(String t, String[] paths, String[] ids)
			throws ConfigException {
		EntityAndLink entityAndLink = new EntityAndLink();
		List<RelFieldType> relFieldTypes = fieldTypeDao.getFieldsByEntTypeId(t);
		RelEntityType relEntityType = entityTypeDao.getById(t);
		String idSplit = parameterDao.getEntityIdSplitStr();

		for (String id : ids) {
			StringBuilder sb = new StringBuilder();
			String[] key = id.split(idSplit);
			List<String> keyProp = Common.getKeyPropNames(relFieldTypes);
			for (int i = 0; i < keyProp.size() && i < key.length; i++) {
				if (i > 0)
					sb.append(" and ");
				if (Common.isTimeProp(keyProp.get(i), relFieldTypes))
					sb.append("to_char( " + keyProp.get(i)
							+ ", 'yyyy-MM-dd HH24:mm:ss')" + " = " + "'" + key[i]
							+ "'");
				else
					sb.append(keyProp.get(i) + " = " + "'" + key[i] + "'");
			}

			List<Map<Object, Object>> resultList = loadEntityValues(
					relEntityType, sb.toString(), false, false);
			for (Map<Object, Object> valueMap : resultList) {
				entityAndLink.addEntityAndLink(expandEntity(relEntityType,
						valueMap, paths, relFieldTypes));

			}
		}
		return entityAndLink;
	}

	private EntityAndLink expandEntity(RelEntityType relEntityType,
			Map<Object, Object> valueMap, String[] ids,
			List<RelFieldType> relFieldTypes) throws ConfigException {
		EntityAndLink entityAndLink = new EntityAndLink();

		String sourceEntityTypeId = relEntityType.getId();
		List<RelPath> relPaths = pathDao.getPath(sourceEntityTypeId, ids);

		for (RelPath relPath : relPaths) {
			try {
				entityAndLink.addEntityAndLink(expandEntity(relEntityType,
						valueMap, relPath, relFieldTypes));
			} catch (SQLException e) {
				throw ConfigException.executeSqlError(e.getMessage());
			}
		}
		return entityAndLink;
	}

	private EntityAndLink expandEntity(RelEntityType relEntityType,
			Map<Object, Object> valueMap, RelPath relPath,
			List<RelFieldType> relFieldTypes) throws ConfigException,
			SQLException {
		EntityAndLink entityAndLink = new EntityAndLink();
		String linkIdSplit = parameterDao.getLinkIdSplitStr();
		List<RelFieldType> linkFields = fieldTypeDao.getFieldsByPath(relPath);
		String sourceEntityType = relPath.getSourceEntity();
		String destEntityType = relPath.getDestEntity();
		boolean isForward = (relPath.isForward() || relPath.isBidi())
				&& sourceEntityType.equals(relEntityType.getId());
		boolean isReverse = (relPath.isReverse() || relPath.isBidi())
				&& destEntityType.equals(relEntityType.getId());

		if (!relPath.hasMiddle()) {
			String oppEntityType = "";
			if (isForward) {
				oppEntityType = relPath.getDestEntity();
			} else if (isReverse) {
				oppEntityType = relPath.getSourceEntity();
			} else
				return entityAndLink;

			String linkExp = relPath.getLinkExp();

			// List<String> propNames = Common.getPropNames(relFieldTypes);
			linkExp = Common.translatePropNamesToValue(relFieldTypes, valueMap,
					linkExp);
			Entity entity = resolveEntity(valueMap, relEntityType,
					relFieldTypes, false, true);

			List<Entity> entities = loadEntitiesEx(oppEntityType, linkExp,
					true, true);
			for (int i = 0; i < entities.size(); i++) {
				Entity oppEntity = entities.get(i);
				Link link = new Link();
				link.setCatType(relPath.getId());

				link.setEnt1Id(entity.getId());
				link.setEnt2Id(oppEntity.getId());
				link.setId(entity.getId() + linkIdSplit + oppEntity.getId()
						+ linkIdSplit + String.valueOf(i));
				link.setDirection(Link.Direction.FORWARD);
				if (relPath.isBidi())
					link.setDirection(Link.Direction.BOTH);
				setLinkPropertyValue(link, linkFields, entity, oppEntity,
						relPath);

				entityAndLink.addLink(link);
			}

			entityAndLink.addEntities(entities);

		} else {
			entityAndLink.addEntityAndLink(expandEntityMiddle(relEntityType,
					valueMap, relPath, relFieldTypes));
		}

		return entityAndLink;
	}

	private EntityAndLink expandEntityMiddle(RelEntityType relEntityType,
			Map<Object, Object> valueMap, RelPath relPath,
			List<RelFieldType> relFieldTypes) throws ConfigException,
			SQLException {
		EntityAndLink entityAndLink = new EntityAndLink();
		List<RelFieldType> linkFields = fieldTypeDao.getFieldsByPath(relPath);

		String sourceEntityType = relPath.getSourceEntity();
		String destEntityType = relPath.getDestEntity();
		boolean isForward = (relPath.isForward() || relPath.isBidi())
				&& sourceEntityType.equals(relEntityType.getId());
		boolean isReverse = (relPath.isReverse() || relPath.isBidi())
				&& destEntityType.equals(relEntityType.getId());

		String oppEntityType = "", linkSourceExp = "", linkDestExp = "";
		if (isForward) {
			oppEntityType = relPath.getDestEntity();
			linkSourceExp = relPath.getLinkSourcExp();
			linkDestExp = relPath.getLinkDestExp();
		} else if (isReverse) {
			oppEntityType = relPath.getSourceEntity();
			linkSourceExp = relPath.getLinkDestExp();
			linkDestExp = relPath.getLinkSourcExp();
		} else
			return entityAndLink;

		Entity entity = resolveEntity(valueMap, relEntityType, relFieldTypes,
				false, false);
		int i = 0;
		if (relPath.hasMiddle()) {

			linkSourceExp = Common.translatePropNamesToValue(relFieldTypes,
					valueMap, linkSourceExp);

			List<Map<Object, Object>> linkValueList = loadLinkValues(relPath,
					linkSourceExp);

			for (Map<Object, Object> linkValue : linkValueList) {
				String exp = linkDestExp;

				exp = Common.translatePropNamesToValue(linkFields, linkValue,
						exp);

				List<Entity> oppEntities = loadEntitiesEx(oppEntityType, exp,
						true, true);
				entityAndLink.addEntities(oppEntities);
				entityAndLink.addLinks(resolveLinks(relPath, entity,
						oppEntities, linkValue, linkFields, i));

				i++;
			}
		}

		return entityAndLink;
	}

	private List<Link> resolveLinks(RelPath relPath, Entity sourceEntity,
			List<Entity> destEntities, Map<Object, Object> linkValue,
			List<RelFieldType> linkFields, int index) throws ConfigException,
			SQLException {
		List<Link> links = new ArrayList<Link>();
		String idSplit = parameterDao.getLinkIdSplitStr();
		String linkId = Common.getKey(linkFields, linkValue, idSplit);
		int i = 0;
		for (Entity destEntity : destEntities) {
			Link link = new Link();
			link.setCatType(relPath.getId());

			if (linkId.length() < 1)
				link.setId(sourceEntity.getId() + idSplit + destEntity.getId()
						+ idSplit + String.valueOf(index) + idSplit
						+ String.valueOf(i));
			else
				link.setId(linkId);
			link.setEnt1Id(sourceEntity.getId());
			link.setEnt2Id(destEntity.getId());

			setLinkPropertyValue(link, linkValue, linkFields, true);
			i++;
			links.add(link);
		}
		return links;
	}

	private void setLinkParameterValue(Link link) {
		String color = parameterDao.getFeature(link.getCatType(), "color");
		String dotStyle = parameterDao
				.getFeature(link.getCatType(), "dotstyle");
		String lineThick = parameterDao.getFeature(link.getCatType(),
				"linethickness ");

		int lineThickness = -1;
		try {
			lineThickness = Integer.parseInt(lineThick);
		} catch (NumberFormatException e) {
		}

		link.setColor(color);
		link.setLineThickness(lineThickness);
		link.setDotStyle(dotStyle);
	}

	private void setLinkPropertyValue(Link link, Map<Object, Object> valueMap,
			List<RelFieldType> relFieldTypes, boolean loadFieldDict)
			throws ConfigException, SQLException {

		setLinkParameterValue(link);

		List<String> propNames = Common.getPropNames(relFieldTypes);
		Map<String, String> nameMap = Common.getNameMap(relFieldTypes);

		for (String propName : propNames) {
			String value = getLinkPropValue(nameMap, propName, valueMap);
			Property property = new Property();
			property.setName(propName);
			property.setText(value);
			link.addProperty(property);
		}
		if (loadFieldDict)
			resetLinkFieldDictValue(relFieldTypes, link, valueMap);

	}

	private void setLinkPropertyValue(Link link, List<RelFieldType> linkFields,
			Entity srcEntity, Entity destEntity, RelPath relPath)
			throws ConfigException {

		setLinkParameterValue(link);

		for (RelFieldType fieldType : linkFields) {
			String propName = fieldType.getId();
			String masterView = fieldType.getMasterView();

			if (masterView.equals(relPath.getSourceEntity())) {
				Property property = srcEntity.getProperty(propName);
				if (property == null) {
					throw ConfigException.noPropertyInEntity(propName,
							masterView);
				} else {
					Property linkProperty = (Property) property.clone();
					link.addProperty(linkProperty);
				}
			} else if (masterView.equals(relPath.getDestEntity())) {
				Property property = destEntity.getProperty(propName);
				if (property == null) {
					throw ConfigException.noPropertyInEntity(propName,
							masterView);
				} else {
					Property linkProperty = (Property) property.clone();
					link.addProperty(linkProperty);
				}
			} else {
				ConfigException.e10006(propName, relPath.getId());
			}
		}
	}

	private List<Map<Object, Object>> loadLinkValues(String pathId,
			String propWhere) throws ConfigException {
		RelPath relPath = pathDao.getById(pathId);
		return loadLinkValues(relPath, propWhere);
	}

	private List<Map<Object, Object>> loadLinkValues(RelPath relPath,
			String propWhere) throws ConfigException {
		List<Map<Object, Object>> linkValues = new ArrayList<Map<Object, Object>>();
		try {
			if (relPath == null)
				return linkValues;
			if (!relPath.hasMiddle())
				return linkValues;

			RelDb dbInfo = dbDao.getById(relPath.getDbid());
			String schema = relPath.getSchema();
			String tableName = relPath.getView();

			List<RelFieldType> relFieldTypes = fieldTypeDao
					.getFieldsByPath(relPath);
			Map<String, String> nameMap = Common.getNameMap(relFieldTypes);
			List<String> propNames = Common.getPropNames(relFieldTypes);
			List<String> fieldNames = Common.getFieldNames(relFieldTypes);

			initPoolDao(dbInfo);

			propWhere = Common.translatePropNameToFieldName(nameMap, propNames,
					propWhere);

			linkValues = poolDao.getResult(schema, tableName, fieldNames,
					propWhere);

		} catch (SQLException e) {
			throw ConfigException.executeSqlError(e.getMessage());
		}

		return linkValues;
	}

	private List<Map<Object, Object>> loadEntityValues(String entTypeId,
			String propWhere, boolean loadPic, boolean loadFieldDict)
			throws ConfigException {
		RelEntityType relEntityType = entityTypeDao.getById(entTypeId);
		return loadEntityValues(relEntityType, propWhere, loadPic,
				loadFieldDict);
	}

	private List<Map<Object, Object>> loadEntityValues(
			RelEntityType relEntityType, String propWhere, boolean loadPic,
			boolean loadFieldDict) throws ConfigException {
		List<Map<Object, Object>> resultValues = new ArrayList<Map<Object, Object>>();
		try {

			if (relEntityType == null)
				return resultValues;

			RelDb dbInfo = dbDao.getById(relEntityType.getDbid());
			String schema = relEntityType.getSchema();
			String tableName = relEntityType.getView();

			List<RelFieldType> relFieldTypes = fieldTypeDao
					.getFieldsByEntityType(relEntityType);

			Map<String, String> nameMap = Common.getNameMap(relFieldTypes);
			List<String> propNames = Common.getPropNames(relFieldTypes);
			List<String> fieldNames = Common.getFieldNames(relFieldTypes);

			initPoolDao(dbInfo);

			propWhere = Common.translatePropNameToFieldName(nameMap, propNames,
					propWhere);

			// picture
			RelEntityPhoto entityPhoto = relEntityType.getPhoto();
			if (entityPhoto != null && entityPhoto.isInMaster() && loadPic) {
				String photoField = entityPhoto.getPhotoField();
				photoField = Common.translatePropNameToFieldName(nameMap,
						photoField);
				fieldNames.add(photoField);
			}

			resultValues = poolDao.getResult(schema, tableName, fieldNames,
					propWhere);

		} catch (SQLException e) {
			throw ConfigException.executeSqlError(e.getMessage());
		}

		return resultValues;
	}

	public List<Entity> loadEntitiesEx(String entTypeId, String propWhere,
			boolean loadPic, boolean loadFieldDict) throws ConfigException {
		List<Entity> entities = new ArrayList<Entity>();

		RelEntityType relEntityType = entityTypeDao.getById(entTypeId);

		if (relEntityType == null)
			return entities;

		List<RelFieldType> relFieldTypes = fieldTypeDao
				.getFieldsByEntTypeId(entTypeId);

		List<Map<Object, Object>> resultList = loadEntityValues(relEntityType,
				propWhere, loadPic, loadFieldDict);

		try {
			entities.addAll(resolveEntities(resultList, relEntityType,
					relFieldTypes, loadPic, loadFieldDict));
		} catch (SQLException e) {
			throw ConfigException.executeSqlError(e.getMessage());
		}

		return entities;
	}

	private List<Entity> resolveEntities(List<Map<Object, Object>> list,
			RelEntityType relEntityType, List<RelFieldType> relFieldTypes,
			boolean loadPic, boolean loadFieldDict) throws ConfigException,
			SQLException {
		List<Entity> entities = new ArrayList<Entity>();

		for (Map<Object, Object> valueMap : list) {
			Entity entity = resolveEntity(valueMap, relEntityType,
					relFieldTypes, loadPic, loadFieldDict);
			entities.add(entity);
		}

		return entities;
	}

	private Entity resolveEntity(Map<Object, Object> valueMap,
			RelEntityType relEntityType, List<RelFieldType> relFieldTypes,
			boolean loadPic, boolean loadFieldDict) throws ConfigException,
			SQLException {

		Entity entity = new Entity();

		String idSplitStr = parameterDao.getEntityIdSplitStr();
		List<String> propNames = Common.getPropNames(relFieldTypes);
		Map<String, String> nameMap = Common.getNameMap(relFieldTypes);

		entity.setCatType(relEntityType.getId());

		String key = Common.getKey(relFieldTypes, valueMap, idSplitStr);
		if (key == null || "".equals(key))
			throw ConfigException.entityNoKey(relEntityType.getId());

		entity.setId(key);

		for (String propName : propNames) {
			String value = getEntityPropValue(relEntityType, nameMap, propName,
					valueMap);

			Property property = new Property();
			property.setName(propName);
			property.setText(value);
			entity.addProperty(property);
		}

		if (loadPic)
			entity.setFormatting(getEntityPhoto(relEntityType, relFieldTypes,
					valueMap));
		if (loadFieldDict)
			resetEntityFieldDictValue(relFieldTypes, entity, valueMap);

		return entity;
	}

	private Formatting getEntityPhoto(RelEntityType relEntityType,
			List<RelFieldType> relFieldTypes, Map<Object, Object> valueMap)
			throws ConfigException, SQLException {
		RelEntityPhoto entityPhoto = relEntityType.getPhoto();
		Formatting formatting = null;

		// photo field is not in master table
		if (entityPhoto != null && !entityPhoto.isInMaster()) {
			RelDb dbInfo = dbDao.getById(entityPhoto.getDbid());
			String schema = entityPhoto.getSchema();
			String tableName = entityPhoto.getView();
			String photoField = entityPhoto.getPhotoField();
			String linkExp = entityPhoto.getLinkExp();

			List<String> propNames = Common.getPropNames(relFieldTypes);
			Map<String, String> nameMap = Common.getNameMap(relFieldTypes);
			for (String propName : propNames) {
				String value = Common.getPropValue(nameMap, propName, valueMap);

				linkExp = linkExp.replace(propName, "'" + value + "'");
			}

			initPoolDao(dbInfo);
			int numOfPhoto = parameterDao.getPhotoNum();
			List<Map<Object, Object>> resultList = poolDao.getResult(schema,
					tableName, photoField, linkExp, numOfPhoto);
			formatting = new Formatting();
			for (Map<Object, Object> rMap : resultList) {
				String img = getEntityFieldValue(relEntityType, photoField,
						rMap);
				ImageURL imageURL = new ImageURL();
				imageURL.setImgCode(img);
				formatting.addImg(imageURL);
			}
		}
		return formatting;
	}

	private void resetLinkFieldDictValue(List<RelFieldType> relFieldTypes,
			Link link, Map<Object, Object> valueMap) throws ConfigException,
			SQLException {
		for (RelFieldType relFieldType : relFieldTypes) {
			if (relFieldType.isDictId()) {
				String propName = relFieldType.getId();
				RelFieldDict relFieldDict = relFieldType.getFieldDict();
				if (relFieldDict == null)
					return;

				String dictLinkExp = relFieldDict.getLinkExp();

				dictLinkExp = Common.translatePropNamesToValue(relFieldTypes,
						valueMap, dictLinkExp);

				RelDb dbInfo = dbDao.getById(relFieldDict.getDbid());
				String schema = relFieldDict.getSchema();
				String tableName = relFieldDict.getView();
				String dictValueField = relFieldDict.getField();
				initPoolDao(dbInfo);
				List<Map<Object, Object>> resultList = poolDao.getResult(
						schema, tableName, dictValueField, dictLinkExp, 1);

				if (resultList.size() > 0) {
					Map<Object, Object> rMap = resultList.get(0);
					String v = Common.getFieldValue(dictValueField, rMap);
					Property property = link.getProperty(propName);
					if (property != null)
						property.setText(v);
				}
			}
		}
	}

	private void resetEntityFieldDictValue(List<RelFieldType> relFieldTypes,
			Entity entity, Map<Object, Object> valueMap)
			throws ConfigException, SQLException {
		for (RelFieldType relFieldType : relFieldTypes) {
			if (relFieldType.isDictId()) {
				String propName = relFieldType.getId();
				RelFieldDict relFieldDict = relFieldType.getFieldDict();
				if (relFieldDict == null)
					return;

				String dictLinkExp = relFieldDict.getLinkExp();

				dictLinkExp = Common.translatePropNamesToValue(relFieldTypes,
						valueMap, dictLinkExp);

				RelDb dbInfo = dbDao.getById(relFieldDict.getDbid());
				String schema = relFieldDict.getSchema();
				String tableName = relFieldDict.getView();
				String dictValueField = relFieldDict.getField();
				initPoolDao(dbInfo);
				List<Map<Object, Object>> resultList = poolDao.getResult(
						schema, tableName, dictValueField, dictLinkExp, 1);

				if (resultList.size() > 0) {
					Map<Object, Object> rMap = resultList.get(0);
					String v = Common.getFieldValue(dictValueField, rMap);
					Property property = entity.getProperty(propName);
					if (property != null)
						property.setText(v);
				}
			}
		}
	}

	private String getEntityFieldValue(RelEntityType relEntityType,
			String fieldName, Map<Object, Object> valueMap) {
		boolean isPhoto = false;

		RelEntityPhoto entityPhoto = relEntityType.getPhoto();

		if (entityPhoto != null) {
			if (fieldName.equals(entityPhoto.getPhotoField()))
				if ("bin".equals(parameterDao.getPhotoFormat()))
					isPhoto = true;
		}

		return Common.getFieldValue(fieldName, valueMap, isPhoto);
	}

	private String getEntityPropValue(RelEntityType relEntityType,
			Map<String, String> nameMap, String propName,
			Map<Object, Object> valueMap) {
		String fieldName = nameMap.get(propName);

		return getEntityFieldValue(relEntityType, fieldName, valueMap);
	}

	private String getLinkPropValue(Map<String, String> nameMap,
			String propName, Map<Object, Object> valueMap) {
		String fieldName = nameMap.get(propName);
		return Common.getFieldValue(fieldName, valueMap, false);
	}

	private void initPoolDao(RelDb dbInfo) throws ConfigException {
		try {
			if (dbInfo == null) {
				poolDao.setLocal();
				return;
			}

			String strDbId = dbInfo.getId();
			String driver = dbInfo.getDriver();
			String url = dbInfo.getUrl();
			String name = dbInfo.getUser();
			String password = dbInfo.getPassword();

			poolDao.setDbInfo(strDbId, driver, url, name, password);
		} catch (SQLException e) {
			throw ConfigException.executeSqlError(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw ConfigException.driverClassNoFound(e.getMessage());
		}
	}

	public EntityTypeDao getEntityTypeDao() {
		return entityTypeDao;
	}

	@Autowired
	public void setEntityTypeDao(EntityTypeDao entityTypeDao) {
		this.entityTypeDao = entityTypeDao;
	}

	public FieldTypeDao getFieldTypeDao() {
		return fieldTypeDao;
	}

	@Autowired
	public void setFieldTypeDao(FieldTypeDao fieldTypeDao) {
		this.fieldTypeDao = fieldTypeDao;
	}

	public PathDao getPathDao() {
		return pathDao;
	}

	@Autowired
	public void setPathDao(PathDao pathDao) {
		this.pathDao = pathDao;
	}

	public RelDbDao getDbDao() {
		return dbDao;
	}

	@Autowired
	public void setDbDao(RelDbDao dbDao) {
		this.dbDao = dbDao;
	}

	public PoolDao getPoolDao() {
		return poolDao;
	}

	@Autowired
	public void setPoolDao(PoolDao poolDao) {
		this.poolDao = poolDao;
	}

	public ParameterDao getParameterDao() {
		return parameterDao;
	}

	@Autowired
	public void setParameterDao(ParameterDao parameterDao) {
		this.parameterDao = parameterDao;
	}

}
