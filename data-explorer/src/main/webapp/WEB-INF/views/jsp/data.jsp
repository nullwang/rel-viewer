<%@ page language="java" pageEncoding="UTF-8" contentType="text/xml; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<?xml version="1.0" encoding="utf-8"?>
<rel>
	<typeCatalogue>
		<entTypes>
		</entTypes>
		<linkTypes>	
		</linkTypes>		
	</typeCatalogue>
	<content>
			<ents>
			<c:forEach items="${entityAndLink.entities}" var="entitiy">
			<ent catType="${entitiy.value.catType}" id="${entitiy.value.id}" 
			representation="${entitiy.value.representation}" isHidden="${entitiy.value.hidden}">
			<properties>
			<c:forEach items="${entitiy.value.properties}" var="property">
			<${property.value.name}>${property.value.text}</${property.value.name}>
			</c:forEach>
			</properties>
			<c:if test="${entitiy.value.formatting != null}">
			<formatting>
			<c:forEach items="${entitiy.value.formatting.imgs}" var="img">
			<imgURL height="${img.height}" width="${img.width}" URL="${img.URL}">${img.imgCode}</imgURL>
			</c:forEach>
			</formatting>
			</c:if>
			</ent>		
			</c:forEach>
			</ents>
			<links>			
            <c:forEach items="${entityAndLink.links}" var="link">
            <link catType="${link.value.catType}" id="${link.value.id}" ent1id="${link.value.ent1Id}"
           ent2id="${link.value.ent2Id}" isHidden="${link.value.hidden}" lineThickness="${link.value.lineThickness}"
            dotStyle="${link.value.dotStyle}" color="${link.value.color}" >
            <properties>
            <c:forEach items="${link.value.properties}" var="property">
            <${property.value.name}>${property.value.text}</${property.value.name}>
            </c:forEach>
            </properties>
            </link>
            </c:forEach>
			</links>
	</content>
</rel>