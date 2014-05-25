package rel.explorer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rel.explorer.dao.EntityTypeDao;
import rel.explorer.dao.FieldTypeDao;
import rel.explorer.dao.PathDao;
import rel.explorer.domain.RelEntityType;
import rel.explorer.domain.RelFieldType;
import rel.explorer.domain.RelPath;
import rel.explorer.e.CatProperty;
import rel.explorer.e.EntityType;
import rel.explorer.e.ImageURL;
import rel.explorer.e.LinkType;
import rel.explorer.e.TypeCatalogue;

@Scope("prototype")
@Service
@Transactional
public class RelTypeService {
	private EntityTypeDao entityTypeDao;
	private FieldTypeDao fieldTypeDao;
	private PathDao pathDao;

	@Transactional(readOnly = true)
	public TypeCatalogue getCatalogue() throws ConfigException {
		TypeCatalogue typeCatalogue = new TypeCatalogue();
		typeCatalogue.setId("1");

		boolean hasKey = false;
		for (RelEntityType relEntType : entityTypeDao.getAll()) {
			EntityType entityType = new EntityType();
			entityType.setLocalName(relEntType.getId());
			entityType.setDisplayName(relEntType.getDesc());
			if( relEntType.isTheme())
			{
				entityType.setRepresentation("theme");
			}
			
			List<RelFieldType> fields = fieldTypeDao
					.getFieldsByEntityType(relEntType);

			for (RelFieldType field : fields) {
				CatProperty catProperty = new CatProperty();
				catProperty.setLocalName(field.getId());
				catProperty.setDisplayName(field.getDesc());

				catProperty.setLabel(field.isLabelFlag());
				catProperty.setToolTip(field.isTooltipFlag());

				if (field.isTimeFlag()) {
					entityType.setLndateTime(catProperty.getLocalName());
				}

				if (field.isTimeType()) {
					catProperty.setFguid("TTTTTT");
				}

				String iconFile = relEntType.getIconFile();
				if (iconFile != null) {
					ImageURL imageUrl = new ImageURL();
					imageUrl.setWidth(32);
					imageUrl.setHeight(32);
					imageUrl.setURL(iconFile);
					entityType.setImageURL(imageUrl);
				}

				entityType.addCatProperty(catProperty);
				if (field.isMasterKey())
					hasKey = true;
			}

			typeCatalogue.addEntityType(entityType);
			if (!hasKey)
				throw ConfigException.entityNoKey(relEntType.getId());
		}

		for (RelPath relPath : pathDao.getAll()) {
			LinkType linkType = new LinkType();
			linkType.setLocalName(relPath.getId());
			linkType.setDisplayName(relPath.getDesc());

			List<RelFieldType> fields = fieldTypeDao.getFieldsByPath(relPath);

			for (RelFieldType field : fields) {
				CatProperty catProperty = new CatProperty();
				catProperty.setLocalName(field.getId());
				catProperty.setDisplayName(field.getDesc());

				catProperty.setLabel(field.isLabelFlag());
				catProperty.setToolTip(field.isTooltipFlag());

				if (field.isTimeFlag()) {
					linkType.setLndateTime(catProperty.getLocalName());
				}

				if (field.isTimeType()) {
					catProperty.setFguid("TTTTTT");
				}

				linkType.addCatProperty(catProperty);
			}

			typeCatalogue.addLinkType(linkType);
		}
		return typeCatalogue;
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
}
