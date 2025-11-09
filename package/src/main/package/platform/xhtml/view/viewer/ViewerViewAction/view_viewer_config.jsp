<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//事件绑定类型
		$(":radio[name='resultType']", $zone).on('ifChanged', function() {
			var type = $(this).val();
			var $table = $("#${_zone}_table", $zone);

			if (type >= 5) {
				$('tr[name]', $table).hide();
				$('tr[name=' + type + ']', $table).show();
			} else {
				$('tr[name]', $table).hide();
				$('tr[name=other]', $table).show();
				$(":radio[name='tempFileType']:checked", $zone).prop('checked', false).iCheck('check');
			}
		});

		// 模板文件类型单选
		$(":radio[name='tempFileType']", $zone).on('ifChanged', function() {
			var type = $(this).val();
			var $table = $("#${_zone}_table", $zone);
			// 模板文件类型处理
			$('tr[name=other]', $table).show();
			if (type == '1') {
				$('tr[filetype=template]', $table).hide();
				$('tr[filetype=path]', $table).show();
			} else {
				$('tr[filetype=template]', $table).show();
				$('tr[filetype=path]', $table).hide();
			}
		});

		// 触发点击模板文件类型
		$(":radio[name='tempFileType']:checked", $zone).prop('checked', false).iCheck('check');
		$(":radio[name='resultType']:checked", $zone).prop('checked', false).iCheck('check');
	});
</script>

<div tabs="true" id="${_zone}_tabs">
	<div title="基础设置">
		<table class="ws-table" id="${_zone}_table">
			<tr>
				<th>展示名</th>
				<td><wcm:widget name="busiName" cmd="text{required:true}" value="${table.busiName}" /></td>
			</tr>
			<tr>
				<th>输出类型</th>
				<td><wcm:widget name="resultType" cmd="radio[@com.riversoft.module.view.viewer.ResultType]" value="${table.resultType}" /></td>
			</tr>
			<tr name="other">
				<th>模板文件类型</th>
				<td><wcm:widget name="tempFileType" cmd="radio[@com.riversoft.module.view.viewer.TempletType]" value="${table.tempFileType}" /></td>
			</tr>
			<tr name="other" filetype="template" class="last-child">
				<th>模板文件</th>
				<td><wcm:widget name="templateFile" cmd="filemanager" value="${table.templateFile}" /></td>
			</tr>
			<tr name="other" filetype="path" class="last-child">
				<th>模板文件路径</th>
				<td><wcm:widget name="tempFilePath" cmd="textarea[50%]{}" value="${table.tempFilePath}" /></td>
			</tr>
			<tr name="5">
				<th>脚本类型</th>
				<td><wcm:widget name="fileType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${table.fileType}" /></td>
			</tr>
			<tr name="5" class="last-child">
				<th>下载内容 <font color="red" tip="true" title="1. 返回二进制、数据流或文件。 <br />2. 除内置函数外还可以使用处理器变量里面定义的变量。">(提示)</font></th>
				<td><wcm:widget name="fileScript" cmd="codemirror[groovy]" value="${table.fileScript}" /></td>
			</tr>
			<tr name="6">
				<th>脚本类型</th>
				<td><wcm:widget name="textType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${table.textType}" /></td>
			</tr>
			<tr name="6" class="last-child">
				<th>文本 <font color="red" tip="true" title="1. 返回类型须为String， 比如返回一段JSON，一段XML或者普通文本均可。 <br />2. 除内置函数外还可以使用处理器变量里面定义的变量。">(提示)</font></th>
				<td><wcm:widget name="textScript" cmd="codemirror[groovy]" value="${table.textScript}" /></td>
			</tr>
			<tr name="7">
				<th>脚本类型</th>
				<td><wcm:widget name="msgType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${table.msgType}" /></td>
			</tr>
			<tr name="7" class="last-child">
				<th>消息内容<font color="red" tip="true"
					title="1. 定义返回消息的类型[info, warning或error]和内容。<br />2. 返回String则按info消息展示。<br />3. 可以返回一个包含type和msg的map，其中type为[info, warning, error]中的一种。<br />4. 除内置函数外还可以使用处理器变量里面定义的变量。">(提示)</font>
				</th>
				<td><wcm:widget name="msgScript" cmd="codemirror[groovy]" value="${table.msgScript}" /></td>
			</tr>
			<tr name="8">
				<th>脚本类型</th>
				<td><wcm:widget name="urlType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${table.urlType}" /></td>
			</tr>
			<tr name="8" class="last-child">
				<th>跳转网址 <font color="red" tip="true" title="1. 返回网址类型须为String。 <br />2. 除内置函数外还可以使用处理器变量里面定义的变量。">(提示)</font></th>
				<td><wcm:widget name="urlScript" cmd="codemirror[groovy]" value="${table.urlScript}" /></td>
			</tr>
		</table>
	</div>
	<div title="处理器变量">
		<div class="ws-msg info">
			1. 可以定义多个处理器变量供该变量后面的处理器变量或者基础设置里面脚本使用。<br />2. 可以在处理器变量里面定义除赋值外其他业务逻辑。<br />3. 多个处理器变量和业务逻辑共用一个事务。<br />
		</div>
		<div id="${_zone}_vars" init="${_acp}/varConfigForm.shtml?key=${table.viewKey}"></div>
	</div
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>