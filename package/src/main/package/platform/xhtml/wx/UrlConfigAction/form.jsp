<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//事件绑定类型
		$(":radio[name='wxType']", $zone).on('ifChanged', function() {
			var type = $(this).val();
			var $form = $('form', $zone);
			if (type == 1) {
				$('tr[name=wxKey]', $form).show();
			} else {
				$('tr[name=wxKey]', $form).hide();
			}
		});

		$('button[name=submitForm]', $zone).click(function() {
			var $form = $('form', $zone);
			Core.fn($zone, 'submitForm')($form, $zone, {
				confirmMsg : '确认提交?',
				errorZone : '${_zone}_err_zone'
			});
		});

		$(":radio[name='wxType']:checked", $zone).prop('checked', false).iCheck('check');

	});
</script>

<c:set var="editFlag" value="${vo!=null}" />
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitForm.shtml" method="post">
	<table class="ws-table">
		<tr>
			<th>逻辑主键</th>
			<td><c:choose>
					<c:when test="${vo==null}">
						<wcm:widget name="urlKey" cmd="key{required:true}" />
					</c:when>
					<c:otherwise>
						<input type="hidden" name="urlKey" value="${vo.urlKey}" />
						<c:out value="${vo.urlKey}" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<c:if test="${vo != null}">
			<tr>
				<th>外部地址</th>
				<td><span style="color: red;">${domain}${vo.urlKey}</span></td>
			</tr>
		</c:if>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea" value="${vo.description}"></wcm:widget></td>
		</tr>

		<tr>
			<th>平台类型</th>
			<td><wcm:widget name="wxType" cmd="radio[@com.riversoft.platform.translate.WxType]{required:true}" value="${vo.wxType}"></wcm:widget></td>
		</tr>

		<tr name="wxKey">
			<th>公众号</th>
			<td><wcm:widget name="wxKey" cmd="select[$WxMp(请选择);mpKey;title]" value="${vo.wxKey}"></wcm:widget></td>
		</tr>
		<tr name="wxKey">
			<th>跳转方式</th>
			<td><wcm:widget name="wxScope" cmd="radio[@com.riversoft.platform.translate.WxScope]" value="${vo.wxScope}" /></td>
		</tr>

		<tr>
			<th>视图</th>
			<td><wcm:widget name="action" cmd="view[WX]{required:true}" value="${vo.action}" /></td>
		</tr>

		<tr>
			<th>动态入参(脚本类型)</th>
			<td><wcm:widget name="paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.paramType}"></wcm:widget></td>
		</tr>
		<tr>
			<th>动态入参(脚本)</th>
			<td><wcm:widget name="paramScript" cmd="codemirror[groovy]" value="${vo.paramScript}" /></td>
		</tr>

		<c:if test="${editFlag}">
			<tr>
				<th>创建时间</th>
				<td>${wcm:widget('date[datetime]',vo.createDate)}</td>
			</tr>
			<tr>
				<th>更新时间</th>
				<td>${wcm:widget('date[datetime]',vo.updateDate)}</td>
			</tr>
		</c:if>
	</table>
</form>

<div class="ws-bar">
	<div class="ws-group">
		<button type="button" icon="check" text="true" name="submitForm">保存</button>
	</div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>