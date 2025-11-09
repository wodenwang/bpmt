<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type='text/javascript'>
	$(function() {
		var myPDF = new PDFObject({
			height : '400px',
			url : '${_cp}/widget/FileAction/downloadOffice.shtml?name=${param.name}&type=${param.type}&fileName=${wcm:urlEncode(param.fileName)}&_random=${param.random}'
		}).embed('${_zone}_pdf_zone');
	});
</script>

<div id="${_zone}_pdf_zone" style="border: solid 1px #000;"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>