<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//设置函数
		//查询,顶层回调
		Core.fn($zone, 'callback', function() {
			$('#${_zone}_list_form').submit();
		});

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
				Core.fn($zone, 'callback')();
			} else {
				Core.fn($zone, 'callback')();
			}
		}

	});
</script>

<%-- 客户端脚本 --%>
<wpf:javascript script="${config.table.listJsScript}" type="${config.table.listJsType}" form="${_zone}_list_form" />

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<%-- 非QUERY模式时,不展示默认tab --%>
	<div title="${wpf:lan(title)}">
		<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
			<textarea style="display: none;" name="_params" id="${_zone}_params">${param._params}</textarea>
			<textarea style="display: none;" name="_main">${_zone}</textarea>
			<input type="hidden" name="_limit" value="${pageLimit}" />
			<%--查询条件 --%>
			<c:if test="${config.querys !=null && fn:length(config.querys)>0}">
				<table class="ws-table" col="2">
					<c:forEach items="${config.querys}" var="vo">
						<tr>
							<th>${wpf:lan(vo.busiName)}</th>
							<td>
							<c:set var="queryFormName" value="querys.${vo.id}" />
						        <c:if test="${vo.name!=null&&vo.name!=''}">
							    <c:set var="queryFormName" value="${vo.name}" />
						        </c:if>
							    <wcm:widget name="${queryFormName}" cmd="${vo.widget}" value="${vo.defVal}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}"></wcm:widget>
                            </td>
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

	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>