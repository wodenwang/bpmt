<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.busiName']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('button[name=viewSubAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_subs_tabs');
			Ajax.tab($tabs, '${_acp}/viewSubViewConfig.shtml', {
				data : {
					type : 'subs',
					pixel : 'subs.B' + Core.nextSeq()
				}
			});
		});

	});
</script>

<input type="hidden" name="hasSubs" value="true" />
<div class="ws-bar">
	<div class="ws-group left">
		<button icon="plus" type="button" name="viewSubAdd">视图标签</button>
	</div>
</div>

<div tabs="true" button="left" sort="y" id="${_zone}_subs_tabs">
	<c:if test="${subs!=null&&fn:length(subs)>0}">
		<c:forEach items="${subs}" var="vo" varStatus="status">
			<c:choose>
				<%-- 视图标签 --%>
				<c:when test="${vo.action!=null}">
					<div title="${vo.busiName}" close="true">
						<c:set var="pixel" value="subs.A${status.index}" />
						<input type="hidden" name="subs" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>展示名</th>
								<td><input type="text" name="${pixel}.busiName" class="{required:true}" value="${vo.busiName}" /></td>
							</tr>
							<tr>
								<th>标签样式</th>
								<td><wcm:widget name="${pixel}.style" cmd="style" value="${vo.style}" /></td>
							</tr>
							<tr>
								<th>链接视图</th>
								<td><wcm:widget name="${pixel}.action" cmd="view[SUB]{required:true}" value="${vo.action}" /></td>
							</tr>
							<tr>
								<th>动态入参(脚本类型)</th>
								<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.paramType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>动态入参(脚本)<br /> <font color="red" tip="true" title="在视图模块中在request中通过[_params]命令字获取.">(提示)</font></th>
								<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]" value="${vo.paramScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>功能点</th>
								<td><wcm:widget name="${pixel}.pri" cmd="pri[vo:实体]{required:true}" value="${vo.pri}" /></td>
							</tr>
						</table>
					</div>
				</c:when>
			</c:choose>
		</c:forEach>
	</c:if>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>