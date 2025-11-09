<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<form action="${_acp}/inviteUser.shtml" sync="true">
	<table class="ws-table" form="${_form}">
		<tr>
			<th field="uid" style="min-width: 80px;">登录ID</th>
			<th field="busiName" style="min-width: 80px;">姓名</th>
			<th field="wxid" style="min-width: 120px;">微信ID</th>
			<th field="mobile" style="min-width: 120px;">手机号码</th>
			<th field="mail" style="min-width: 150px;">邮箱</th>
			<th field="wxStatus" style="min-width: 80px;">微信状态</th>
		</tr>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center">${vo.uid}</td>
				<td class="left"><c:choose>
						<c:when test="${vo.wxAvatar!=null}">
							<span tip="true" selector=".logo"><img src="${vo.wxAvatar}" style="width: 30px; margin-bottom: -8px;" />${vo.busiName}<div class="logo">
									<img src="${vo.wxAvatar}" style="width: 100px;" />
								</div></span>
						</c:when>
						<c:otherwise>${vo.busiName}</c:otherwise>
					</c:choose></td>
				<td class="left">${vo.wxid}</td>
				<td class="center">${vo.mobile}</td>
				<td class="left">${vo.mail}</td>
				<td class="center"><c:choose>
						<c:when test="${vo.wxEnable!=1}">
							<font color="red">企业号已禁用</font>
						</c:when>
						<c:otherwise>
							<c:out value="${wcm:widget('select[@com.riversoft.platform.translate.WxStatus]',vo.wxStatus)}" />
						</c:otherwise>
					</c:choose></td>
			</tr>
		</c:forEach>
	</table>
</form>

<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>