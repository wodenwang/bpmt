<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('form[sync=true]', $zone).submit(function(event) {
			event.preventDefault();
			var $form = $(this);
			Core.fn($zone, 'submitDev')($form);
		});

		$('#${_zone}_opr_list_form').submit();
	});
</script>

<div tabs="true" button="left">
	<div title="生成快照">
		<form action="${_acp}/submitDev.shtml" sync="true">
			<table class="ws-table">
				<tr>
					<th>快照唯一健</th>
					<td colspan="3"><c:choose>
							<c:when test="${template.key!=null}">${template.key}</c:when>
							<c:otherwise>
								<font color="red">(自动生成)</font>
							</c:otherwise>
						</c:choose></td>
				</tr>
				<tr>
					<th>生成快照</th>
					<td><wcm:widget name="name" cmd="text{required:true}" /></td>
					<th>基准快照</th>
					<td>${template.key!=null?template.name:'初始开发'}</td>
				</tr>
				<tr>
					<th>生成快照描述</th>
					<td><wcm:widget name="description" cmd="textarea" /></td>
					<th>基准快照描述</th>
					<td>${template.description}</td>
				</tr>
				<tr>
					<th>附加数据表<br /> <font color="red" tip="true" title="目标系统中附加数据表的数据将会被快照中的数据覆盖.">(提示)</font></th>
					<td><wcm:widget name="copyDataTables" cmd="multiselect[$com.riversoft.platform.po.TbTable;name;name]" value="${template.copyDataTables}" /></td>
					<th>基准快照附加表</th>
					<td><wcm:widget name="tmp" cmd="multiselect[$com.riversoft.platform.po.TbTable;name;name]" value="${template.copyDataTables}" state="readonly" /></td>
				</tr>
				<tr>
					<th>即将生成版本</th>
					<td><wcm:widget name="version" cmd="text{number:true}" value="${template.version+1}" state="readonly" /></td>
					<th>基准快照版本</th>
					<td>${template.version}</td>
				</tr>
				<tr>
					<th>当前平台版本</th>
					<td><wcm:widget name="platformVersion" cmd="text" value="${platformVersion}" state="readonly" /></td>
					<th>基准快照平台版本</th>
					<td>${template.platformVersion}</td>
				</tr>
				<tr>
					<th>生成时间</th>
					<td><font color="red">(自动计算)</font></td>
					<th>基准快照生成时间</th>
					<td><f:formatDate value="${template.date}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
				<tr>
					<th class="ws-bar">
						<button icon="play" type="submit">生成</button>
					</th>
				</tr>
			</table>
		</form>
	</div>
	<div title="开发日志">
		<form zone="${_zone}_opr_list" action="${_acp}/oprList.shtml" query="true" id="${_zone}_opr_list_form" method="get">
			<input type="hidden" name="_field" value="createDate" /> <input type="hidden" name="_dir" value="desc" /> <input type="hidden" name="_ne_version" value="${template.version}" />
		</form>
		<div id="${_zone}_opr_list"></div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>