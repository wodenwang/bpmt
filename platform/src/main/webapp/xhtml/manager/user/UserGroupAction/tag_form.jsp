<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $table = $('table[name=userSetting] tbody', $zone);

		//表单动作
		$('form', $zone).submit(function() {
			var $form = $(this);
			Ajax.form('${_zone}', $form, {
				errorZone : '${_zone}_msg',
				confirmMsg : '是否保存?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
					}
				}
			});
			return false;
		});
		
		Core.fn($zone, 'removeUser', function(btn) {
			var $btn = $(btn);
			Ui.confirm('确认从当前标签移除用户?', function() {
				$btn.parents('tr:first').remove();
				$table.styleTable();
			});
		});

		$('button[name=del]', $zone).click(function() {
			Core.fn($zone, 'removeUser')(this);
		});
		
		$('button[name=addUser]', $zone).click(function() {
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

<div tabs="true">
	<c:choose>
		<c:when test="${isCreate}">
			<c:set var="title" value="添加标签" />
		</c:when>
		<c:otherwise>
			<c:set var="title" value="标签[${vo.busiName}]编辑" />
		</c:otherwise>
	</c:choose>
	<div title="${title}">
		<%--错误提示区域 --%>
		<div id="${_zone}_msg"></div>
		<%--表单 --%>
		<form action="${_acp}/submitTagForm.shtml" method="post" sync="true">
			<input type="hidden" name="isCreate" value="${isCreate?1:0}" />
			<table class="ws-table">
				<tr>
					<th>标签KEY</th>
					<td><wcm:widget name="tagKey" cmd="key{required:true}" value="${vo.tagKey}" state="${isCreate?'normal':'readonly'}" /></td>
				</tr>
				<tr>
					<th>展示名</th>
					<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
				</tr>
			</table>
			<div class="ws-bar">
				<div class="left ws-group">
			         <button icon="plus" type="button" name="addUser" tip="true">分配用户</button>
		        </div>
		        <div class="right">
			         <button icon="disk" type="submit" tip="true">保存设置</button>
          		</div>
			</div>
			
	 <table class="ws-table" name="userSetting">
		<thead>
			<tr>
				<th>操作</th>
				<th>用户登陆名</th>
				<th>用户展示名</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="vo">
				<tr>
					<td style="width: 7em;" class="center">
							<button icon="trash" type="button" text="false" name="del">删除</button>
					</td>
					<td class="center">${vo.uid}<input type="hidden" name="uid" value="${vo.uid}" />
					</td>
					<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName;null;false]',vo.uid)}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
		</form>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>