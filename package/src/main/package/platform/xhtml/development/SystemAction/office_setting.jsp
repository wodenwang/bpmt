<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=reload]', $zone).click(function() {
			Ajax.post($zone, '${_acp}/setOffice.shtml');
		});
	});
</script>

<form zone="${_zone}_msg" action="${_acp}/submitOfficeSetting.shtml" option="{confirmMsg:'确认保存?'}">
	<div class="ws-bar">
		<div class="left"></div>
		<div class="right">
			<button type="button" icon="refresh" name="reload">刷新</button>
			<button type="submit" icon="disk">保存</button>
		</div>
	</div>
	<div id="${_zone}_msg"></div>
	<div tabs="true" button="left">
		<div title="文档上传">
			<table class="ws-table">
				<tr>
					<th>上传大小限制 <font color="red" title="单位:M,默认值100" tip="true">(提示)</font></th>
					<td><wcm:widget name="office.upload.size" cmd="text{digits:true}" value="${config['office.upload.size']}" /></td>
				</tr>
				<tr>
					<th>允许文件后缀<br /> <font color="red" title="小写,逗号分隔;系统已内置支持[常用图片格式],[常用OFFICE文档格式],[常用压缩文件格式]." tip="true">(提示)</font></th>
					<td><wcm:widget name="office.upload.pixel" cmd="textarea" value="${config['office.upload.pixel']}" /></td>
				</tr>
			</table>
		</div>
		<div title="文档转换">
			<div accordion="true" multi="true">
				<div title="基础设置">
					<table class="ws-table">
						<tr>
							<th>总开关</th>
							<td><wcm:widget name="office.flag" cmd="radio[YES_NO]" value="${config['office.flag']}" /></td>
						</tr>
						<tr>
							<th>是否启用缓存</th>
							<td><wcm:widget name="office.prepare" cmd="radio[YES_NO]" value="${config['office.prepare']}" /></td>
						</tr>
						<tr>
							<th>转换大小限制 <font color="red" title="单位:M,默认值:10" tip="true">(提示)</font></th>
							<td><wcm:widget name="office.file.size" cmd="text{digits:true}" value="${config['office.file.size']}" /></td>
						</tr>
					</table>
				</div>
				<div title="系统服务">
					<table class="ws-table">
						<tr>
							<th>OFFICE安装目录</th>
							<td><wcm:widget name="office.installation.path" cmd="textarea" value="${config['office.installation.path']}" /></td>
						</tr>
						<tr>
							<th>端口<font color="red" tip="true" title="留空则使用协议默认端口.">(提示)</font></th>
							<td><wcm:widget name="office.port" cmd="text{digits:true}" value="${config['office.port']}" /></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>