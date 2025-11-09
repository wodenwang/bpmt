<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//鼠标hover事件
		$('div.g', $zone).hover(function() {
			if (!$(this).attr('hold')) {
				$(this).css('border', '5px solid blue');
			}
		}, function() {
			if (!$(this).attr('hold')) {
				$(this).css('border', '');
			}
		});

		//单击事件
		$('div.g', $zone).click(function() {
			$('div.g', $zone).css('border', '');
			$('div.g', $zone).removeAttr('hold');
			$(this).css('border', '5px solid red');
			$(this).attr('hold', true);

			var activityId = $(this).attr('name');
			Ajax.win('${_acp}/detail.shtml', {
				title : '节点配置',
				minWidth : 1024,
				data : {
					activityId : activityId,
					pdId : '${pd.id}'
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '保存',
					click : function() {
						var $this = $(this);
						var $form = $('form', $this);
						if ($form.size() > 0) {
							var option = $form.attr("option");
							if (option == null) {
								option = {};
							} else {
								option = eval('(' + option + ')');
							}
							option.callback = function(flag) {
								if (flag) {
									$this.dialog("close");
								}
							};
							Ajax.form('${_zone}_node_msg', $form, option);

						} else {
							Ui.alert('此类型节点无需配置.');
						}
					}
				} ]
			});
		});

		//直接设置工作流视图
		$('button[name=editView]', $zone).click(function() {
			var viewKey = $('select[name=basicViewKey]', $zone).val();
			if (viewKey == undefined || viewKey == '') {
				Ui.alert('请先选择待编辑的视图模块.');
				return;
			}

			var $win = Ajax.win('${_acp}/updateViewZone.shtml', {
				title : '流程基础视图设置',
				minWidth : 1024,
				data : {
					viewKey : viewKey
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '保存',
					click : function() {
						var $this = $(this);
						Core.fn($this, 'submitForm')();
					}
				} ]
			});
		});

		$('a[tabId]', $zone).click(function() {
			var tabId = $(this).attr('tabId');
			var $tabs = $('#${_zone}_tabs');

			$('ul li a[tab=' + $('div[tabId="' + tabId + '"]', $tabs).attr('tab') + ']:first', $tabs).click();
		});

		//绑定表
		$('button[name=lockTable]', $zone).click(function() {
			var tableType = $(this).val();
			var $select = $('select[name=lockTable][tableType=' + tableType + ']', $zone);
			if ($select.val() == '') {
				Ui.alert('请先选择一个锁定表.');
				return;
			}
			Ui.confirm('确认锁定表[' + $select.val() + ']?', function() {
				Ajax.post('${_zone}_pd_msg_zone', '${_acp}/lockTable.shtml', {
					data : {
						pdId : $(':hidden[name=pdId]', $zone).val(),
						name : $select.val(),
						tableType : tableType
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
						}
					}
				});
			});
		});

		//接触绑定表
		$('button[name=unlockTable]', $zone).click(function() {
			var tableType = $(this).val();
			Ui.confirm('确认解除表?', function() {
				Ajax.post('${_zone}_pd_msg_zone', '${_acp}/unlockTable.shtml', {
					data : {
						pdId : $(':hidden[name=pdId]', $zone).val(),
						tableType : tableType
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
						}
					}
				});
			});
		});
	});
</script>

<div tabs="true" id="${_zone}_tabs">
	<div title="流程概况(版本:${pd.version})">

		<div id="${_zone}_pd_msg_zone"></div>

		<form action="${_acp}/submitPdConfig.shtml" zone="${_zone}_pd_msg_zone" option="{confirmMsg:'确认保存设置?'}">
			<input type="hidden" name="pdId" value="${pd.id}" />
			<table class="ws-table">
				<tr>
					<th>唯一KEY<br /> <font color="red" tip="true" title="启动/执行流程的按钮中需要此KEY作为入参.">(提示)</font></th>
					<td>${pd.key}</td>
				</tr>
				<tr>
					<th>版本</th>
					<td>${pd.version}</td>
				</tr>
				<tr>
					<th>流程名称</th>
					<td>${pd.name}</td>
				</tr>
				<tr>
					<th>[A1]订单表</th>
					<td><a tabId="A1" href="#">${wcm:widget('select[$com.riversoft.platform.po.TbTable;name;description;null;true]',view.tableName)}</a></td>
				</tr>
				<tr>
					<th>[A2]历史表</th>
					<td><c:choose>
							<c:when test="${view.historyTableName!=null&&view.historyTableName!=''}">
								<a tabId="A2" href="#">${wcm:widget('select[$com.riversoft.platform.po.TbTable;name;description;null;true]',view.historyTableName)}</a>
								<button type="button" icon="unlocked" text="false" name="unlockTable" value="historyTableName">解除绑定</button>
							</c:when>
							<c:otherwise>
								<select name="lockTable" class="chosen" tableType="historyTableName">
									<option value="">请选择</option>
									<c:forEach items="${historyTables}" var="model">
										<option value="${model.name}">[${model.name}]${model.description}</option>
									</c:forEach>
								</select>
								<button type="button" icon="locked" text="false" name="lockTable" value="historyTableName">绑定</button>
							</c:otherwise>
						</c:choose></td>
				</tr>
				<tr>
					<th>[A3]审批意见表</th>
					<td><c:choose>
							<c:when test="${view.opinionTableName!=null&&view.opinionTableName!=''}">
								<a tabId="A3" href="#">${wcm:widget('select[$com.riversoft.platform.po.TbTable;name;description;null;true]',view.opinionTableName)}</a>
								<button type="button" icon="unlocked" text="false" name="unlockTable" value="opinionTableName">解除绑定</button>
							</c:when>
							<c:otherwise>
								<select name="lockTable" class="chosen" tableType="opinionTableName">
									<option value="">请选择</option>
									<c:forEach items="${opinionTables}" var="model">
										<option value="${model.name}">[${model.name}]${model.description}</option>
									</c:forEach>
								</select>
								<button type="button" icon="locked" text="false" name="lockTable" value="opinionTableName">绑定</button>
							</c:otherwise>
						</c:choose></td>
				</tr>
				<tr>
					<th>[B1]基础视图</th>
					<td><a tabId="B1" href="#">${wcm:widget('select[$com.riversoft.platform.po.VwUrl;viewKey;description;null;true]',config.basicViewKey)}</a></td>
				</tr>
				<tr>
					<th>部署时间</th>
					<td>${wcm:widget('date[datetime]',dm.deploymentTime)}</td>
				</tr>
				<tr>
					<th>备注</th>
					<td><wcm:widget name="description" cmd="textarea" value="${config.description}" /></td>
				</tr>
				<tr>
					<th class="ws-bar">
						<div class="ws-group">
							<button type="submit" icon="disk">保存备注</button>
						</div>
					</th>
				</tr>
			</table>
		</form>
	</div>

	<div title="[A1]订单表" tabId="A1" init="${_acp}/tableZone.shtml?tableName=${view.tableName}"></div>

	<c:if test="${view.historyTableName!=null&&view.historyTableName!=''}">
		<div title="[A2]历史表" tabId="A2" init="${_acp}/tableZone.shtml?tableName=${view.historyTableName}"></div>
	</c:if>

	<c:if test="${view.opinionTableName!=null&&view.opinionTableName!=''}">
		<div title="[A3]审批意见表" tabId="A3" init="${_acp}/tableZone.shtml?tableName=${view.opinionTableName}"></div>
	</c:if>

	<div title="[B1]基础视图" tabId="B1" init="${_acp}/viewZone.shtml?viewKey=${config.basicViewKey}"></div>

	<div title="节点设置">
		<div id="${_zone}_node_msg"></div>
		<div style="border: 1px dashed #000; margin-bottom: 5px;">
			<!-- 流程图 -->
			<div id="${_zone}_img" style=" position: relative; height:  ${baseG.maxY-baseG.minY+30}px; width: auto; overflow: auto;">
				<img style="position: absolute;" alt="流程图" src="${_acp}/picture.shtml?id=${pd.id}" />

				<%-- 坐标信息 --%>
				<c:forEach items="${gs}" var="entry">
					<c:set var="g" value="${entry.value}" />
					<div class="g" name="${entry.key}"
						style="position: absolute;cursor: pointer;width: ${g.width-5}px; height: ${g.height-5}px; left:${g.x-baseG.minX+2}px;top:${g.y-baseG.minY+2}px;border-radius: 15px;"></div>
				</c:forEach>
			</div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>