package rel.explorer.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import rel.explorer.domain.RelFieldType;
import rel.explorer.service.ConfigException;

public class Common {

	public static String getKey(List<RelFieldType> relFieldTypes,
			Map<Object, Object> valueMap, String idSplit) {
		List<String> keys = Common.getKeyPropNames(relFieldTypes);
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			String value = Common.getPropValue(relFieldTypes, key, valueMap);
			if (i > 0)
				sb.append(idSplit);
			sb.append(value);
			i++;
		}
		return sb.toString();
	}

	public static List<String> getPropNames(List<RelFieldType> relFieldTypes) {
		List<String> names = new ArrayList<String>();
		for (RelFieldType relFieldType : relFieldTypes) {
			names.add(relFieldType.getId());
		}
		return names;
	}

	public static List<String> getFieldNames(List<RelFieldType> relFieldTypes) {
		List<String> names = new ArrayList<String>();
		for (RelFieldType relFieldType : relFieldTypes) {
			names.add(relFieldType.getMasterField());
		}
		return names;
	}

	// advanced feature, recursively retrieve field name
	public static String getFieldName(List<RelFieldType> relFieldTypes,
			RelFieldType relFieldType) {
		return "";
	}

	public static Map<String, String> getNameMap(
			List<RelFieldType> relFieldTypes) {
		Map<String, String> map = new LinkedHashMap<String, String>();

		for (RelFieldType relFieldType : relFieldTypes) {
			map.put(relFieldType.getId(), relFieldType.getMasterField());
		}

		return map;
	}

	public static boolean isTimeProp(String propName,
			List<RelFieldType> relFieldTypes) {
		for (RelFieldType relFieldType : relFieldTypes) {
			if (relFieldType.isTimeType()
					&& propName.equals(relFieldType.getId()))
				return true;
		}
		return false;
	}

	public static List<String> getKeyPropNames(List<RelFieldType> relFieldTypes) {
		List<String> names = new ArrayList<String>();
		for (RelFieldType relFieldType : relFieldTypes) {
			if (relFieldType.isMasterKey())
				names.add(relFieldType.getId());
		}
		return names;
	}

	public static boolean isKeyProp(List<RelFieldType> relFieldTypes,
			String propName) {
		for (RelFieldType relFieldType : relFieldTypes) {
			if (relFieldType.isMasterKey())
				if (propName.equals(relFieldType.getId()))
					return true;
		}
		return false;
	}

	public static String translatePropNamesToValue(
			List<RelFieldType> relFieldTypes, Map<Object, Object> valueMap,
			String s) {
		Map<String, String> nameMap = Common.getNameMap(relFieldTypes);
		List<String> propNames = Common.getPropNames(relFieldTypes);
		return translatePropNamesToValue(nameMap, valueMap, propNames, s);
	}

	public static String translatePropNamesToValue(Map<String, String> nameMap,
			Map<Object, Object> valueMap, List<String> propNames, String s) {
		String ret = s;
		for (String propName : propNames) {
			ret = translatePropNameToValue(nameMap, valueMap, propName, ret);
		}
		return ret;
	}

	public static String translatePropNameToValue(Map<String, String> nameMap,
			Map<Object, Object> valueMap, String propName, String s) {
		String value = getPropValue(nameMap, propName, valueMap);
		return s.replace(propName, "'" + value + "'");
	}

	public static String translateFieldNameToValue(
			Map<Object, Object> valueMap, String fieldName, String s) {
		String value = getFieldValue(fieldName, valueMap);
		return s.replace(fieldName, value);
	}

	public static String translatePropNameToFieldName(
			Map<String, String> nameMap, List<String> propNames, String s)
			throws ConfigException {
		String result = s;
		for (String propName : propNames) {
			result = translatePropNameToFieldName(nameMap, propName, result);
		}
		return result;
	}

	public static String translatePropNameToFieldName(
			Map<String, String> nameMap, String propName, String s)
			throws ConfigException {
		if (nameMap.get(propName) == null)
			throw ConfigException.propNoField(propName);
		return s.replace(propName, nameMap.get(propName));
	}

	/**
	 * translate the t_field prop name to the table field name
	 * 
	 * @param nameMap
	 * @param propName
	 * @return
	 */
	public static String translatePropNameToFieldName(
			Map<String, String> nameMap, String propName) {
		String name = nameMap.get(propName);
		if (name == null)
			return propName;
		else
			return name;
	}

	public static List<String> translatePropNamesToFieldNames(
			Map<String, String> nameMap, List<String> propNames) {
		List<String> list = new ArrayList<String>();
		for (String propName : propNames) {
			list.add(Common.translatePropNameToFieldName(nameMap, propName));
		}
		return list;
	}

	public static Map<String, String> translatePropConditions(
			Map<String, String> nameMap, Map<String, String> conditions) {
		Map<String, String> retMap = new LinkedHashMap<String, String>();
		for (String propName : conditions.keySet()) {
			retMap.put(nameMap.get(propName), conditions.get(propName));
		}
		return retMap;
	}

	public static String getPropValue(List<RelFieldType> relFieldTypes,
			String propName, Map<Object, Object> valueMap) {
		Map<String, String> nameMap = Common.getNameMap(relFieldTypes);
		return getPropValue(nameMap, propName, valueMap);
	}

	public static String getPropValue(Map<String, String> nameMap,
			String propName, Map<Object, Object> valueMap) {
		String fieldName = nameMap.get(propName);
		return getFieldValue(fieldName, valueMap);
	}

	public static String getFieldValue(String fieldName,
			Map<Object, Object> valueMap) {
		return getFieldValue(fieldName, valueMap, false);
	}

	public static String getFieldValue(String fieldName,
			Map<Object, Object> valueMap, boolean isPhoto) {
		Object obj = valueMap.get(fieldName);
		if (obj == null)
			return "";

		if (obj instanceof Date) {
			Date d = (Date) obj;
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
		}

		if (obj instanceof byte[]) {
			byte[] b = (byte[]) obj;
			return Base64.encode(b);
		}

		// if (obj instanceof byte[]) {
		// byte[] b = (byte[]) obj;
		//
		// if (isPhoto) {
		// return Base64.encode(b);
		// } else {
		// return new String(b);
		// }
		// }

		return obj.toString();

	}

}
