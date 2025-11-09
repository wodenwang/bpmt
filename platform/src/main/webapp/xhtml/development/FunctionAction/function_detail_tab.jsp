<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div tabs="true">
	<div title="函数[${vo.functionKey}]脚本">
		<div id="${_zone}_result_zone"></div>
		<form action="${_acp}/submitScript.shtml" method="post"
			zone="${_zone}_result_zone"
			option="{confirmMsg:'确认保存函数[${vo.functionKey}]?'}">
			<input type="hidden" name="functionKey" value="${vo.functionKey}" />
			<table class="ws-table">
				<tr>
					<th>脚本类型</th>
					<td><wcm:widget name="functionType"
							cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
							value="${vo.functionType}"></wcm:widget></td>
				</tr>
				<tr>
					<th>脚本</th>
					<td><wcm:widget name="functionScript"
							cmd="codemirror[groovy]{required:true}"
							value="${vo.functionScript}"></wcm:widget></td>
				</tr>
				<tr>
					<th class="ws-bar">
						<div class="ws-group">
							<button type="submit" icon="disk" text="true">保存</button>
						</div>
					</th>
				</tr>
			</table>
		</form>
	</div>
	<div title="函数[${vo.functionKey}]例子">
		<wcm:widget name="_tmp" cmd="codemirror[groovy;true]"
			state="readonly" value="${vo.example}"></wcm:widget>
	</div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>