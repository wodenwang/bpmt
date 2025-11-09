<%@ page language="java" pageEncoding="UTF-8"%>
<c:if test="${(_head==null && param._head != 'false') || (_head!=false && param._head!='false')}">
	<!doctype html>
	<html class="no-js">
	<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="renderer" content="webkit">
	<meta http-equiv="Cache-Control" content="no-siteapp" />
	<meta name="format-detection" content="telephone=no" />
	<c:if test="${_ico!=null&&_icon!=''}">
		<link rel="shortcut icon" href="${_ico}" type="image/x-icon" />
	</c:if>
	
	<title>${wpf:lan(_title)}</title>
	
	<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>

	<c:choose>
		<c:when test="${_h5_js=='amaze'||param._h5_js=='amaze'}">
			<link rel="stylesheet" href="http://cdn.amazeui.org/amazeui/2.6.2/css/amazeui.min.css" type="text/css">
			<script type="text/javascript" src="http://cdn.amazeui.org/amazeui/2.6.2/js/amazeui.min.js"></script>
		</c:when>
		<c:when test="${_h5_js!='none'&&param._h5_js=='none'}">
			<link rel="stylesheet" href="http://cdn.bootcss.com/weui/0.4.3/style/weui.min.css">
			<link rel="stylesheet" href="http://cdn.bootcss.com/jquery-weui/0.8.0/css/jquery-weui.min.css">
			<script src="http://cdn.bootcss.com/jquery-weui/0.8.0/js/jquery-weui.min.js"></script>
		</c:when>
	</c:choose>

	<script src="${_cp}/js/jquery.form.min.js"></script>
	<script src="${_cp}/js/ws-widget.js"></script>
	<script src="${_cp}/js/ws-wxui.js"></script>
	
	<style type="text/css">
	* {
		margin: 0;
		padding: 0;
		border: 0;
	}
	</style>
	</head>
	<body>
</c:if>