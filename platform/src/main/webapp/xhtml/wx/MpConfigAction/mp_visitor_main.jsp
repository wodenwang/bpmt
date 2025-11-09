<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=sync]', $zone).click(function() {
			Ui.confirmPassword('确认同步公众号用户库?此操作可能需要花费较长时间.', function() {
				Ajax.post('${_zone}_msg_zone', '${_acp}/syncVisitor.shtml', {
					loading : true,
					data : {
						mpKey : '${param.mpKey}'
					},
					callback : function(flag) {
						if (flag) {
							Ajax.post($zone, '${_acp}/visitorMain.shtml', {
								data : {
									mpKey : '${param.mpKey}'
								}
							});
						}
					}
				});
			});
		});

		$('#${_zone}_list_form').submit();
	});
</script>

<form id="${_zone}_list_form" zone="${_zone}_list_zone" action="${_acp}/listVisitor.shtml" method="get">
	<input type="hidden" name="mpKey" value="${param.mpKey}" />
	<table class="ws-table" col="2">
		<tr>
			<th>OPEN_ID</th>
			<td><wcm:widget name="_sl_OPEN_ID" cmd="text" /></td>
		</tr>
		<tr>
			<th>UNION_ID</th>
			<td><wcm:widget name="_sl_UNION_ID" cmd="text" /></td>
		</tr>
		<tr>
			<th>标签</th>
			<td><select class="chosen" name="tags" multiple="multiple">
					<c:forEach items="${tags}" var="o">
						<option value="${o.TAG_ID}">${o.TAG_NAME}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<th>昵称</th>
			<td><wcm:widget name="_sl_NICK_NAME" cmd="text" /></td>
		</tr>
		<tr>
			<th>性别</th>
			<td><wcm:widget name="_ne_SEX" cmd="select[SEX(请选择)]" /></td>
		</tr>
		<tr>
			<th>城市</th>
			<td><wcm:widget name="_sl_CITY" cmd="text" /></td>
		</tr>
		<tr>
			<th>备注</th>
			<td><wcm:widget name="_sl_REMARK" cmd="text" /></td>
		</tr>
		<tr>
			<th>是否关注</th>
			<td><wcm:widget name="_ne_SUBSCRIBE" cmd="select[YES_NO(请选择)]" /></td>
		</tr>
		<tr>
			<th>关注时间(>=)</th>
			<td><wcm:widget name="_dnl_SUBSCRIBE_TIME" cmd="date" /></td>
		</tr>
		<tr>
			<th>关注时间(<=)</th>
			<td><wcm:widget name="_dnm_SUBSCRIBE_TIME" cmd="date" /></td>
		</tr>
		<tr whole="true">
			<th class="ws-bar">
				<div class="ws-group left">
					<button type="button" icon="transferthick-e-w" text="true" name="sync">全量同步</button>
				</div>
				<div class="ws-group right">
					<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
					<button type="submit" icon="search" text="true">查询</button>
				</div>
			</th>
		</tr>
	</table>
</form>

<div id="${_zone}_msg_zone"></div>

<div id="${_zone}_list_zone"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>