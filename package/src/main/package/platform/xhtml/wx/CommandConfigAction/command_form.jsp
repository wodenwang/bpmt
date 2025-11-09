<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//绑定提交事件
		$("#${_zone}_form").submit(function() {
			var $this = $(this);
			Core.fn($zone, 'submit')($this);
			return false;
		});

		var checkMpFlag = function(val) {
			$.each($('textarea', $('#${_zone}_support_types')), function() {
				var o = eval('(' + $(this).val() + ')');
				if (val == '1' && o.type > 0 || val != '1' && o.type < 0) {
					$(":checkbox[name='supportType'][value='" + o.code + "']", $zone).iCheck('uncheck').iCheck('disable');
				} else {
					$(":checkbox[name='supportType'][value='" + o.code + "']", $zone).iCheck('enable');
				}
			});
		};

		$(':radio[name=mpFlag]', $zone).on('ifClicked', function(event) {
			var val = $(this).val();
			checkMpFlag(val);
		});

		checkMpFlag($(':radio[name=mpFlag]:checked', $zone).val());

	});
</script>

<div id="${_zone}_msg_zone" name="msgZone"></div>

<div id="${_zone}_support_types" style="display: none;">
	<c:forEach items="${supportTypes}" var="o">
		<textarea>{type:${o.type},code:${o.code}}</textarea>
	</c:forEach>
</div>

<form action="${_acp}/submit.shtml" sync="true" id="${_zone}_form">
	<div accordion="true" multi="true">
		<div title="基础信息">
			<table class="ws-table">
				<tr>
					<th>逻辑主键</th>
					<td><c:choose>
							<c:when test="${vo==null}">
								<wcm:widget name="commandKey" cmd="key{required:true}" />
								<input type="hidden" name="isCreate" value="1" />
							</c:when>
							<c:otherwise>
								<input type="hidden" name="commandKey" value="${vo.commandKey}" />
								<input type="hidden" name="isCreate" value="0" />
								<c:out value="${vo.commandKey}" />
							</c:otherwise>
						</c:choose></td>
				</tr>
				<tr>
					<th>展示名</th>
					<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
				</tr>
				<tr>
					<th>描述</th>
					<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
				</tr>
				<tr>
					<th rowspan="2">使用范围</th>
					<td><wcm:widget name="mpFlag" cmd="radio[@com.riversoft.platform.translate.WxCommandMpFlag]" value="${vo.mpFlag}" /></td>
				</tr>
				<tr>
					<td c><wcm:widget name="supportType" cmd="checkbox[@com.riversoft.platform.translate.WxCommandSupportType]" value="${vo.supportType}" /></td>
				</tr>
			</table>
		</div>
		<div title="脚本">
			<table class="ws-table">
				<tr>
					<th>脚本类型</th>
					<td><wcm:widget name="logicType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.logicType}" /></td>
				</tr>
				<tr>
					<th>脚本<br /> <font color="red" tip="true" title="wo:微信消息对象;mp:公众号函数库;agent:企业号应用函数库;更多参考开发者手册">(提示)</font></th>
					<td><wcm:widget name="logicScript" cmd="codemirror[groovy]{required:true}" value="${vo.logicScript}" /></td>
				</tr>
			</table>
		</div>
	</div>

	<div class="ws-bar">
		<button type="submit" icon="disk">保存</button>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>