<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		Core.fn($zone, 'del', function(viewKey) {
			Ui.confirmPassword('确认删除动态视图?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/delete.shtml?viewKey=' + viewKey, {
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});
		Core.fn($zone, 'submitForm', function(formid, tabid) {
			var form = $('#' + formid);
			var zone = '${_zone}_msg';//信息提示区域
			var option = eval('(' + form.attr("option") + ')');

			option = $.extend({}, {
				callback : function(flag) {
					if (flag) {//调用成功
						//关闭tab
						Ui.closeTab(tabid);
						$('#${_zone}_list_form').submit();
					}
				},
				btn : $('button', form)
			}, option);
			$.scrollTo($("#" + zone));
			Ajax.form(zone, form, option);
		});

		Core.fn($zone, 'lock', function(viewKey) {
			Ajax.post('${_zone}_msg', '${_acp}/lock.shtml', {
				data : {
					viewKey : viewKey,
					lockFlag : '1'
				},
				callback : function(flag) {
					if (flag) {
						$('#${_zone}_list_form').submit();
					}
				}
			});
		});

		Core.fn($zone, 'unlock', function(viewKey) {
			Ui.confirmPassword('确认解锁?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/lock.shtml', {
					data : {
						viewKey : viewKey,
						lockFlag : '0'
					},
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});

		Core.fn($zone, 'copy', function(viewKey) {
			Ui.confirm('是否复制视图[' + viewKey + ']?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/copy.shtml', {
					data : {
						viewKey : viewKey
					},
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});

		//绑定模块初始化
		$('button:reset', $zone).click(function(event) {
			event.preventDefault();
			$('select[name=_se_viewClass]').val('').trigger("liszt:updated");
		});

		$('#${_zone}_list_form').submit();
	});
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<div title="动态视图汇总">
		<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
			<input type="hidden" name="_field" value="updateDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>视图主键(模糊)</th>
					<td><wcm:widget name="_sl_viewKey" cmd="text" /></td>
					<th>描述(模糊)</th>
					<td><wcm:widget name="_sl_description" cmd="text" /></td>
				</tr>
				<tr>
					<th>绑定模块</th>
					<td><select name="_se_viewClass" class="chosen">
							<option value="">请选择</option>
							<c:forEach items="${moduleGroups}" var="group">
								<optgroup label="${group.key}">
									<c:forEach items="${group.value}" var="module">
										<option value="${module.name}">${module.description}</option>
									</c:forEach>
								</optgroup>
							</c:forEach>
					</select></td>
					<th>是否锁定</th>
					<td><wcm:widget name="_ne_lockFlag" cmd="select[YES_NO(请选择)]" /></td>
				</tr>
				<tr>
					<th>创建时间(&gt;=)</th>
					<td><wcm:widget name="_dnl_createDate" cmd="date" /></td>
					<th>创建时间(&lt;=)</th>
					<td><wcm:widget name="_dnm_createDate" cmd="date" /></td>
				</tr>
				<tr>
					<th>更新时间(&gt;=)</th>
					<td><wcm:widget name="_dnl_updateDate" cmd="date" /></td>
					<th>更新时间(&lt;=)</th>
					<td><wcm:widget name="_dnm_updateDate" cmd="date" /></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<%--错误提示区域 --%>
		<div id="${_zone}_msg"></div>

		<%--查询结果 --%>
		<div id="${_zone}_list"></div>

	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>