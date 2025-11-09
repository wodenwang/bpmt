<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<wwx:jssdk url="${_full_url}" />

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

<script>
	$(function() {

		//增加loading
		window.onbeforeunload = function() {
			Wxui.showLoading();
		};

		//跳转
		$("#${_zone}_btn_home").on('click', function(event) {
			window.location.href = "${_acp}/list.shtml?_params=${wcm:urlEncode(param._params)}";
		});

		$("#${_zone}_flow_picture_btn").on('click', function() {
			$("#${_zone}_flow_picture").modal();
		});

		//删除
		$("#${_zone}_delete_btn").on('click', function(event) {
			Wxui.confirm('确认删除订单[${vo.ORD_ID}]?', function() {
				Wxui.json("${_acp}/removeOrder.shtml?_FO=${wcm:urlEncode(wcm:json(fo))}&_params=${wcm:urlEncode(param._params)}", function(json) {
					Wxui.alert(json.msg, function() {
						window.location.href = "${_acp}/list.shtml?_params=${wcm:urlEncode(param._params)}";
					});
				});
			});
		});

		//表单处理
		var $form = $("#${_zone}_form");

		//禁止回车提交
		$form.on("submit", function(e) {
			return false;
		});

		var $flowInit = $('#${_zone}_init');
		var $sequenceFlowId = $('#${_zone}_flowId');
		var $sequenceFlowName = $('#${_zone}_flowName');
		var $opinion = $('#${_zone}_opinion');

		//提交表单
		var submitForm = function() {
			Wxui.form($form, function(json) {
				Wxui.alert('订单保存成功.', function() {
					window.location.href = "${_acp}/detail.shtml?ordFlag=${ordFlag?1:0}&_params=${wcm:urlEncode(param._params)}&_ORD_ID=" + json.ordId + "&_ACTIVITY_ID=" + json.activityId;
				});
			});
		};

		//保存
		$('#${_zone}_btn_zone [name=save]').click(function() {
			//先验证
			if (!$form.validator('isFormValid')) {
				return;
			}
			//保存需要把流程fo信息清理掉
			$flowInit.val('0');
			$opinion.val('');
			$sequenceFlowId.val('');
			$sequenceFlowName.val('');
			submitForm();
		});

		//保存并启动流程
		$('#${_zone}_btn_zone [name=start]').click(function() {
			//先验证
			if (!$form.validator('isFormValid')) {
				return;
			}

			var $btn = $(this);
			$flowInit.val('1');
			$opinion.val('');

			var confirmMsg = $btn.attr("confirmResult");
			if (confirmMsg == null || confirmMsg == '') {
				confirmMsg = '确认启动流程?';
			}

			var opinionFlag = $btn.attr('opinionFlag');
			var opinionTableName = '${config.table.opinionTableName}';
			if (opinionFlag == '1' && opinionTableName != '') {//需要弹出意见
				$("#${_zone}_opinion_win .am-modal-bd span").html(confirmMsg);
				$("#${_zone}_opinion_win .am-modal-prompt-input").val($btn.text());
				$('#${_zone}_opinion_win').modal({
					relatedTarget : this,
					closeViaDimmer : false,
					onConfirm : function(e) {
						$opinion.val(e.data);
						submitForm();
					}
				});

			} else {
				Wxui.confirm(confirmMsg, function() {
					submitForm();
				});
			}
		});

		//工作流处理
		$('#${_zone}_btn_zone [name=submitTask]').click(function() {
			//先验证
			if (!$form.validator('isFormValid')) {
				return;
			}

			var $btn = $(this);
			$flowInit.val('1');
			$sequenceFlowId.val($btn.attr('sequenceFlowId'));
			$sequenceFlowName.val($btn.attr('sequenceFlowName'));
			$opinion.val('');

			var confirmMsg = $btn.attr("confirmResult");
			if (confirmMsg == null || confirmMsg == '') {
				confirmMsg = '确认提交[' + $sequenceFlowName.val() + ']操作?';
			}

			var opinionFlag = $btn.attr('opinionFlag');
			var opinionTableName = '${config.table.opinionTableName}';
			if (opinionFlag == '1' && opinionTableName != '') {//需要弹出意见
				$("#${_zone}_opinion_win .am-modal-bd span").html(confirmMsg);
				$("#${_zone}_opinion_win .am-modal-prompt-input").val($btn.text());
				$('#${_zone}_opinion_win').modal({
					relatedTarget : this,
					closeViaDimmer : false,
					onConfirm : function(e) {
						$opinion.val(e.data);
						submitForm();
					}
				});

			} else {
				Wxui.confirm(confirmMsg, function() {
					submitForm();
				});
			}
		});

		//领取任务
		$('#${_zone}_btn_zone [name=claimTask]').click(function() {
			var taskId = $(this).attr('taskId');
			Wxui.confirm('是否领取群组任务?领取后该任务将成为您的个人任务,其他候选人将无法处理.', function() {
				Wxui.json('${_acp}/claimTask.shtml?_TASK_ID=' + taskId, function(json) {
					Wxui.alert(json.msg, function() {
						window.location.reload(true);
					});
				});
			});
		});

		//绑定提示
		$.each($('#${_zone}_btn_zone [name=showTip]'), function() {
			var $btn = $(this);
			var tip = $btn.val();
			if (tip) {
				$btn.popover({
					content : tip
				});
			}
		});

		//删除第一个分割线
		$('li.am-divider:first', $('ul.am-dropdown-content')).remove();
		if ($('header .am-header-right.am-header-nav ul.am-dropdown-content li').size() < 1) {
			$('header .am-header-right.am-header-nav').hide();
		}
	});
</script>

<%-- 流程图 --%>
<div class="am-modal am-modal-no-btn" tabindex="-1" id="${_zone}_flow_picture">
	<div class="am-modal-dialog">
		<div class="am-modal-hd">
			流程图 <a href="javascript: void(0)" class="am-close am-close-spin" data-am-modal-close>&times;</a>
		</div>
		<div class="am-modal-bd">
			<figure data-am-widget="figure" class="am am-figure am-figure-default " data-am-figure="{pureview: true}">
				<img src="${_acp}/picture.shtml?_PD_ID=${fo.pdId}&_TASK_ID=${fo.taskId}&_ORD_ID=${fo.ordId}&_ACTIVITY_ID=${fo.activityId}&imageType=png" alt="点击看大图" />
			</figure>
		</div>
	</div>
</div>

<%-- 意见框 --%>
<div class="am-modal am-modal-prompt" tabindex="-1" id="${_zone}_opinion_win">
	<div class="am-modal-dialog">
		<div class="am-modal-hd">填写意见</div>
		<div class="am-modal-bd">
			<span></span>
			<textarea class="am-modal-prompt-input"></textarea>
		</div>
		<div class="am-modal-footer">
			<span class="am-modal-btn" data-am-modal-cancel>取消</span> <span class="am-modal-btn" data-am-modal-confirm>确认</span>
		</div>
	</div>
</div>

<%-- 回到顶部 --%>
<div data-am-widget="gotop" class="am-gotop am-gotop-fixed">
	<a href="#top" title="回到顶部"> <span class="am-gotop-title">回到顶部</span> <i class="am-gotop-icon am-icon-chevron-up"></i>
	</a>
</div>

<%--顶部--%>
<header data-am-widget="header" class="am-header am-header-default">
	<div class="am-header-left am-header-nav">
		<a href="javascript:void(0);" class="" id="${_zone}_btn_home"><i class="am-header-icon am-icon-home"></i></a>
	</div>
	<h1 class="am-header-title">${_title}</h1>
	<c:if test="${!isCreate}">
		<div class="am-header-right am-header-nav " data-am-dropdown>
			<a href="###" class="am-dropdown-toggle"><i class="am-header-icon am-icon-bars"></i></a>
			<ul class="am-dropdown-content">
				<li class="am-divider"></li>
				<li><a href="${_acp}/detail.shtml?ordFlag=${ordFlag?1:0}&_params=${wcm:urlEncode(param._params)}&_FO=${wcm:urlEncode(wcm:json(fo))}"><i
						class="am-icon-file-text-o am-icon-fw am-margin-right-xs"></i>查看</a></li>

				<c:forEach items="${subs}" var="sub" varStatus="status">
					<%-- 权限空白,直接可看 --%>
					<c:if test="${sub.pri==null||wpf:checkExt(sub.pri,context)}">
						<c:choose>
							<%-- 视图子 --%>
							<c:when test="${sub.action!=null}">
							</c:when>
							<%-- 流程图 --%>
							<c:when test="${sub.name=='picture'}">
								<li class="am-divider"></li>
								<li><a href="javascript:void(0);" id="${_zone}_flow_picture_btn"><i class="am-icon-object-group am-icon-fw am-margin-right-xs"></i>${sub.busiName}</a></li>
							</c:when>
							<c:when test="${sub.name=='history'}">
							</c:when>
						</c:choose>
					</c:if>
				</c:forEach>
			</ul>
		</div>
	</c:if>
</header>

<%--表单 --%>
<form id="${_zone}_form" class="am-form" action="${_acp}/submit.shtml">
	<textarea style="display: none;" name="_FO" id="${_zone}_fo">${wcm:json(fo)}</textarea>
	<textarea style="display: none;" id="${_zone}_params" name="_params">${param._params}</textarea>

	<textarea style="display: none;" id="${_zone}_opinion" name="_OPINION"></textarea>
	<textarea style="display: none;" id="${_zone}_init" name="_INIT">1</textarea>
	<textarea style="display: none;" id="${_zone}_flowId" name="_FLOW_ID"></textarea>
	<textarea style="display: none;" id="${_zone}_flowName" name="_FLOW_NAME"></textarea>

	<%-- 订单系统内置字段,不需要表单传递值 --%>
	<c:forEach items="${ordKeys}" var="ordKey">
		<input type="hidden" name="${ordKey.column.name}_" value="true">
	</c:forEach>

	<div class="am-panel-group" id="${_zone}_accordion">
		<c:forEach items="${columns}" var="line" varStatus="status">
			<c:if test="${line.whole==null}">
				<%--分割线 --%>
				<c:choose>
					<%-- 节点字段,采用decide决定结果 --%>
					<c:when test="${field.decideScript!=null}">
						<c:set var="lineDecideResult" value="${wpf:script(field.decideType,field.decideScript,context)}" />
					</c:when>
					<%-- 继承字段,直接展示--%>
					<c:otherwise>
						<c:set var="lineDecideResult" value="${true}" />
					</c:otherwise>
				</c:choose>
				<c:if test="${field.name==null&&lineDecideResult}">
					<div class="am-panel am-panel-secondary">
						<div class="am-panel-hd" data-am-collapse="{target: '#${_zone}_accordion_p${status.index}'}">
							<h4 class="am-panel-title">
								${line.busiName}
								<c:if test="${line.tipScript!=null&&line.tipScript!=''}">(${wpf:script(line.tipType,line.tipScript,context)})</c:if>
							</h4>
						</div>
						<div id="${_zone}_accordion_p${status.index}" class="am-panel-collapse am-collapse ${line.expandFlag==0?'':'am-in'}">
							<div class="am-panel-bd">
								<dl>
									<c:forEach items="${columns}" var="field" varStatus="fieldStatus" begin="${line.form_begin}" end="${line.form_end}">
										<%-- 是否出现分割线 --%>
										<c:set var="hrFlag" value="${false}" />

										<c:choose>
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
													<c:set var="hrFlag" value="${true}" />
													<c:if test="${field.whole!=2}">
														<dt>${field.busiName}</dt>
													</c:if>
													<dd>
														<wcm:widget actionMode="h5" name="${field.name}" cmd="${field.widget}" value="${wpf:script(field.contentType,field.contentScript,context)}"
															state="${editDecideResult?'normal':'readonly'}" params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}" />
													</dd>
												</c:if>
											</c:when>
											<c:otherwise>
												<%-- 展示字段 --%>
												<c:set var="hrFlag" value="${true}" />
												<c:if test="${field.whole!=2}">
													<dt>${field.busiName}</dt>
												</c:if>
												<dd>
													<wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" />
												</dd>
											</c:otherwise>
										</c:choose>

										<%-- 分割线 --%>
										<c:if test="${hrFlag&&fieldStatus.index!=fieldStatus.end}">
											<hr data-am-widget="divider" style="" class="am-divider am-divider-dashed" />
										</c:if>

									</c:forEach>
								</dl>
							</div>
						</div>
					</div>
				</c:if>
			</c:if>
		</c:forEach>
	</div>

	<%-- 按钮区域,左右结构 --%>
	<div class="am-container am-margin-top am-margin-bottom am-cf" id="${_zone}_btn_zone">

		<%-- 固定功能性按钮暂时收进来 --%>
		<div class="am-dropdown am-dropdown-up" data-am-dropdown>
			<button class="am-btn am-radius am-btn-secondary am-dropdown-toggle" data-am-dropdown-toggle>
				更多 <span class="am-icon-caret-up"></span>
			</button>
			<ul class="am-dropdown-content">

				<c:if test="${task!=null&&task.assignee==null}">
					<%-- 领取 --%>
					<li class="am-divider"></li>
					<li><a name="claimTask" href="javascript:void(0);" taskId="${fo.taskId}"><i class="am-icon-hand-stop-o am-icon-fw am-margin-right-xs"></i>领取</a></li>
				</c:if>

				<c:forEach items="${btns}" var="btn">
					<c:set var="checkResult" value="${btn.checkScript!=null?(wpf:script(btn.checkType,btn.checkScript,context)):true}" />
					<c:choose>
						<c:when test="${btn.name=='forward'}">
							<c:if test="${wpf:checkAdmin()||(task!=null&&task.assignee!=null&&checkResult)}">
								<%-- 转办 --%>
								<%-- TODO 转发暂不支持 --%>
								<c:if test="${false}">
									<li class="am-divider"></li>
									<li><a name="${btn.name}" value="${btn.btnKey}" href="javascript:void(0);"><i class="am-icon-reply am-icon-fw am-margin-right-xs"></i>${btn.busiName}</a></li>
								</c:if>
							</c:if>
						</c:when>
						<c:when test="${btn.name=='save'}">
							<c:if test="${wpf:checkAdmin()||((task==null||task.assignee!=null)&&checkResult)}">
								<%-- 保存 --%>
								<li class="am-divider"></li>
								<li><a name="${btn.name}" href="javascript:void(0);"><i class="am-icon-save am-icon-fw am-margin-right-xs"></i>${btn.busiName}</a></li>
							</c:if>
						</c:when>
					</c:choose>
				</c:forEach>

				<c:if test="${vo!=null&&wflow:checkRemove(vo)}">
					<li class="am-divider"></li>
					<li><a href="javascript:void(0);" id="${_zone}_delete_btn" style="color: red;"><i class="am-icon-trash am-icon-fw am-margin-right-xs"></i>删除</a></li>
				</c:if>
			</ul>
		</div>

		<c:forEach items="${btns}" var="btn">
			<c:set var="checkResult" value="${btn.checkScript!=null?(wpf:script(btn.checkType,btn.checkScript,context)):true}" />
			<c:choose>
				<c:when test="${btn.name=='start'}">
					<%-- 启动 --%>
					<c:set var="confirmResult" value="${btn.confirmScript!=null?(wpf:script(btn.confirmType,btn.confirmScript,context)):''}" />
					<button type="button" class="am-btn am-btn-success am-radius am-margin-left-xs am-fr" name="${btn.name}" confirmResult="${confirmResult}" opinionFlag="${btn.opinionFlag}">${btn.busiName}</button>
				</c:when>
				<c:when test="${btn.name==''||btn.name==null}">
					<c:if test="${wpf:checkAdmin()||((task==null||task.assignee!=null)&&checkResult)}">
						<%-- 工作流按钮 --%>
						<c:choose>
							<%-- 允许展示 --%>
							<c:when test="${checkResult}">
								<button type="button" class="am-btn am-btn-success am-radius am-margin-left-xs am-fr" name="submitTask" sequenceFlowId="${btn.flowId}" sequenceFlowName="${btn.busiName}"
									confirmResult="${confirmResult}" opinionFlag="${btn.opinionFlag}">${btn.busiName}</button>
							</c:when>
							<c:otherwise>
								<c:set var="tip" value="${btn.disabledTipScript!=null?(wpf:script(btn.disabledTipType,btn.disabledTipScript,context)):null}" />
								<button type="button" class="am-btn am-radius am-margin-left-xs am-fr" name="showTip" value="${tip}">${btn.busiName}</button>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:when>
			</c:choose>
		</c:forEach>
	</div>
</form>

<%-- 客户端脚本，必须写在form表单下面，否则widget不会初始化 --%>
<wpf:javascript script="${config.table.formJsScript}" type="${config.table.formJsType}" context="${context}" form="${_zone}_form" actionMode="h5" />

<footer data-am-widget="footer" class="am-footer am-footer-default"></footer>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>