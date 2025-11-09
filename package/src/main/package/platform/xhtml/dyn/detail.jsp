<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(wcm:map(null,'mode',2),'vo',vo)}" />
<%-- 获取left join绑定上下文 --%>
<c:forEach items="${config.table.parents}" var="parent">
	<c:set var="context" value="${wcm:map(context,parent.var,wpf:pixelVO(parent.var,vo))}" />
</c:forEach>
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $main, $msg, $tabs;
		if ('${param._main}' != '') {
			$main = $('#${param._main}');
			$msg = $('[name=mainMsgZone]', $main);
			$tabs = $('div[tabs=true]:first', $main);
		} else {
			if ('${param._list}' != '') {
				$main = $('#${param._list}');
			} else {
				$main = null;
			}
			$msg = $('#${_zone}_msg');
			$tabs = null;
		}

		//跳到子表
		$('button[name=scrollToSub]', $zone).click(function() {
			$.scrollTo('#${_zone}_subTab_main', 500);
		});

		//刷新按钮
		$('button[name=refresh]', $zone).click(function() {
			Ajax.post('${_zone}', '${_acp}/detail.shtml', {
				data : {
					_params : $('#${_zone}_params').val(),
					_main : '${param._main}',
					_list : '${param._list}',
					_key : $('#${_zone}_key').val()
				},
				showFlag : false
			});
		});

		//编辑按钮
		$('button[name=edit]', $zone).click(function() {
			var $tab;
			if ($tabs != null) {
				$tab = Ajax.tab($tabs, '${_acp}/updateZone.shtml', {
					title : '${wpf:lan(title)}[${wpf:lan("#:zh[编辑]:en[Edit]#")}]',
					data : {
						_params : $('#${_zone}_params').val(),
						_main : '${param._main}',
						_list : '${param._list}',
						_key : $('#${_zone}_key').val()
					}
				});
			} else if ($zone.attr('win') == 'true') {
				$tab = Ajax.win('${_acp}/updateZone.shtml', {
					title : '${wpf:lan(title)}[${wpf:lan("#:zh[编辑]:en[Edit]#")}]',
					minWidth : 1024,
					data : {
						_params : $('#${_zone}_params').val(),
						_main : '${param._main}',
						_list : '${param._list}',
						_key : $('#${_zone}_key').val()
					}
				});
			} else {
				$tab = Ajax.post($zone, '${_acp}/updateZone.shtml', {
					data : {
						_params : $('#${_zone}_params').val(),
						_main : '${param._main}',
						_list : '${param._list}',
						_key : $('#${_zone}_key').val()
					}
				});
			}
			Core.fn($tab, 'callback', function() {
				if ($.isFunction(Core.fn($zone, 'callback'))) {
					Core.fn($zone, 'callback')();
				}
			});
			Ui.closeCurrent('${_zone}');
		});

		var $subZone = $('#${_zone}_subTab_main');
		if ($subZone.size() > 0) {//存在子表标签
			Ajax.post($subZone, '${_acp}/sub.shtml', {
				data : {
					_params : $('#${_zone}_params').val(),
					_main : '${param._main}',
					_list : '${param._list}',
					_key : $('#${_zone}_key').val()
				}
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

<%-- 内置传参 --%>
<textarea style="display: none;" id="${_zone}_params">${param._params}</textarea>
<textarea style="display: none;" id="${_zone}_key">${wcm:jsonKey(vo,config.keysArray)}</textarea>

<%-- 顶部按钮栏 --%>
<div class="ws-bar">
	<div class="left ws-group">
		<c:if test="${wpf:checkExt(config.updateBtn.pri,context)}">
			<button type="button" icon="${config.updateBtn.icon}" name="${config.updateBtn.name}">${wpf:lan(config.updateBtn.busiName)}</button>
		</c:if>
	</div>
	<div class="right">
		<span class="ws-group">
			<button icon="refresh" text="true" type="button" name="refresh">${wpf:lan("#:zh[刷新]:en[Refresh]#")}</button> <c:if test="${config.subs!=null&&fn:length(config.subs)>0}">
				<button icon="arrowthick-1-s" text="true" type="button" name="scrollToSub">${wpf:lan("#:zh[底部]:en[Bottom]#")}</button>
			</c:if>
			<button type="button" icon="closethick" text="true" onclick="Ui.closeCurrent('${_zone}')">${wpf:lan("#:zh[关闭]:en[Close]#")}</button>
		</span>
	</div>
</div>

<c:if test="${fn:length(config.detailFields)>0}">
	<table class="ws-table" col="${config.table.col}" group="true">
		<c:forEach items="${config.detailFields}" var="field">
			<c:choose>
				<c:when test="${field.whole==null}">
					<%--分割线 --%>
					<c:if test="${field.name==null&&wpf:checkExt(field.pri,context)}">
						<tr whole="true" group="true" show="${field.expandFlag==0?'false':'true'}">
							<th colspan="${config.table.col*2}">${wpf:lan(field.busiName)}</th>
							<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
								<td>${wpf:script(field.tipType,field.tipScript,context)}</td>
							</c:if>
						</tr>
					</c:if>
				</c:when>
				<c:otherwise>
					<%-- 展示和固定 --%>
					<c:if test="${wpf:checkExt(field.pri,context)}">
						<tr whole="${field.whole==1}" self="${field.whole==2}">
							<th>${wpf:lan(field.busiName)}</th>
							<td class="left" style="${wcm:widget('style[min-width;text-align]',field.style)}"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
						</tr>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</table>
</c:if>

<c:if test="${config.subs!=null&&fn:length(config.subs)>0}">
	<div id="${_zone}_subTab_main"></div>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>