<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $table = $('table[name=userSetting] tbody', $zone);

		$table.sortable({
			helper : function(e, ui) {
				ui.children().each(function() {
					$(this).width($(this).width()); //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了  
				});
				return ui;
			},
			axis : "y"
		});

		Core.fn($zone, 'removeRole', function(btn) {
			var $btn = $(btn);
			Ui.confirm('确认移除当前角色?', function() {
				$btn.parents('tr:first').remove();
				$table.styleTable();
			});
		});

		$('button[name=del]', $zone).click(function() {
			Core.fn($zone, 'removeRole')(this);
		});

	});
</script>

<div id="${_zone}_msg"></div>

<form action="${_acp}/submitUserRoleSetting.shtml" zone="${_zone}_msg" option="{confirmMsg:'确认保存当前角色分配方案?'}">
	<input type="hidden" name="uid" value="${uid}" />
	<div class="ws-bar">
		<div class="left ws-group"></div>
		<div class="right">
			<button icon="disk" type="submit" tip="true" title="拖拽用户可以设置优先顺序.">保存设置</button>
		</div>
	</div>
	<table class="ws-table" name="userSetting">
		<thead>
			<tr>
				<th>操作</th>
				<th>所属组织</th>
				<th>所属角色</th>
				<th>默认</th>
				<th>创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="vo">
				<tr>
					<td style="width: 7em;" class="center ws-group"><c:if test="${vo.sysFlag!=1}">
							<button icon="trash" type="button" text="false" name="del">删除</button>
						</c:if></td>
					<td class="center"><textarea style="display: none;" name="relate">{roleKey:'${vo.roleKey}',groupKey:'${vo.groupKey}'}</textarea>
						${wcm:widget('select[$com.riversoft.platform.po.UsGroup;groupKey;busiName;null;false]',vo.groupKey)}</td>
					<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsRole;roleKey;busiName;null;false]',vo.roleKey)}</td>
					<td class="center"><c:choose>
							<c:when test="${vo.defaultFlag==1}">
								<input type="radio" name="default" value="${vo.groupKey};${vo.roleKey}" checked="checked" />
								<label>设为默认</label>
							</c:when>
							<c:otherwise>
								<input type="radio" name="default" value="${vo.groupKey};${vo.roleKey}" />
								<label>设为默认</label>
							</c:otherwise>
						</c:choose></td>
					<td class="center">${wcm:widget('date',vo.createDate)}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>