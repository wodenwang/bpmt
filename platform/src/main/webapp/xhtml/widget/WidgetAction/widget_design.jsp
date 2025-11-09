<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=ok]', $zone).click(function() {
			var val = $(this).val();
			Core.fn($zone, 'callback')(val);
		});

		$('#${_zone}_tabs', $zone).on("tabsactivate", function(event, ui) {
			var $panel = ui.newPanel;
			if ($('form', $panel).size() > 0) {
				$('form', $panel).submit();
			}
		});

		Core.fn('${_zone}_vwwidget_list', 'callback', function(val) {
			Core.fn($zone, 'callback')(val);
		});
		Core.fn('${_zone}_select_zone', 'callback', function(val) {
			Core.fn($zone, 'callback')(val);
		});
		Core.fn('${_zone}_db_list', 'callback', function(val) {
			Core.fn($zone, 'callback')(val);
		});
	});
</script>

<div tabs="true" id="${_zone}_tabs">
	<div title="普通输入框">
		<table class="ws-table">
			<tr>
				<th style="width: 2em;"></th>
				<th>控件</th>
				<th>示例</th>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="text">确认</button></td>
				<td>普通文本框</td>
				<td><wcm:widget name="test" cmd="text"></wcm:widget></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="textarea">确认</button></td>
				<td>多行文本框</td>
				<td><wcm:widget name="test" cmd="textarea"></wcm:widget></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="ueditor">确认</button></td>
				<td>富文本编辑框</td>
				<td><wcm:widget name="test" cmd="ueditor"></wcm:widget></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value=filemanager>确认</button></td>
				<td>文件选择框</td>
				<td><wcm:widget name="test" cmd="filemanager"></wcm:widget></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="multifilemanager">确认</button></td>
				<td>文件选择框(多选)</td>
				<td><wcm:widget name="test" cmd="multifilemanager"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="日期时间控件">
		<table class="ws-table">
			<tr>
				<th style="width: 2em;"></th>
				<th>控件</th>
				<th>示例</th>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="date">确认</button></td>
				<td>日期选择框</td>
				<td><wcm:widget name="test" cmd="date" /></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="date[time]">确认</button></td>
				<td>时间选择框</td>
				<td><wcm:widget name="test" cmd="date[time]" /></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="date[datetime]">确认</button></td>
				<td>日期/时间选择框</td>
				<td><wcm:widget name="test" cmd="date[datetime]" /></td>
			</tr>
			<tr>
				<td class="center"><button icon="check" type="button" name="ok" text="false" value="date[yearmonth]">确认</button></td>
				<td>年月选择框</td>
				<td><wcm:widget name="test" cmd="date[yearmonth]" /></td>
			</tr>
		</table>
	</div>
	<div title="组织架构控件">
		<table class="ws-table">
			<tr>
				<th style="width: 4em;">选择</th>
				<th>控件</th>
				<th>描述</th>
			</tr>
			<tr>
				<td class="center ws-group"><button type="button" name="ok" value="group">单选</button>
					<button type="button" name="ok" value="multigroup">多选</button></td>
				<td>组织选择</td>
				<td>选定系统内一个组织</td>
			</tr>
			<tr>
				<td class="center ws-group"><button type="button" name="ok" value="user">单选</button>
					<button type="button" name="ok" value="multiuser">多选</button></td>
				<td>用户选择</td>
				<td>选定一个系统用户</td>
			</tr>
		</table>
	</div>
	<div title="下拉数据框(数据字典)">
		<form zone="${_zone}_db_list" action="${_acp}/list.shtml" id="${_zone}_db_list_form" method="get">
			<table class="ws-table">
				<tr>
					<th>类型主键</th>
					<td><wcm:widget name="_sl_dataType" cmd="text">不支持命令</wcm:widget></td>
					<th>分类</th>
					<td><wcm:widget name="_sl_catelog" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>展示名</th>
					<td><wcm:widget name="_sl_busiName" cmd="text">不支持命令</wcm:widget></td>
					<th>描述</th>
					<td><wcm:widget name="_sl_description" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class=" right">
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<%--查询结果 --%>
		<div id="${_zone}_db_list"></div>
	</div>

	<div title="下拉数据框(动态表)" init="${_acp}/dynTable.shtml" id="${_zone}_select_zone"></div>
	<div title="自定义控件">
		<form zone="${_zone}_vwwidget_list" action="${_acp}/vwWidgetList.shtml" id="${_zone}_vwwidget_list_form" method="get">
			<table class="ws-table">
				<tr>
					<th>调用KEY(模糊)</th>
					<td><wcm:widget name="_se_widgetKey" cmd="text">不支持命令</wcm:widget></td>
					<th>调用KEY(精确)</th>
					<td><wcm:widget name="_sl_widgetKey" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>展示名(模糊)</th>
					<td><wcm:widget name="_sl_busiName" cmd="text">不支持命令</wcm:widget></td>
					<th>描述(模糊)</th>
					<td><wcm:widget name="_sl_description" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class=" right">
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<%--查询结果 --%>
		<div id="${_zone}_vwwidget_list"></div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>