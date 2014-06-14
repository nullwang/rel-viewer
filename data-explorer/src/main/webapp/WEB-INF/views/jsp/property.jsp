<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<table width=100% height=30 cellpadding=0 cellspacing=0>
    <tr>
        <td bgcolor='#f0f0f0' align=left>&nbsp;&nbsp;属性列表</td>
        <td bgcolor='#f0f0f0' align=right>
        <input type=button value='&nbsp;&nbsp;关闭&nbsp;&nbsp;' onclick='window.close()'/>&nbsp;&nbsp;
        </td>
    </tr>
</table>
<table width=100% border=1 cellSpacing=0 cellPadding=2>
<tr>
<td height='20' bgcolor='#f0f0f0' align=center><font size=2 face='Arial' color='#000000'><b>
属性</b></font></td><td height='20' bgcolor='#f0f0f0' align=center><font size=2 face='Arial' color='#000000'><b>
值</b></font></td>
</tr>
<c:forEach items="${entity.properties}" var="property">
<tr bgcolor="#F3EFEF" >
<td bgcolor="#F3EFEF" width="200" height="20">
<font size=2 face='Arial'>&nbsp;${property.value.name}</font>
</td>
<td bgcolor="#F3EFEF" width="300" >
<font size=2 face='Arial'>&nbsp;${property.value.text}</font>
</td>
</tr>
</c:forEach>
</table>
</body>
</html>