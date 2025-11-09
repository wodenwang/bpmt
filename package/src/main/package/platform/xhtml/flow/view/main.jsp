<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $main, $tabs;
		if ('${param._main}' != '') {
			$main = $('#${param._main}');
			$tabs = $('div[tabs=true]:first', $main);
		} else {
			$main = $zone;
			$tabs = null;
		}
		var $msg = $('div[name=mainMsgZone]:first', $main);

		//回调函数
		var invokeCallback = function() {
			//判断该区域是否有回调函数
			if ($.isFunction(Core.fn($zone, 'callback'))) {
				Core.fn($zone, 'callback')();
			} else {
				$("#${_zone}_list_form").submit();
			}
		};

		Core.fn('${_zone}_list', 'callback', invokeCallback);

		//查询框展开/收缩
		$('button[name=expand]', $zone).click(function() {
			var $this = $(this);
			var $table = $this.parents('table:first');
			if ($('tr:hidden:not(.last-child)', $table).size() < 1) {
				$('tr:not(.last-child)', $table).hide('fast');
			} else {
				$('tr:not(.last-child)', $table).show('fast');
			}
		});

		//初始化模式
		{
			var querySize = new Number('${fn:length(config.querys)}');
			var initQuery = new Number('${config.table.initQuery}');

			if (querySize > 0 && initQuery == 0) {//被动查询模式

			} else if (initQuery == 1) {
				$('tr:not(.last-child)', $('#${_zone}_list_form table')).hide();
				$("#${_zone}_list_form").submit();
			} else {
				$("#${_zone}_list_form").submit();
			}
		}

	});
</script>

<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
	<textarea style="display: none;" name="_params" id="${_zone}_params">${param._params}</textarea>
	<textarea style="display: none;" name="_main">${param._main}</textarea>
	<input type="hidden" name="_limit" value="${pageLimit}" />
	<c:if test="${param.type!=null}">
		<textarea style="display: none;" name="type">${param.type}</textarea>
	</c:if>
	<%--查询条件 --%>
	<c:if test="${config.querys !=null && fn:length(config.querys)>0}">
		<table class="ws-table" col="2">
			<c:forEach items="${config.querys}" var="vo">
				<tr>
					<th>${wpf:lan(vo.busiName)}</th>
					<td><c:choose>
							<c:when test="${vo.name!=null}">
								<wcm:widget name="${vo.name}" cmd="${vo.widget}" value="${vo.defVal}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}"></wcm:widget>
							</c:when>
							<c:otherwise>
								<wcm:widget name="querys.${vo.id}" cmd="${vo.widget}" value="${vo.defVal}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}"></wcm:widget>
							</c:otherwise>
						</c:choose></td>
				</tr>
			</c:forEach>
			<tr whole="true">
				<th class="ws-bar">
					<div class="ws-group left">
						<button type="button" icon="arrowthick-2-n-s" name="expand" text="true">${wpf:lan("#:zh[查询条件]:en[Query criteria]#")}</button>
						<button type="reset" icon="arrowreturnthick-1-w" text="true">${wpf:lan("#:zh[重置条件]:en[Reset query]#")}</button>
					</div>
					<div class="ws-group right">
						<button type="submit" icon="search" text="true">${wpf:lan("#:zh[查询]:en[Query]#")}</button>
					</div>
				</th>
			</tr>
		</table>
	</c:if>
</form>

<%--错误提示区域 --%>
<div id="${_zone}_msg" name="mainMsgZone"></div>

<%--查询结果 --%>
<div id="${_zone}_list">
	<div class="ws-msg normal">${wpf:lan("#:zh[请输入查询条件并点击(查询)按钮.]:en[Please enter the query  and click on the (Query)button]#")}</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>