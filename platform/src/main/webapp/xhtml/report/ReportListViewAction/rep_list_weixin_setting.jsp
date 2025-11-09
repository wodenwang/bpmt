<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="isCopy" value="${param.copy==1}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var initListMode = function(val) {
			var $tabs = $("#${_zone}_weixin_tabs");
			$tabs.tabs("option", "active", 0);
			if (val == '0') {
				$tabs.tabs("option", "disabled", [ 1 ]);
				$tabs.tabs("option", "enable", [ 3 ]);
			} else {
				$tabs.tabs("option", "disabled", [ 3 ]);
				$tabs.tabs("option", "enable", [ 1 ]);
			}
		};

		$(":radio[name='weixin.listMode']", $zone).on('ifChecked', function(event) {
			var val = $(this).val();
			initListMode(val);
		});

		initListMode($(":radio[name='weixin.listMode']:checked", $zone).val());
	});
</script>

<input type="hidden" name="hasWeixin" value="true" />

<table class="ws-table">
	<tr>
		<th>功能点</th>
		<td><wcm:widget name="weixin.pri" cmd="pri{required:true}" value="${vo.pri}" /></td>
	</tr>
	<tr>
		<th>列表页模式</th>
		<td><wcm:widget name="weixin.listMode" cmd="radio[@com.riversoft.module.dyn.WeixinListMode]" value="${vo.listMode}" /></td>
	</tr>
	<tr>
		<th>点击打开模式</th>
		<td><wcm:widget name="weixin.urlMode" cmd="radio[@com.riversoft.module.dyn.WeixinUrlMode]" value="0" state="readonly"/></td>
	</tr>
</table>

<div tabs="true" button="left" id="${_zone}_weixin_tabs">
	<div title="标题">
		<table class="ws-table">
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="weixin.titleType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.titleType}" /></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="weixin.titleScript" cmd="codemirror[groovy]" value="${vo.titleScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="左侧图标">
		<table class="ws-table">
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="weixin.imgType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.imgType}" /></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="vo:实体;返回文件字节串或图片URL.">(提示)</font></th>
				<td><wcm:widget name="weixin.imgScript" cmd="codemirror[groovy]" value="${vo.imgScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="底部说明">
		<table class="ws-table">
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="weixin.desType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.desType}" /></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="weixin.desScript" cmd="codemirror[groovy]" value="${vo.desScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="右侧说明">
		<table class="ws-table">
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="weixin.dateType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.dateType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="weixin.dateScript" cmd="codemirror[groovy]" value="${vo.dateScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>