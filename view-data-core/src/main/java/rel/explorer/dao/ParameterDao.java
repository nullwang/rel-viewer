package rel.explorer.dao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import rel.explorer.domain.RelParameter;

@Repository
public class ParameterDao extends BaseDao {

	public static final String ENTITY_ID_SPLIT = "ENTITY_ID_SPLIT";
	public static final String PHOTO_FORMAT = "PHOTO_FORMAT";
	public static final String PHOTO_NUM = "PHOTO_NUM";
	public static final String LINK_ID_SPLIT = "LINK_ID_SPLIT";

	public Map<String, RelParameter> cacheMap = new HashMap<String, RelParameter>();
	public Map<String, Map<String,String>> featureMaps = new HashMap<String,Map<String,String>>();
	
	public synchronized RelParameter getById(String id) {
		if (cacheMap.get(id) != null)
			return cacheMap.get(id);

		RelParameter relParameter = (RelParameter) getSession().get(RelParameter.class, id);
		if(relParameter != null){
            cacheMap.put(id, relParameter);
        }
        return relParameter;
	}

	synchronized Map<String, String>  getFeatures(String id) {
		if( featureMaps.get(id) != null && ! featureMaps.get(id).isEmpty()) 
		{
			return featureMaps.get(id);
		}
		
		Map<String, String> featureMap = new HashMap<String, String>();
		RelParameter relParameter = getById(id);
		if (relParameter != null) {
			String pairs = relParameter.getValue();
			if (pairs != null && !"".equals(pairs)) {
				String[] ps = pairs.split(" ");
				for (int i = 0; i < ps.length; i++) {
					int index = ps[i].indexOf(":");
					String name = ps[i].substring(0, index);
					String value = ps[i].substring(index+1, ps[i].length());
					if (name != null && value != null && !"".equals(name)
							&& !"".equals(value)) {
						featureMap.put(name, value);
					}
				}
			}
		}
		featureMaps.put(id, featureMap);
		
		return featureMap;
	}
	
	public  String  getFeature(String id, String name)
	{
		return getFeatures(id).get(name);
	}

	public  String getEntityIdSplitStr() {
		RelParameter relParameter = getById(ENTITY_ID_SPLIT);
		return relParameter == null ? "_" : relParameter.getValue();
	}

	public  String getLinkIdSplitStr() {
		RelParameter relParameter = getById(LINK_ID_SPLIT);
		return relParameter == null ? "_" : relParameter.getValue();
	}

	public  String getPhotoFormat() {
		RelParameter relParameter = getById(PHOTO_FORMAT);
		return relParameter == null ? "base64" : relParameter.getValue();
	}

	public  int getPhotoNum() {
		RelParameter relParameter = getById(PHOTO_NUM);
		if (relParameter == null)
			return 0;

		try {
			return Integer.parseInt(relParameter.getValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
