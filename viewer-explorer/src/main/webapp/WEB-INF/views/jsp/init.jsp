<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<?xml version="1.0" encoding="utf-8"?>
<rel>
	<typeCatalogue id="${typeCatalogue.id}">
		<entTypes>
		<c:forEach items="${typeCatalogue.entityTypes}" var="entityType">
		  <entType localName="${entityType.value.localName}" 
		        displyName="${entityType.value.displayName}"
                representation="${entityType.value.representation}"
		        LNDateTime="${entityType.value.lndateTime}">	
		        <c:if test="${entityType.value.imageURL != null}">
				<imgURL width="${entityType.value.imageURL.width}" height="${entityType.value.imageURL.height}" 
				URL="${entityType.value.imageURL.URL}"></imgURL>
				</c:if> 
				<c:forEach items="${entityType.value.catProperties}" var="catProperty" >
				<catProperty localName="${catProperty.value.localName}"  
					displayName="${catProperty.value.displayName}"
					isHidden="${catProperty.value.hidden}"
					isLabel="${catProperty.value.label}"
					isToolTip="${catProperty.value.toolTip}"					
					pGUID="${catProperty.value.pguid}"
					fGUID="${catProperty.value.fguid}">
			</catProperty>
			</c:forEach>
			</entType>
			</c:forEach>
		</entTypes>
		<linkTypes>	
		 <c:forEach items="${typeCatalogue.linkTypes}" var="linkType">
          <linkType localName="${linkType.value.localName}" 
                displyName="${linkType.value.displayName}"
                LNDateTime="${linkType.value.lndateTime}"
                showArrows="true">
                <c:forEach items="${linkType.value.catProperties}" var="catProperty" >
                <catProperty localName="${catProperty.value.localName}"  
                    displayName="${catProperty.value.displayName}"
                    isHidden="${catProperty.value.hidden}"
                    isLabel="${catProperty.value.label}"
                    isToolTip="${catProperty.value.toolTip}"                    
                    pGUID="${catProperty.value.pguid}"
                    fGUID="${catProperty.value.fguid}">
            </catProperty>
            </c:forEach>
            </linkType>
            </c:forEach>		  
		</linkTypes>
		<forms>
			<form fGUID="guidFDEED4A3-619E-45DE-AF1A-2A4108EF9593">
				<FormName>Unformed Text</FormName>
				<baseForm>text</baseForm>
			</form>
			<form fGUID="guid4DAD0EE8-5B22-4C72-AEBC-0053D7BBCD06">
				<FormName>Country - Full Name</FormName>
				<baseForm>text</baseForm>
			</form>
			<!-- 日期时间型属性（需要通过该属性进行时序展现的实体）的fGUID必须指向这个GUID， -->
			<form fGUID="TTTTTT">
				<FormName>DateTime</FormName>
				<baseForm>dateTime</baseForm>
				<formatters>
					<formatter syntax=".net">yyyy-MM-dd HH:mm:ss</formatter>
					<formatter syntax=".net">yyyy-MM-dd HH:mm</formatter>
					<formatter syntax=".net">yyyy-MM-dd</formatter>
					<formatter syntax=".net">yyyyMMdd</formatter>
				</formatters>
			</form>
		</forms>
	</typeCatalogue>
	<content>
			<ents>
			</ents>
			<links>
			</links>
	</content>
</rel>