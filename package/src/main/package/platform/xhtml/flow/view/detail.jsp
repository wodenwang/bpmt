<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(wcm:map(null,'mode',2),'vo',vo)}" />
<c:set var="context" value="${wcm:map(context,'fo',fo)}" />
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<%-- 是否需要校验权限 --%>
<c:set var="ordFlag" value="${fo.activityId==null||ordFlag}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//处理订单
		$('button[name=task]', $zone).click(function() {
			var $btn = $(this);
			Ajax.post($zone, '${_acp}/form.shtml', {
				errorZone : '${_zone}_error',
				data : {
					_main : '${param._main}',
					_params : $('#${_zone}_params').val(),
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

		//跳到子表
		$('button[name=scrollToSub]', $zone).click(function() {
			$.scrollTo('#${_zone}_subTab_main', 500);
		});

		//刷新按钮
		$('button[name=refresh]', $zone).click(function() {
			Ajax.post($zone, '${_acp}/detail.shtml', {
				data : {
					_main : '${param._main}',
					_params : $('#${_zone}_params').val(),
					_FO : $('#${_zone}_fo', $zone).val(),
					ordFlag : '${ordFlag?1:0}'
				},
				showFlag : false
			});
		});

		//流程图
		$('button[name=picture]', $zone).click(function() {
			var $win = $('<div><img src="${_acp}/picture.shtml?_PD_ID=${fo.pdId}&_ORD_ID=${fo.ordId}"/></div>');
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

		//设置标题
		if ('${ordFlag?1:0}' != '1') {
			Ui.changeCurrentTitle($zone, '${pd.name}[${vo.ORD_ID}]:${fo.activityName}');
		} else {
			Ui.changeCurrentTitle($zone, '${pd.name}[${vo.ORD_ID}]');
		}

		//子表标签
		var $subZone = $('#${_zone}_subTab_main');
		if ($subZone.size() > 0) {//存在子表标签
			Ajax.post($subZone, '${_acp}/sub.shtml', {
				data : {
					_main : '${param._main}',
					_params : $('#${_zone}_params').val(),
					_FO : $('#${_zone}_fo', $zone).val(),
					ordFlag : '${ordFlag?1:0}'
				},
			});

			//回写函数
			Core.fn($subZone, 'callback', function() {
				if ($.isFunction(Core.fn($zone, 'callback'))) {
					Core.fn($zone, 'callback')();
				}
				$('button[name=refresh]', $zone).click();
			});
		}

	});
</script>


<%-- 明细刷新的表单 --%>
<textarea style="display: none;" name="_FO" id="${_zone}_fo">${wcm:json(fo)}</textarea>
<textarea style="display: none;" id="${_zone}_params" name="_params">${param._params}</textarea>

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

<div class="ws-bar">
	<div class="left ws-group">
		<c:if test="${wflow:checkTask(vo)}">
			<button type="button" icon="circle-triangle-e" name="task">${wpf:lan("#:zh[处理]:en[Handle]#")}</button>
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
		<button icon="image" text="true" type="button" name="picture">${wpf:lan("#:zh[流程图]:en[Flow chart]#")}</button>
		<button icon="refresh" text="true" type="button" name="refresh">${wpf:lan("#:zh[刷新]:en[Refresh]#")}</button>
		<c:if test="${config.subs!=null&&fn:length(config.subs)>0}">
			<button icon="arrowthick-1-s" text="true" type="button" name="scrollToSub">${wpf:lan("#:zh[底部]:en[Bottom]#")}</button>
		</c:if>
		<button type="button" icon="closethick" text="true" onclick="Ui.closeCurrent('${_zone}')">${wpf:lan("#:zh[关闭]:en[Close]#")}</button>
	</div>
</div>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<table class="ws-table" col="${config.table.col}" group="true">
	<c:forEach items="${columns}" var="field">
		<c:choose>
			<c:when test="${field.whole==null}">
				<c:set var="checkResult" value="${true}" />
				<%--分割线 --%>
				<c:choose>
					<c:when test="${ordFlag}">
						<c:set var="checkResult" value="${wpf:checkExt(field.pri,context)}" />
					</c:when>
					<%-- 节点字段,采用decide决定结果 --%>
					<c:when test="${field.decideScript!=null}">
						<c:set var="checkResult" value="${wpf:script(field.decideType,field.decideScript,context)}" />
					</c:when>
					<%-- 继承字段,直接展示--%>
					<c:otherwise>
						<c:set var="checkResult" value="${true}" />
					</c:otherwise>
				</c:choose>
				<c:if test="${checkResult}">
					<tr whole="true" group="true" show="${field.expandFlag==0?'false':'true'}">
						<th colspan="${config.table.col*2}">${wpf:lan(field.busiName)}</th>
						<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
							<td>${wpf:script(field.tipType,field.tipScript,context)}</td>
						</c:if>
					</tr>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="checkResult" value="${true}" />
				<%--展示字段 --%>
				<c:choose>
					<c:when test="${ordFlag}">
						<c:set var="checkResult" value="${wpf:checkExt(field.pri,context)}" />
					</c:when>
					<%-- 节点字段,采用decide决定结果 --%>
					<c:when test="${field.decideScript!=null}">
						<c:set var="checkResult" value="${wpf:script(field.decideType,field.decideScript,context)}" />
					</c:when>
					<%-- 继承字段,直接展示--%>
					<c:otherwise>
						<c:set var="checkResult" value="${true}" />
					</c:otherwise>
				</c:choose>
				<c:if test="${checkResult}">
					<tr whole="${field.whole==1}" self="${field.whole==2}">
						<th>${wpf:lan(field.busiName)}</th>
						<td class="left"><c:choose>
								<c:when test="${field.name==null&&field.contentScript!=null}">
									<%-- 展示字段 --%>
									<wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" />
								</c:when>
								<c:when test="${field.name!=null&&field.showContentScript!=null&&field.showContentScript!=''}">
									<%-- 表单字段,设置了showContentScript --%>
									<wpf:script script="${field.showContentScript}" type="${field.showContentType}" context="${context}" />
								</c:when>
								<c:when test="${field.name!=null}">
									<%-- 表单字段,showContentScirpt为空 --%>
									<wcm:widget name="${field.name}" cmd="${field.widget}" state="readonly" value="${wpf:script(field.contentType,field.contentScript,context)}" />
								</c:when>
							</c:choose></td>
					</tr>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</table>

<c:if test="${config.subs!=null&&fn:length(config.subs)>0}">
	<div id="${_zone}_subTab_main"></div>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>