<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var urls = {};//构建map
		{
			urls['${_zone}_column'] = '${_acp}/columnConfigForm.shtml';
			urls['${_zone}_query'] = '${_acp}/queryConfigForm.shtml';
			urls['${_zone}_limit'] = '${_acp}/limitConfigForm.shtml';
			urls['${_zone}_js'] = '${_acp}/jsConfigForm.shtml';
			urls['${_zone}_btn'] = '${_acp}/btnConfigForm.shtml';
			urls['${_zone}_subs'] = '${_acp}/subsConfigForm.shtml';
			urls['${_zone}_vars'] = '${_acp}/varConfigForm.shtml';
			urls['${_zone}_frame'] = '${_acp}/frameSetting.shtml';
			urls['${_zone}_weixin'] = '${_acp}/weixinSetting.shtml';
		}

		$('#${_zone}_tabs', $zone).on("tabsactivate", function(event, ui) {
			$('textarea', ui.newPanel).blur();
			var current = $("#${_zone}_tabs", $zone).tabs("option", "active");
			if (current == 0) {
				//do nothing
			} else if (current == 9) {
				$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 1, 2, 3, 4, 5, 6, 7, 8 ]);
			} else {
				$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 9 ]);
			}

			var $panel = ui.newPanel;
			var hasLoad = $panel.attr("hasLoad");
			var id = $panel.attr("id");
			var url = urls[id];
			if (url != undefined && url != null && hasLoad != 'true') {
				Ajax.post(ui.newPanel, url, {
					data : {
						key : '${config.viewKey}'
					}
				});
				$panel.attr("hasLoad", "true");
			}
		});

		//新建时
		if ('${config!=null?1:0}' == '0') {
			$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 9 ]);
		}
	});
</script>

<div tabs="true" id="${_zone}_tabs">
	<div title="基本配置">
		<table class="ws-table">
			<tr>
				<th>展示名</th>
				<td><wcm:widget name="config.busiName" cmd="text{required:true}" value="${config!=null?config.busiName:''}"></wcm:widget></td>
			</tr>
			<tr>
				<th>主SQL(类型)</th>
				<td><wcm:widget name="config.mainSqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${config.mainSqlType}" /></td>
			</tr>
			<tr>
				<th>主SQL(脚本)<br /> <font color="red" tip="true" title="注意必须是:select [字段] from [表] where [条件] 的格式.">(提示)</font></th>
				<td><wcm:widget name="config.mainSqlScript" cmd="codemirror[groovy]{required:true}" value="${config.mainSqlScript}" /></td>
			</tr>
			<tr>
				<th>排序语句<br /> <font color="red" tip="true" title="order by语句之后的信息.例:[ID asc,CREATE_DATE desc]">(提示)</font></th>
				<td><wcm:widget name="config.orderBy" cmd="textarea" value="${config.orderBy}" /></td>
			</tr>
			<tr>
				<th>可选数据源<br /> <font color="red" tip="true" title="留空表示使用默认数据源;">(提示)</font></th>
				<td><wcm:widget name="config.dbKey" cmd="text" value="${config.dbKey}" /></td>
			</tr>
		</table>
		<div accordion="true" multi="true">
			<div title="展示">
				<table class="ws-table">
					<tr>
						<th>展示分列数量</th>
						<td><wcm:widget name="config.col" cmd="text{required:true,digits:true,max:5,min:1}" value="${config!=null?config.col:2}"></wcm:widget></td>
					</tr>
					<tr>
						<th>自动查询 <font color="red" tip="true" title="打开视图时无需手动点击'查询'即可浏览数据.">(提示)</font></th>
						<td><wcm:widget name="config.initQuery" cmd="radio[@com.riversoft.platform.translate.InitQueryType]{required:true}" value="${config!=null?config.initQuery:1}"></wcm:widget></td>
					</tr>
					<tr>
						<th>列表页汇集</th>
						<td><wcm:widget name="config.summaryFlag" cmd="radio[YES_NO]" value="${config!=null?config.summaryFlag:'0'}" /></td>
					</tr>
					<tr>
						<th>是否分页</th>
						<td><wcm:widget name="config.pageFlag" cmd="radio[YES_NO]" value="${config!=null?config.pageFlag:1}" /></td>
					</tr>
					<tr>
						<th>每页条数</th>
						<td><wcm:widget name="config.pageLimit" cmd="text{digits:true}" value="${config!=null?config.pageLimit:''}" /></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div title="视图字段" id="${_zone}_column"></div>
	<div title="按钮设置" id="${_zone}_btn"></div>
	<div title="页面脚本(JS)" id="${_zone}_js"></div>
	<div title="数据筛选" id="${_zone}_limit"></div>
	<div title="查询条件" id="${_zone}_query"></div>
	<div title="展示变量" id="${_zone}_vars"></div>
	<div title="子表设置" id="${_zone}_subs"></div>
	<div title="微信设置" id="${_zone}_weixin"></div>
	<div title="界面排版" id="${_zone}_frame"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>