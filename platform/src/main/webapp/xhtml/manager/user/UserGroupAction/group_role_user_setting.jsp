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

		Core.fn($zone, 'removeUser', function(btn) {
			var $btn = $(btn);
			Ui.confirm('确认从当前角色移除用户?', function() {
				$btn.parents('tr:first').remove();
				$table.styleTable();
			});
		});

		$('button[name=del]', $zone).click(function() {
			Core.fn($zone, 'removeUser')(this);
		});

		$('button[name=add]', $zone).click(function() {
			Ajax.win('${_acp}/selectUser.shtml', {
				title : '选择用户',
				minWidth : 800,
				minHeight : 600,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '选中',
					click : function() {
						var $win = $(this);
						$.each($(':checkbox:checked', $win), function() {
							var $winTr = $(this).parents('tr:first');
							var uid = $('input[name=uid]', $winTr).val();
							var busiName = $('input[name=busiName]', $winTr).val();
							if (uid != null && $('input[name=uid][value=' + uid + ']', $table).size() == 0) {//uid不存在
								//构建新行
								var $tr = $('<tr></tr>');
								$table.append($tr);
								{
									var $td = $('<td style="width: 7em;" class="center"></td>');
									var $delBtn = $('<button type="button" icon="trash" text="false">删除</button>');
									$delBtn.styleButton();
									$delBtn.click(function() {
										Core.fn($zone, 'removeUser')(this);
									});
									$td.append($delBtn);
									$tr.append($td);
								}
								{
									var $td = $('<td class="center"></td>');
									$td.html(uid);
									var $input = $('<input type="hidden" name="uid" />');
									$input.val(uid);
									$td.append($input);
									$tr.append($td);
								}
								{
									var $td = $('<td class="center"></td>');
									$td.html(busiName);
									$tr.append($td);
								}
								{
									var $td = $('<td class="center"></td>');
									var $checkbox = $('<input type="checkbox" name="default"/>');
									$checkbox.val(uid);
									$td.append($checkbox).append('<label>是</label>');
									$tr.append($td);
									$('input:checkbox', $td).radioset();
								}
							}
						});
						$('table[name=userSetting]', $zone).styleTable();
						$win.dialog("close");
					}
				} ]
			});
		});
	});
</script>

<div id="${_zone}_msg"></div>

<table class="ws-table">
	<tr>
		<th>组织</th>
		<td>[${group.groupKey}]${group.busiName} <c:if test="${otherGroups!=null&&otherGroups!=''}">
				<span style="margin-left: 5px; color: red;" tip="true" selector=".relate_group"><span class="relate_group">${otherGroups}</span>(其他相关)</span>
			</c:if></td>
	</tr>
	<tr>
		<th>角色</th>
		<td>[${role.roleKey}]${role.busiName}</td>
	</tr>
</table>

<form action="${_acp}/submitGroupRoleUserSetting.shtml" zone="${_zone}_msg" option="{confirmMsg:'确认保存当前用户分配方案?'}">
	<input type="hidden" name="groupKey" value="${group.groupKey}" /><input type="hidden" name="roleKey" value="${role.roleKey}" />

	<div class="ws-bar">
		<div class="left ws-group">
			<button icon="plus" type="button" name="add" tip="true" title="此操作仅用于已有用户分配,新用户请到[用户管理]创建.">分配用户</button>
		</div>
		<div class="right">
			<button icon="disk" type="submit" tip="true" title="拖拽用户可以设置优先顺序.">保存设置</button>
		</div>
	</div>

	<table class="ws-table" name="userSetting">
		<thead>
			<tr>
				<th>操作</th>
				<th>用户登陆名</th>
				<th>用户展示名</th>
				<th>是否默认角色</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="vo">
				<tr>
					<td style="width: 7em;" class="center"><c:if test="${vo.sysFlag!=1}">
							<button icon="trash" type="button" text="false" name="del">删除</button>
						</c:if></td>
					<td class="center">${vo.uid}<input type="hidden" name="uid" value="${vo.uid}" />
					</td>
					<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName;null;false]',vo.uid)}</td>
					<td class="center"><c:choose>
							<c:when test="${vo.defaultFlag==1}">
								<input type="checkbox" name="default" value="${vo.uid}" checked="checked" />
								<label>是</label>
							</c:when>
							<c:otherwise>
								<input type="checkbox" name="default" value="${vo.uid}" />
								<label>是</label>
							</c:otherwise>
						</c:choose></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>