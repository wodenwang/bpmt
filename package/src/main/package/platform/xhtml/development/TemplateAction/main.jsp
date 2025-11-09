<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//提交快照
		Core.fn('${_zone}_dev_zone', 'submitDev', function($form) {
			Ajax.form('${_zone}_dev_msg', $form, {
				confirmMsg : '快照生成需要一个较久过程,需要继续生成吗?',
				loading : true,
				callback : function(flag) {
					if (flag) {
						Ajax.post('${_zone}_dev_zone', '${_acp}/dev.shtml');
						$('#${_zone}_list_form').submit();
					}
				}
			});
		});

	});
</script>

<div tabs="true">
	<div title="快照开发">
		<div id="${_zone}_dev_msg"></div>
		<div init="${_acp}/dev.shtml" id="${_zone}_dev_zone"></div>
	</div>
	<div title="所有快照">
		<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
			<input type="hidden" name="_field" value="createDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>名称(模糊)</th>
					<td><wcm:widget name="_sl_name" cmd="text">不支持命令</wcm:widget></td>
					<th>描述(模糊)</th>
					<td><wcm:widget name="_sl_description" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>平台版本(模糊)</th>
					<td><wcm:widget name="_sl_platformVersion" cmd="text">不支持命令</wcm:widget></td>
					<th>开发者UID(模糊)</th>
					<td><wcm:widget name="_sl_createUid" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<div id="${_zone}_list">
			<div class="ws-msg info">请按查询.</div>
		</div>

	</div>
	<div title="开发日志查询">
		<form zone="${_zone}_opr_list" action="${_acp}/oprList.shtml" query="true" id="${_zone}_opr_list_form" method="get">
			<input type="hidden" name="_field" value="createDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">

				<tr>
					<th>执行类(模糊)</th>
					<td><wcm:widget name="_sl_oprClass" cmd="text">不支持命令</wcm:widget></td>
					<th>执行方法(模糊)</th>
					<td><wcm:widget name="_sl_oprMethod" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>操作备注(模糊)</th>
					<td><wcm:widget name="_sl_oprMemo" cmd="text">不支持命令</wcm:widget></td>
					<th>开发者UID(模糊)</th>
					<td><wcm:widget name="_sl_createUid" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>视图版本(>=)</th>
					<td><wcm:widget name="_nbe_version" cmd="text{integer:true}">不支持命令</wcm:widget></td>
					<th>视图版本(<=)</th>
					<td><wcm:widget name="_nse_version" cmd="text{integer:true}">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>执行时间(>=)</th>
					<td><wcm:widget name="_dnl_createDate" cmd="date[datetime]">不支持命令</wcm:widget></td>
					<th>执行时间(<=)</th>
					<td><wcm:widget name="_dnm_createDate" cmd="date[datetime]">不支持命令</wcm:widget></td>
				</tr>

				<tr>
					<th class="ws-bar ">
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>
		<div id="${_zone}_opr_list">
			<div class="ws-msg info">请按查询.</div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>