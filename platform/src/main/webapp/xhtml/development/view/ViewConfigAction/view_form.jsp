<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('select[name=viewClass]', $zone).on("change", function() {
			var value = this.value;
			if (value != null && value != '') {
				Ajax.post('${_zone}_config', '${_acp}/configZone.shtml?viewClass=' + value);
				Ajax.json('${_acp}/loginTypeGetter.shtml', function(result) {
					var $loginTypeZone = $("#${_zone}_login_type_zone");
					$(':radio', $loginTypeZone).iCheck('disable');
					if (result) {
						$.each(result.types, function(i, o) {
							$(":radio[value='" + o + "']", $loginTypeZone).iCheck('enable'); //可用
						});
					}
					if ($(':radio:checked', $loginTypeZone).prop("disabled")) {
						$(':radio:first', $loginTypeZone).iCheck('check');
					}
				}, {
					data : {
						viewClass : value
					}
				});

			} else {
				$('#${_zone}_config').html('');
				$('#${_zone}_copy_config').html('');
			}
		});
	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submit.shtml" method="post" id="${_zone}_form" option="{errorZone:'${_zone}_error',confirmMsg:'确认提交？'}">
	<c:if test="${!isCreate}">
		<input type="hidden" name="viewKey" value="${vo.viewKey}">
	</c:if>

	<table class="ws-table">
		<c:if test="${!isCreate}">
			<tr>
				<th>视图主键(自动生成)</th>
				<td><font color="red">${vo.viewKey}</font></td>
			</tr>
		</c:if>
		<tr>
			<th>绑定模块</th>
			<td><c:choose>
					<c:when test="${isCreate}">
						<select class="chosen {required:true}" name="viewClass">
							<option value="">请选择</option>
							<c:forEach items="${moduleGroups}" var="group">
								<optgroup label="${group.key}">
									<c:forEach items="${group.value}" var="module">
										<option value="${module.name}">${module.description}</option>
									</c:forEach>
								</optgroup>
							</c:forEach>
						</select>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="viewClass" value="${vo.viewClass}" />
						<span style="color: red; font-weight: bold;">${module.description}</span>
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<th>需要登录</th>
			<td><c:choose>
					<c:when test="${isCreate}">
						<div id="${_zone}_login_type_zone">
							<wcm:widget name="loginType" cmd="radio[YES_NO]" />
						</div>
					</c:when>
					<c:otherwise>
						<div id="${_zone}_login_type_zone">
							<script type="text/javascript">
								$(function() {
									var $loginTypeZone = $("#${_zone}_login_type_zone");
									var types = "${loginTypes}".split(";");
									$(':radio', $loginTypeZone).iCheck('disable');
									$.each(types, function(i, o) {
										$(":radio[value='" + o + "']", $loginTypeZone).iCheck('enable'); //可用
									});
								});
							</script>
							<wcm:widget name="loginType" cmd="radio[YES_NO]" value="${vo.loginType}" />
						</div>
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea{required:true}" value="${!isCreate?vo.description:''}"></wcm:widget></td>
		</tr>

	</table>

	<c:choose>
		<c:when test="${isCreate}">
			<div id="${_zone}_config"></div>
		</c:when>
		<c:otherwise>
			<div id="${_zone}_config" init="${_cp}${action}?key=${vo.viewKey}"></div>
		</c:otherwise>
	</c:choose>

	<div class="ws-bar">
		<div class=" ws-group left">
			<button type="button" icon="closethick" text="true" onclick="Ui.closeTab('${_zone}')">关闭</button>
		</div>
		<div class=" ws-group">
			<button type="button" icon="check" text="true" onclick="Core.fn($('#${_zone}').parents('div[tabs=true]:first').parent(),'submitForm')('${_zone}_form','${_zone}');">提交</button>
		</div>
	</div>

</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>