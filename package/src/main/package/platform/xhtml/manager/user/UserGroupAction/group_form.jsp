<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//表单动作
		$('form', $zone).submit(function() {
			var $form = $(this);
			Ajax.form('${param.errorZone}', $form, {
				confirmMsg : '是否保存?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
						Ajax.post($zone, '${_acp}/editGroupZone.shtml', {
							data : {
								groupKey : $('[name=groupKey]', $zone).val()
							}
						});
					}
				}
			});
			return false;
		});

		$.each($('input[name=roleKey]', $('#${_zone}_disabled_role')), function() {
			var roleKey = $(this).val();
			$('option[value=' + roleKey + ']', $('select[name=roles]', $zone)).attr("disabled", "disabled");
		});
		$('select[name=roles]', $('#${_zone}')).trigger('liszt:updated');
	});
</script>

<%-- 不允许选择的数据 --%>
<div style="display: none;" id="${_zone}_disabled_role">
	<c:forEach items="${sysList}" var="roleKey">
		<input name="roleKey" value="${roleKey}" />
	</c:forEach>
</div>

<div tabs="true">
	<c:choose>
		<c:when test="${isCreate}">
			<c:set var="title" value="添加组织" />
		</c:when>
		<c:otherwise>
			<c:set var="title" value="组织[${vo.busiName}]编辑" />
		</c:otherwise>
	</c:choose>
	<div title="${title}">

		<%--表单 --%>
		<form action="${_acp}/submitGroupForm.shtml" method="post" sync="true">
			<input type="hidden" name="isCreate" value="${isCreate?1:0}" />
			<div accordion="true" multi="true">
				<div title="基础信息">
					<table class="ws-table">
						<tr>
							<th>组织KEY</th>
							<td><wcm:widget name="groupKey" cmd="key[CM_GROUP]{required:true}" value="${vo.groupKey}" state="${isCreate?'normal':'readonly'}" /></td>
						</tr>
						<tr>
							<th>展示名</th>
							<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
						</tr>
					</table>
				</div>
				<div title="角色分配">
					<table class="ws-table">
						<tr>
							<th>分配角色<br /> <font color="red" tip="true" title="此处仅将已有角色关联到组织,若需新增角色请切换到[角色管理]界面添加.">(提示)</font></th>
							<td><wcm:widget name="roles" cmd="multiselect[$com.riversoft.platform.po.UsRole;roleKey;busiName;1=1 order by sort asc;false]" value="${roles}">不支持命令</wcm:widget></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="ws-bar">
				<button type="submit" icon="disk">保存</button>
			</div>
		</form>
	</div>
</div>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>