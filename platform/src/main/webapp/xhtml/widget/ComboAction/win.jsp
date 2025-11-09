<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//设置函数
		//查询
		Core.fn($zone, 'query', function() {
			$('#${_zone}_list_form').submit();
		});

		//搜索栏收缩
		$('button[name=expand]', $zone).click(function() {
			var $this = $(this);
			var $table = $this.parents('table:first');
			if ($('tr:hidden:not(.last-child)', $table).size() < 1) {
				$('tr:not(.last-child)', $table).hide('fast');
			} else {
				$('tr:not(.last-child)', $table).show('fast');
			}
		});

		//调用函数
		var querySize = new Number('${fn:length(config.querys)}');
		var initQuery = new Number('${config.initQuery}');
		if (querySize > 0 && initQuery == 0) {//被动查询模式
			//do nothing
		} else if (initQuery == 1) {//主动查询(收缩)
			$('button[name=expand]', $zone).click();
			Core.fn($zone, 'query')();
		} else {//主动查询(展开)
			Core.fn($zone, 'query')();
		}
	});
</script>

<%-- 客户端脚本 --%>
<wpf:javascript script="${config.jsScript}" type="${config.jsType}" form="${_zone}_list_form" />

<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
	<textarea style="display: none;" name="widgetKey">${param.widgetKey}</textarea>
	<textarea style="display: none;" name="_params">${param._params}</textarea>
	<input type="hidden" value="${pageLimit}" name="_limit" />

	<%--查询条件 --%>
	<c:if test="${fn:length(config.querys)>0}">
		<table class="ws-table" col="2">
			<c:forEach items="${config.querys}" var="vo">
				<tr>
					<th>${wpf:lan(vo.busiName)}</th>
					<c:choose>
						<c:when test="${vo.name!=null&&vo.name!=''}">
							<c:set var="queryFormName" value="${vo.name}" />
						</c:when>
						<c:otherwise>
							<c:set var="queryFormName" value="querys.${vo.id}" />
						</c:otherwise>
					</c:choose>
					<td><wcm:widget name="${queryFormName}" cmd="${vo.widget}" value="${vo.defVal}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}" /></td>
				</tr>
			</c:forEach>
			<tr whole="true">
				<th class="ws-bar">
					<div class="ws-group left">
						<button type="button" icon="arrowthick-2-n-s" name="expand">${wpf:lan("#:zh[展开/收缩查询框]:en[Expansion/contraction query box]#")}</button>
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
<div id="${_zone}_msg" name="errorZone"></div>

<%--查询结果 --%>
<div id="${_zone}_list">
	<div class="ws-msg normal">${wpf:lan("#:zh[请输入查询条件并点击(查询)按钮.]:en[Please enter the query  and click on the (Query)button]#")}</div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>