<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${config==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var urls = {};//构建map
		{
			urls['${_zone}_column'] = '${_acp}/columnConfigForm.shtml';
			urls['${_zone}_query'] = '${_acp}/queryConfigForm.shtml';
			urls['${_zone}_limit'] = '${_acp}/limitConfigForm.shtml';
			urls['${_zone}_ext'] = '${_acp}/extConfig.shtml';
		}

		$('#${_zone}_tabs', $zone).on("tabsactivate", function(event, ui) {
			var $panel = ui.newPanel;
			var hasLoad = $panel.attr("hasLoad");
			var id = $panel.attr("id");
			var url = urls[id];
			if (url != undefined && url != null && hasLoad != 'true') {
				Ajax.post(ui.newPanel, url, {
					data : {
						widgetKey : '${config.widgetKey}'
					}
				});
				$panel.attr("hasLoad", "true");
			}
		});

		//关闭
		$('button[name=close]', $zone).click(function() {
			Ui.confirm('当前操作未保存,确认关闭?', function() {
				Ui.closeCurrent('${_zone}');
			});
		});

		//提交
		$('button[name=submit]', $zone).click(function() {
			Core.fn($zone, 'submit')($zone, $('#${_zone}_form'));
		});
	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submit.shtml" method="post" id="${_zone}_form" option="{errorZone:'${_zone}_error',confirmMsg:'确认提交？'}">
	<input type="hidden" name="isCreate" value="${isCreate?1:0}">
	<table class="ws-table">
		<tr>
			<th>控件主键<font color="red" tip="true" title="做为detail控件的key">(提示)</font></th>
			<td><c:choose>
					<c:when test="${isCreate}">
						<font color="red">(自动生成)</font>
					</c:when>
					<c:otherwise>
						<wcm:widget name="widgetKey" cmd="text" value="${config.widgetKey}" state="readonly" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="busiName" cmd="text{required:true}" value="${config.busiName}"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea" value="${config.description}"></wcm:widget></td>
		</tr>
	</table>

	<div tabs="true" id="${_zone}_tabs">
		<div title="基础设置">
			<div accordion="true" multi="true">
				<div title="数据来源">
					<table class="ws-table">
						<tr>
							<th>主SQL(类型)</th>
							<td><wcm:widget name="mainSqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${config.mainSqlType}" /></td>
						</tr>
						<tr>
							<th>主SQL(脚本)<br /> <font color="red" tip="true" title="注意必须是:select [字段] from [表] where [条件] 的格式.若不需要待选数据,此项返回null.">(提示)</font></th>
							<td><wcm:widget name="mainSqlScript" cmd="codemirror[groovy]{required:true}" value="${config.mainSqlScript}" /></td>
						</tr>
						<tr>
							<th>排序语句<br /> <font color="red" tip="true" title="order by语句之后的信息.例:[ID asc,CREATE_DATE desc]">(提示)</font></th>
							<td><wcm:widget name="orderBy" cmd="textarea" value="${config.orderBy}" /></td>
						</tr>
					</table>
				</div>
				<div title="展示">
					<table class="ws-table">
						<tr>
							<th>弹出框宽度</th>
							<td><wcm:widget name="width" cmd="text{digits:true}" value="${config.width}" /></td>
						</tr>
						<tr>
							<th>自动查询 <font color="red" tip="true" title="打开视图时无需手动点击'查询'即可浏览数据.">(提示)</font></th>
							<td><wcm:widget name="initQuery" cmd="radio[@com.riversoft.platform.translate.InitQueryType]{required:true}" value="${config.initQuery}"></wcm:widget></td>
						</tr>
						<tr>
							<th>每页条数</th>
							<td><wcm:widget name="pageLimit" cmd="text{digits:true}" value="${config.pageLimit}" /></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div id="${_zone}_column" title="数据字段"></div>

		<div title="页面脚本(JS)">
			<table class="ws-table">
				<tr>
					<th>脚本类型</th>
					<td><wcm:widget name="jsType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${config.jsType}" /></td>
				</tr>
				<tr>
					<th>脚本<br /> <font color="red" tip="true" title="_zone:当前页面ID;">(提示)</font></th>
					<td><wcm:widget name="jsScript" cmd="codemirror[javascript]" value="${config.jsScript}" /></td>
				</tr>
			</table>
		</div>

		<div id="${_zone}_limit" title="数据筛选"></div>
		<div id="${_zone}_query" title="查询条件"></div>
		<div id="${_zone}_ext" title="扩展设置"></div>
	</div>

	<div class="ws-bar">
		<div class="ws-group">
			<button type="button" icon="closethick" text="true" name="close">关闭</button>
			<button type="button" icon="check" text="true" name="submit">提交</button>
		</div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>