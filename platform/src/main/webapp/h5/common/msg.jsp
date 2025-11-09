<%@ page language="java" pageEncoding="UTF-8"%>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<c:choose>
	<c:when test="${_msg_type=='info'}">
		<c:set var="am_msgType" value="am-alert-success" />
		<c:set var="am_msgIcon" value="am-icon-info-circle" />
		<c:set var="weui_msgIcon" value="weui_icon_success" />
		<c:set var="msgTitle" value="提示" />
	</c:when>
	<c:when test="${_msg_type=='warning'}">
		<c:set var="am_msgType" value="am-alert-warning" />
		<c:set var="am_msgIcon" value="am-icon-warning" />
		<c:set var="weui_msgIcon" value="weui_icon_info" />
		<c:set var="msgTitle" value="警告" />
	</c:when>
	<c:when test="${_msg_type=='error'}">
		<c:set var="am_msgType" value="am-alert-danger" />
		<c:set var="am_msgIcon" value="am-icon-times-circle" />
		<c:set var="weui_msgIcon" value="weui_icon_warn" />
		<c:set var="msgTitle" value="警告" />
	</c:when>
	<c:otherwise>
		<c:set var="am_msgType" value="am-alert-primary" />
		<c:set var="am_msgIcon" value="am-icon-info-circle" />
		<c:set var="weui_msgIcon" value="weui_icon_info" />
		<c:set var="msgTitle" value="提示" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${_h5_js=='amaze'||param._h5_js=='amaze'}">
		<div class="am-container am-margin-top">
			<div class="am-alert ${am_msgType}">
				<h4>
					<i class="${am_msgIcon}"></i> ${msgTitle}
				</h4>
				<div class="am-margin-top">${_msg}</div>
			</div>
			<button type="button" class="am-btn am-btn-default am-radius am-btn-block" onclick="wx.closeWindow();">关闭</button>
		</div>
	</c:when>
	<c:otherwise>
		<div class="container" id="container">
			<div class="msg">
				<div class="weui_msg">
					<div class="weui_icon_area">
						<i class="${weui_msgIcon} weui_icon_msg"></i>
					</div>
					<div class="weui_text_area">
						<h2 class="weui_msg_title">${msgTitle}</h2>
						<p class="weui_msg_desc">${_msg}</p>
					</div>
					<div class="weui_opr_area">
						<p class="weui_btn_area">
							<a href="javascript:;" class="weui_btn weui_btn_primary" onclick="wx.closeWindow();">关闭</a>
						</p>
					</div>
					<div class="weui_extra_area"></div>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>