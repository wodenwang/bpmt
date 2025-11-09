<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(wcm:map(null,'mode',3),'vo',vo)}" />
<c:set var="context" value="${wcm:map(context,'fo',fo)}" />
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<%-- 是否需要校验权限 --%>
<c:set var="ordFlag" value="${param.ordFlag==1}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var $form = $('form[name=flow]', $zone);
		var validateForm = Ajax.validateForm($form);//表单校验
		var $sequenceFlowId = $('input:hidden[name=_FLOW_ID]', $form);
		if ($sequenceFlowId.size() < 1) {
			$sequenceFlowId = $('<input type="hidden" name="_FLOW_ID"/>');
			$form.append($sequenceFlowId);
		}
		var $sequenceFlowName = $('input:hidden[name=_FLOW_NAME]', $form);
		if ($sequenceFlowName.size() < 1) {
			$sequenceFlowName = $('<input type="hidden" name="_FLOW_NAME"/>');
			$form.append($sequenceFlowName);
		}
		var $flowInit = $('input:hidden[name=_INIT]', $form);

		var showError = function($errorZone) {
			var $error = $('<div>${wpf:lan("#:zh[表单验证不通过,请检查所有项是否都已按规定填写.]:en[Form validation is not through, please check if all items are in accordance with the provisions.]#")}</div>');
			$errorZone.children().remove();
			$errorZone.append($error);
			$error.addClass('ws-msg');
			$error.styleMsg({
				type : 'error'
			});
			$.scrollTo('#' + $errorZone.attr('id'), 500);
		};

		//定义为变量方便传递
		var params = $('#${_zone}_params').val();

		//弹出意见
		Core.fn($zone, 'submitWithOpinion', function($btn, btnOption) {
			//先校验表单
			if (validateForm.form()) {
				Ajax.win('${_acp}/confirmWithOpinion.shtml', {
					title : '${wpf:lan("#:zh[填写意见]:en[Fill in the comments]#")}',
					minWidth : 800,
					data : {
						pdId : '${pd.id}',
						activityId : '${fo.activityId}',
						flowId : $btn.attr('sequenceFlowId'),
						confirmMsg : btnOption.confirmMsg
					},
					buttons : [ {
						icons : {
							primary : "ui-icon-close"
						},
						text : '${wpf:lan("#:zh[取消]:en[Cancel]#")}',
						click : function() {
							$(this).dialog("close");
						}
					}, {
						icons : {
							primary : "ui-icon-check"
						},
						text : '${wpf:lan("#:zh[确认]:en[Confirm]#")}',
						click : function() {
							var $this = $(this);
							var $opinion = $('[name=_OPINION]', $this);
							Core.fn($zone, 'submit')($btn, {
								loading : btnOption.loading
							}, {
								_OPINION : $opinion.val()
							});
							$this.dialog("close");
						}
					}, {
						icons : {
							primary : "ui-icon-circle-check"
						},
						text : '${wpf:lan("#:zh[确认并关闭]:en[Confirm and close]#")}',
						click : function() {
							var $this = $(this);
							var $opinion = $('[name=_OPINION]', $this);
							Core.fn($zone, 'submit')($btn, {
								loading : btnOption.loading,
								close : true
							}, {
								_OPINION : $opinion.val()
							});
							$this.dialog("close");
						}
					} ]
				});
			} else {
				var $errorZone = $('#${_zone}_error', $zone);
				showError($errorZone);
			}
		});

		//表单提交
		Core.fn($zone, 'submit', function($btn, btnOption, data) {
			if (data == undefined) {//额外参数
				data = null;
			}
			var loading = 0 + btnOption.loading == 1;
			Ajax.form('${_zone}_error', $form, {
				confirmMsg : btnOption.confirmMsg,
				errorZone : '${_zone}_error',
				btn : $btn,
				dataType : 'json',
				data : data,
				loading : loading,
				focusLoading : true,
				successFn : function(returnObj) {
					//回调
					var fn = Core.fn($zone, 'callback');
					if ($.isFunction(fn)) {
						fn();
					}
					if (btnOption.close) {
						Ui.closeCurrent($zone);
					} else {
						Ajax.post($zone, '${_acp}/detail.shtml', {
							errorZone : '${_zone}_error',
							data : {
								_main : '${param._main}',
								_ORD_ID : returnObj.ordId,
								_ACTIVITY_ID : returnObj.activityId,
								_params : params,
								ordFlag : '${ordFlag?1:0}'
							}
						});
					}
				}
			});
		});

		//转办
		Core.fn($zone, 'forward', function(btnOption, data) {
			Ajax.post('${_zone}_error', '${_acp}/submitForward.shtml', {
				data : data,
				focusLoading : true,
				callback : function(flag) {
					if (flag) {
						var fn = Core.fn($zone, 'callback');
						if ($.isFunction(fn)) {
							fn();
						}
						if (btnOption && btnOption.close) {
							Ui.closeCurrent($zone);
						} else {
							Ajax.post($zone, '${_acp}/detail.shtml', {
								errorZone : '${_zone}_error',
								data : {
									_main : '${param._main}',
									_ORD_ID : '${fo.ordId}',
									_ACTIVITY_ID : '${fo.activityId}',
									_params : params,
									ordFlag : '${ordFlag?1:0}'
								}
							});
						}
					}
				}
			});
		});

		//保存
		$('button[name=save]', $zone).click(function() {
			var $btn = $(this);
			var loading = $btn.attr('loading');

			//保存需要把流程fo信息清理掉
			$flowInit.val('0');
			$sequenceFlowId.val('');
			$sequenceFlowName.val('');

			Core.fn($zone, 'submit')($btn, {
				loading : loading,
				confirmMsg : '${wpf:lan("#:zh[确认暂存订单?]:en[Confirm the temporary order?]#")}'
			});
		});

		//转办
		$('button[name=forward]', $zone).click(function() {
			var btnKey = $(this).val();
			var btnName = $(this).text().trim();
			var clickFn = function($this, btnOption) {
				var $opinion = $('[name=_OPINION]', $this);
				var $uid = $('[name=_FORWARD_UID]', $this);
				var $formFlag = $('[name=formFlag]:checked', $this);
				var uid = $uid.val();
				if (uid != null && uid != '') {
					if ($formFlag.val() != '1') {
						Core.fn($zone, 'forward')(btnOption, {
							btnName : btnName,
							_TASK_ID : '${fo.taskId}',
							_OPINION : $opinion.val(),
							_FORWARD_UID : uid
						});
						$this.dialog("close");
					} else {
						if (validateForm.form()) {
							//保存需要把流程fo信息清理掉
							$flowInit.val('0');
							$sequenceFlowId.val('');
							$sequenceFlowName.val('');
							Ajax.form('${_zone}_error', $form, {
								errorZone : $("div[name=errorZone]", $this).attr('id'),
								dataType : 'json',
								loading : false,
								focusLoading : true,
								successFn : function(returnObj) {
									$this.dialog("close");
									//保存成功才处理转发
									Core.fn($zone, 'forward')(btnOption, {
										btnName : btnName,
										_TASK_ID : '${fo.taskId}',
										_OPINION : $opinion.val(),
										_FORWARD_UID : uid
									});
								}
							});
						} else {
							showError($("div[name=errorZone]", $this));
						}
					}
				} else {
					$("div[name=errorZone]", $this).html('${wpf:lan("#:zh[接收人必填]:en[Recipient shall fill in]#")}.').styleMsg({
						type : 'error'
					});
				}
			};
			Ajax.win('${_acp}/forwardWin.shtml', {
				title : '${wpf:lan("#:zh[转交/提交信息]:en[Transfer/submit information]#")}',
				minWidth : 800,
				data : {
					pdId : '${pd.id}',
					activityId : '${fo.activityId}',
					btnKey : btnKey,
					_TASK_ID : '${fo.taskId}'
				},
				buttons : [ {
					icons : {
						primary : "ui-icon-close"
					},
					text : '${wpf:lan("#:zh[取消]:en[Cancel]#")}',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					icons : {
						primary : "ui-icon-check"
					},
					text : '${wpf:lan("#:zh[确认]:en[Confirm]#")}',
					click : function() {
						var $this = $(this);
						clickFn($this);
					}
				}, {
					icons : {
						primary : "ui-icon-circle-check"
					},
					text : '${wpf:lan("#:zh[确认并关闭]:en[Confirm and close]#")}',
					click : function() {
						var $this = $(this);
						clickFn($this, {
							close : true
						});
					}
				} ]
			});
		});

		//保存并启动流程
		$('button[name=start]', $zone).click(function() {
			var $btn = $(this);
			$flowInit.val('1');

			var confirmMsg = $btn.attr("confirmResult");
			if (confirmMsg == null || confirmMsg == '') {
				confirmMsg = '${wpf:lan("#:zh[确认启动流程?]:en[Confirm start the process?]#")}';
			}

			var opinionFlag = $btn.attr('opinionFlag');
			var loading = $btn.attr('loading');
			var opinionTableName = '${config.table.opinionTableName}';
			if (opinionFlag == '1' && opinionTableName != '') {//需要弹出意见
				Core.fn($zone, 'submitWithOpinion')($btn, {
					confirmMsg : confirmMsg,
					loading : loading
				});
			} else {
				Core.fn($zone, 'submit')($btn, {
					confirmMsg : confirmMsg,
					loading : loading
				});
			}
		});

		//提交任务
		$('button[name=submitTask][checkResult=true]', $zone).click(function() {
			var $btn = $(this);
			$flowInit.val('1');
			$sequenceFlowId.val($btn.attr('sequenceFlowId'));
			$sequenceFlowName.val($btn.attr('sequenceFlowName'));

			var confirmMsg = $btn.attr("confirmResult");
			if (confirmMsg == null || confirmMsg == '') {
				confirmMsg = '${wpf:lan("#:zh[是否提交]:en[Whether to commit]#")}[' + $sequenceFlowName.val() + ']${wpf:lan("#:zh[操作]:en[Operation]#")}?';
			}

			var opinionFlag = $btn.attr('opinionFlag');
			var loading = $btn.attr('loading');
			var opinionTableName = '${config.table.opinionTableName}';
			if (opinionFlag == '1' && opinionTableName != '') {//需要弹出意见
				Core.fn($zone, 'submitWithOpinion')($btn, {
					confirmMsg : confirmMsg,
					loading : loading
				});
			} else {
				Core.fn($zone, 'submit')($btn, {
					confirmMsg : confirmMsg,
					loading : loading
				});
			}
		});

		//冒泡提示
		$.each($('button[name=submitTask]', $zone), function() {
			var $button = $(this);
			var title = $button.attr('title');
			var $span = $button.parent();
			if ('true' == $button.attr('tipFlag')) {
				$span.tooltip({
					track : true,
					content : $('<div>' + title + '</div>').styleMsg({
						type : 'info'
					}).html()
				});
			}

			if ($button.attr("checkResult") != 'true') {
				$button.addClass('ui-button-disabled').addClass('ui-state-disabled');
			}
		});

		//查看订单
		$('button[name=detail]', $zone).click(function() {
			var $btn = $(this);
			Ajax.post($zone, '${_acp}/detail.shtml', {
				errorZone : '${_zone}_error',
				data : {
					_main : '${param._main}',
					_params : params,
					_FO : $('#${_zone}_fo', $zone).val(),
					ordFlag : '${ordFlag?1:0}'
				}
			});
		});

		//初始化"查看更多"菜单
		var $menu = $('#${_zone}_detail_menu').menu({
			select : function(event, ui) {
				var fo = JSON.parse($('#${_zone}_fo', $zone).val());
				var activityId = ui.item.attr('activityId');
				if (activityId) {
					fo.activityId = activityId;
				}
				Ajax.post($zone, '${_acp}/detail.shtml', {
					errorZone : '${_zone}_error',
					data : {
						_main : '${param._main}',
						_params : $('#${_zone}_params').val(),
						_FO : JSON.stringify(fo),
						ordFlag : 0
					}
				});
			}
		});

		//查看订单
		$('button[name=showDetailMenu]', $zone).click(function() {
			var $btn = $(this);
			$menu.show().position({
				my : "left top",
				at : "left+90 bottom",
				of : this
			});
			$(document).one("click", function() {
				$menu.hide();
			});
			return false;
		});

		//领取任务
		$('button[name=claimTask]', $zone).click(function() {
			var taskId = $(this).val();
			Ui.confirm('${wpf:lan("#:zh[是否领取群组任务?领取后该任务将成为您的个人任务,其他候选人将无法处理]:en[Get the group task?]#")}.', function() {
				Ajax.json('${_acp}/claimTask.shtml', function() {
					if ($.isFunction(Core.fn($zone, 'callback'))) {
						Core.fn($zone, 'callback')();
					}
					Ajax.post($zone, '${_acp}/form.shtml', {
						data : {
							_main : '${param._main}',
							_params : params,
							_FO : $('#${_zone}_fo', $zone).val()
						}
					});
				}, {
					data : {
						_main : '${param._main}',
						_TASK_ID : taskId
					},
					errorZone : '${_zone}_error'
				});
			});
		});

		//流程图
		$('button[name=picture]', $zone).click(function() {
			var $win = $('<div><img src="${_acp}/picture.shtml?_PD_ID=${fo.pdId}&_TASK_ID=${fo.taskId}&_ORD_ID=${fo.ordId}&_ACTIVITY_ID=${fo.activityId}"/></div>');
			$win.dialog({
				modal : true,
				title : '${wpf:lan("#:zh[流程图]:en[Flow chart]#")}',
				minWidth : 800,
				maxHeight : 600,
				buttons : [ {
					text : '${wpf:lan("#:zh[关闭]:en[Close]#")}',
					click : function() {
						$(this).dialog("close");
					}
				} ]
			}).dialogExtend({
				"closable" : true,
				"maximizable" : true,
				"minimizable" : false,
				"minimizeLocation" : 'left',
				"collapsable" : false,
				"dblclick" : 'maximize'
			});
		});

		//删除
		$('button[name=remove]', $zone).click(function() {
			var ordId = $(this).val();
			Ui.confirm('${wpf:lan("#:zh[确认删除订单?]:en[Confirm to delete the order?]#")}.', function() {
				Ajax.json('${_acp}/removeOrder.shtml', function(json) {
					Ui.msg($zone, json.msg);
					if ($.isFunction(Core.fn($zone, 'callback'))) {
						Core.fn($zone, 'callback')();
					}
				}, {
					data : {
						_main : '${param._main}',
						_ORD_ID : ordId
					},
					errorZone : '${_zone}_error'
				});
			});
		});

		//刷新按钮
		$('button[name=refresh]', $zone).click(function() {
			Ui.confirm('${wpf:lan("#:zh[(刷新)操作将不会保存您当前未提交的内容,是否继续?]:en[(Refresh)operation will not save your current uncommitted content, whether or not to continue?]#")}', function() {
				Ajax.post($zone, '${_acp}/form.shtml', {
					data : {
						_main : '${param._main}',
						_params : params,
						_FO : $('#${_zone}_fo', $zone).val(),
						ordFlag : '${ordFlag?1:0}'
					}
				});
			});
		});

		//跳到子表
		$('button[name=scrollToSub]', $zone).click(function() {
			$.scrollTo('#${_zone}_subTab_main', 500);
		});

		//子表标签
		var $subZone = $('#${_zone}_subTab_main');
		if ($subZone.size() > 0) {//存在子表标签
			Ajax.post($subZone, '${_acp}/sub.shtml', {
				data : {
					_main : '${param._main}',
					_params : params,
					_FO : $('#${_zone}_fo', $zone).val(),
					ordFlag : '0'
				},
			});

			//回写函数,直接刷新(不提示)
			Core.fn($subZone, 'callback', function() {
				if ($.isFunction(Core.fn($zone, 'callback'))) {
					Core.fn($zone, 'callback')();
				}
				Ajax.post($zone, '${_acp}/form.shtml', {
					data : {
						_main : '${param._main}',
						_params : params,
						_FO : $('#${_zone}_fo', $zone).val(),
						ordFlag : '${ordFlag?1:0}'
					},
					showFlag : false
				});
			});
		}

		//修改标题
		Ui.changeCurrentTitle($zone, '<span style="color:blue;font-weight:bold;">${_title}</span>');
	});
</script>

<%-- 客户端脚本 --%>
<wpf:javascript script="${jsScript}" type="${jsType}" context="${wcm:map(wcm:map(null,'fo',fo),'vo',vo)}" form="${_zone}_form" />

<%--查看节点菜单 --%>
<c:if test="${!ordFlag&&historyList!=null&&fn:length(historyList)>0}">
	<ul id="${_zone}_detail_menu" style="display: none; position: absolute; z-index: 9999;">
		<c:forEach items="${historyList}" var="o">
			<c:choose>
				<c:when test="${fo.activityId==o.ACTIVITY_ID}">
					<li activityId="${o.ACTIVITY_ID}" style="color: blue; font-weight: bold;"><span class="ui-icon ui-icon-check"></span>${o.ACTIVITY_NAME}</li>
				</c:when>
				<c:otherwise>
					<li activityId="${o.ACTIVITY_ID}">${o.ACTIVITY_NAME}</li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</ul>
</c:if>

<%-- 顶部bar --%>
<div class="ws-bar" id="${_zone}_button_bar">
	<div class="left ws-group">
		<c:if test="${!isCreate}">
			<button type="button" icon="circle-zoomin" name="detail">${wpf:lan("#:zh[查看]:en[View]#")}</button>
		</c:if>
		<c:if test="${task!=null&&task.assignee==null}">
			<button name="claimTask" type="button" icon="star" value="${fo.taskId}">${wpf:lan("#:zh[领取]:en[Receive]#")}</button>
		</c:if>
		<c:if test="${vo!=null&&wflow:checkRemove(vo)}">
			<button type="button" icon="trash" name="remove" value="${fo.ordId}">${wpf:lan("#:zh[删除]:en[Delete]#")}</button>
		</c:if>
	</div>
	<div class="right ws-group">
		<c:if test="${!ordFlag}">
			<c:choose>
				<c:when test="${historyList!=null&&fn:length(historyList)>0}">
					<button type="button" icon="flag" name="showDetailMenu" secicon="triangle-1-s">${wpf:lan("#:zh[查看节点]:en[View point]#")}</button>
				</c:when>
				<c:otherwise>
					<button type="button" icon="flag" name="showDetailMenu" secicon="triangle-1-s" disabled="disabled">${wpf:lan("#:zh[查看节点]:en[View point]#")}</button>
				</c:otherwise>
			</c:choose>
		</c:if>
		<button icon="image" text="true" type="button" name="picture">${wpf:lan("#:zh[流程图]:en[Flow Chart]#")}</button>
		<button icon="refresh" text="true" type="button" name="refresh">${wpf:lan("#:zh[刷新]:en[Refresh]#")}</button>
		<c:if test="${!isCreate&&config.subs!=null&&fn:length(config.subs)>0}">
			<button icon="arrowthick-1-s" text="true" type="button" name="scrollToSub">${wpf:lan("#:zh[底部]:en[Bottom]#")}</button>
		</c:if>
		<button type="button" icon="closethick" text="true" onclick="Ui.closeCurrent('${_zone}')">${wpf:lan("#:zh[关闭]:en[Close]#")}</button>
	</div>
</div>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form name="flow" action="${_acp}/submit.shtml" id="${_zone}_form" method="post" sync="true" onsubmit="$('button[name=save]',$(this)).click();return false;">
	<textarea style="display: none;" name="_FO" id="${_zone}_fo">${wcm:json(fo)}</textarea>
	<textarea style="display: none;" id="${_zone}_params" name="_params">${param._params}</textarea>
	<input type="hidden" name="_INIT" value="1" />
	<%-- 订单系统内置字段,不需要表单传递值 --%>
	<c:forEach items="${ordKeys}" var="ordKey">
		<input type="hidden" name="${ordKey.column.name}_" value="true">
	</c:forEach>

	<table class="ws-table" col="${config.table.col}" group="true">
		<c:forEach items="${columns}" var="field">
			<c:choose>
				<c:when test="${field.whole==null}">
					<%--分割线 --%>
					<c:choose>
						<%-- 节点字段,采用decide决定结果 --%>
						<c:when test="${field.decideScript!=null}">
							<c:set var="decideResult" value="${wpf:script(field.decideType,field.decideScript,context)}" />
						</c:when>
						<%-- 继承字段,直接展示--%>
						<c:otherwise>
							<c:set var="decideResult" value="${true}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${field.name==null&&decideResult}">
						<tr whole="true" group="true" show="${field.expandFlag==0?'false':'true'}">
							<th colspan="${config.table.col*2}">${wpf:lan(field.busiName)}</th>
							<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
								<td>${wpf:script(field.tipType,field.tipScript,context)}</td>
							</c:if>
						</tr>
					</c:if>
				</c:when>
				<c:when test="${field.name!=null}">
					<%-- 表单字段 --%>
					<c:choose>
						<%-- 节点字段和继承字段,采用decide角色结果 --%>
						<c:when test="${field.decideScript!=null}">
							<c:set var="decideResult" value="${wpf:script(field.decideType,field.decideScript,context)}" />
							<c:set var="editDecideResult" value="${wpf:script(field.editDecideType,field.editDecideScript,context)}" />
						</c:when>
						<c:otherwise>
							<c:set var="decideResult" value="${false}" />
							<c:set var="editDecideResult" value="${false}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${decideResult}">
						<tr whole="${field.whole==1}" self="${field.whole==2}">
							<th>${wpf:lan(field.busiName)}<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
									<br />
									<span style="color: red; font-weight: bold; cursor: help;" tip="true" title="${wpf:script(field.tipType,field.tipScript,context)}">${wpf:lan("#:zh[(提示)]:en[(TIPS)]#")}</span>
								</c:if>
							</th>
							<td><wcm:widget name="${field.name}" cmd="${field.widget}" value="${wpf:script(field.contentType,field.contentScript,context)}" state="${editDecideResult?'normal':'readonly'}"
									params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}">${wpf:lan("#:zh[不支持命令]:en[Do not support the command]#")}</wcm:widget></td>
						</tr>
					</c:if>
				</c:when>
				<c:otherwise>
					<%-- 展示字段,继承则直接展示 --%>
					<tr whole="${field.whole==1}" self="${field.whole==2}">
						<th>${wpf:lan(field.busiName)}</th>
						<td class="left"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
					</tr>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</table>
</form>

<div class="ws-bar" name="${_zone}_buttons_zone">
	<c:forEach items="${fn:split('center,left,right',',')}" var="styleClass">
		<div class="${styleClass} ws-group">
			<c:forEach items="${btns}" var="btn">
				<c:if test="${btn.styleClass==styleClass}">
					<c:set var="checkResult" value="${btn.checkScript!=null?(wpf:script(btn.checkType,btn.checkScript,context)):true}" />
					<c:choose>
						<c:when test="${btn.name=='forward'}">
							<c:if test="${wpf:checkAdmin()||(task!=null&&task.assignee!=null&&checkResult)}">
								<%-- 转办 --%>
								<button type="button" icon="${btn.icon}" name="${btn.name}" value="${btn.btnKey}">${wpf:lan(btn.busiName)}</button>
							</c:if>
						</c:when>
						<c:when test="${btn.name=='save'}">
							<c:if test="${wpf:checkAdmin()||((task==null||task.assignee!=null)&&checkResult)}">
								<%-- 保存 --%>
								<button type="button" icon="${btn.icon}" name="${btn.name}" loading="${btn.loading}">${wpf:lan(btn.busiName)}</button>
							</c:if>
						</c:when>
						<c:when test="${btn.name=='start'}">
							<%-- 启动 --%>
							<c:set var="confirmResult" value="${btn.confirmScript!=null?(wpf:script(btn.confirmType,btn.confirmScript,context)):''}" />
							<button type="button" icon="${btn.icon}" name="${btn.name}" confirmResult="${confirmResult}" opinionFlag="${btn.opinionFlag}" loading="${btn.loading}">${wpf:lan(btn.busiName)}</button>
						</c:when>
						<c:otherwise>
							<%-- 工作流按钮 --%>
							<c:set var="confirmResult" value="${btn.confirmScript!=null?(wpf:script(btn.confirmType,btn.confirmScript,context)):''}" />
							<c:choose>
								<%-- 允许展示 --%>
								<c:when test="${checkResult}">
									<c:set var="tip" value="${btn.enabledTipScript!=null?(wpf:script(btn.enabledTipType,btn.enabledTipScript,context)):null}" />
								</c:when>
								<c:otherwise>
									<c:set var="tip" value="${btn.disabledTipScript!=null?(wpf:script(btn.disabledTipType,btn.disabledTipScript,context)):null}" />
								</c:otherwise>
							</c:choose>
							<span title="${tip}">
								<button type="button" icon="${btn.icon}" name="submitTask" sequenceFlowId="${btn.flowId}" sequenceFlowName="${wpf:lan(btn.busiName)}" title="${tip}" tipFlag="${tip!=''&&tip!=null}"
									checkResult="${checkResult}" confirmResult="${confirmResult}" opinionFlag="${btn.opinionFlag}" loading="${btn.loading}">${wpf:lan(btn.busiName)}</button>
							</span>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
		</div>
	</c:forEach>
</div>

<c:if test="${!isCreate&&config.subs!=null&&fn:length(config.subs)>0}">
	<div id="${_zone}_subTab_main"></div>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>