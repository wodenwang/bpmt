<%@ page language="java" pageEncoding="UTF-8"%>
<c:if test="${(_head==null && param._head != 'false') || (_head!=false && param._head!='false')}">
	<c:if test="${_zone == null || _zone == '' || _zone == '_body'}">
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<title>${wpf:lan(_title)}</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta name="renderer" content="webkit">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache">
		<meta http-equiv="Expires" content="0">
		<link rel="shortcut icon" href="${_ico}" type="image/x-icon" />
		<script type="text/javascript">
			document
					.write('<div id="loading" style="-moz-border-radius-bottomright: 5px;-webkit-border-bottom-right-radius: 5px;border-bottom-right-radius: 5px;font-size: 12px;z-index: 999999; padding: 5px 0 5px 9px; background: gray; left: 0; top: 0; width: 100px; color: #fff; position: fixed;">页面加载中...<\/div>');
		</script>
		<%@ include file="/include/html_include.jsp"%>
		</head>
		<body>
	</c:if>
	<%@ include file="/include/html_init.jsp"%>
</c:if>