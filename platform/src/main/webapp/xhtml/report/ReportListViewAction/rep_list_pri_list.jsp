<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$.each($('td[colindex] :checkbox', $zone), function() {
			var $checkbox = $(this);
			var $td = $checkbox.parent();
			$td.addClass("ws-checkbox");

			$checkbox.on('change', function() {
				if ($(this).prop("checked")) {
					$td.addClass("ui-state-focus");
					$td.next().addClass("ui-state-focus");
				} else {
					$td.removeClass("ui-state-focus");
					$td.next().removeClass("ui-state-focus");
				}
			});
			var checkstate = $checkbox.attr('checkstate');
			if (checkstate != undefined && checkstate == 'true') {
				$checkbox.click();
			}
		});

		$.each($('th[colindex] :checkbox', $zone), function() {
			var $checkbox = $(this);
			var $th = $checkbox.parent();
			$th.addClass("ws-checkbox");

			var colindex = $th.attr('colindex');
			$checkbox.click(function() {
				if ($(this).prop("checked")) {
					$("td[colindex='" + colindex + "'] input:unchecked", $zone).click();
				} else {
					$("td[colindex='" + colindex + "'] input:checked", $zone).click();
				}
			});
		});
	});
</script>

<input type="hidden" name="groupId" value="${param.groupId}" />

<div accordion="true" multi="true">
	<div title="按钮权限">
		<table class="ws-table">
			<tr>
				<th></th>
				<th style="width: 4em;">预览</th>
				<th colindex="sys_btn"><input type="checkbox" /></th>
				<th>特殊设置</th>
			</tr>
			<c:forEach items="${table.sysBtns}" var="vo">
				<tr>
					<th>${vo.busiName}</th>
					<td class="center"><button type="button" icon="${vo.icon}" text="false">${vo.busiName}</button></td>
					<td colindex="sys_btn"><c:if test="${vo.pri != null && (!vo.pri.scriptOnly)}">
							<input type="checkbox" value="${vo.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,vo.pri.priKey)}" />
						</c:if></td>
					<td class="right"><c:if test="${vo.pri != null}">
							<wcm:widget name="relate" cmd="prigroup[${groupId};${vo.pri.priKey}${vo.type==1?';vo:实体':''}]" />
						</c:if></td>
				</tr>
			</c:forEach>
			<c:forEach items="${table.itemBtns}" var="vo">
				<tr>
					<th>${vo.busiName}</th>
					<td class="center"><button type="button" icon="${vo.icon}" text="false">${vo.busiName}</button></td>
					<td colindex="sys_btn"><c:if test="${!vo.pri.scriptOnly}">
							<input type="checkbox" value="${vo.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,vo.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${vo.pri.priKey};vo:实体]"></wcm:widget></td>
				</tr>
			</c:forEach>
			<c:forEach items="${table.summaryBtns}" var="vo">
				<tr>
					<th>${vo.busiName}</th>
					<td class="center"><button type="button" icon="${vo.icon}" text="false">${vo.busiName}</button></td>
					<td colindex="sys_btn"><c:if test="${!vo.pri.scriptOnly}">
							<input type="checkbox" value="${vo.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,vo.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${vo.pri.priKey}]"></wcm:widget></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div title="字段权限">
		<table class="ws-table">
			<tr>
				<th colspan="4">字段</th>
			</tr>
			<tr>
				<th></th>
				<th colindex="field"><input type="checkbox" /></th>
				<th>特殊设置</th>
			</tr>
			<c:forEach items="${fields}" var="field">
				<tr>
					<td class="center"><c:choose>
							<c:when test="${fn:endsWith(field['$type$'],'Form')}">
								<img src="${_cp}/css/icon/application_form.png" tip="true" title="表单字段" />
							</c:when>
							<c:when test="${fn:endsWith(field['$type$'],'Line')}">
								<img src="${_cp}/css/icon/bookmark.png" tip="true" title="分割线字段" />
							</c:when>
							<c:when test="${fn:endsWith(field['$type$'],'Show')}">
								<img src="${_cp}/css/icon/application.png" tip="true" title="展示字段" />
							</c:when>
							<c:otherwise>
								<img src="${_cp}/css/icon/table.png" tip="true" title="固定字段" />
							</c:otherwise>
						</c:choose>${field.busiName}</td>
					<td colindex="field"><c:if test="${!field.pri.scriptOnly}">
							<input type="checkbox" value="${field.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,field.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${field.pri.priKey}]" /></td>

				</tr>
			</c:forEach>
		</table>
	</div>
	<div title="子表权限">
		<table class="ws-table">
			<tr>
				<th></th>
				<th colindex="sub"><input type="checkbox" /></th>
				<th>特殊设置</th>
			</tr>
			<c:forEach items="${table.viewSubs}" var="vo">
				<tr>
					<th>${vo.busiName}</th>
					<td colindex="sub"><c:if test="${!vo.pri.scriptOnly}">
							<input type="checkbox" value="${vo.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,vo.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${vo.pri.priKey};vo:实体]"></wcm:widget></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div title="其他">
		<table class="ws-table">
			<tr>
				<th colspan="4">数据筛选权限</th>
			</tr>
			<tr>
				<th></th>
				<th colindex="limit"><input type="checkbox" /></th>
				<th>特殊设置</th>
			</tr>
			<c:forEach items="${table.limits}" var="vo">
				<tr>
					<th>${vo.description}</th>
					<td colindex="limit"><c:if test="${!vo.pri.scriptOnly}">
							<input type="checkbox" value="${vo.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,vo.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${vo.pri.priKey}]" /></td>
				</tr>
			</c:forEach>
		</table>
		<table class="ws-table">
			<tr>
				<th colspan="4">微信企业号</th>
			</tr>
			<tr>
				<th></th>
				<th colindex="weixin"><input type="checkbox" /></th>
				<th>特殊设置</th>
			</tr>
			<tr>
				<th>微信端权限</th>
				<td colindex="weixin"><c:if test="${!table.weixin.pri.scriptOnly}">
						<input type="checkbox" value="${table.weixin.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,table.weixin.pri.priKey)}" />
					</c:if></td>
				<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${table.weixin.pri.priKey}]"></wcm:widget></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>