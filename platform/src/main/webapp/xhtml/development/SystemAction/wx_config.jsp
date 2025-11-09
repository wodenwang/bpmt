<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
	});
</script>

<div id="${_zone}_netset_msg_zone"></div>
<form zone="${_zone}_netset_msg_zone" action="${_acp}/saveNetConfig.shtml" option="{confirmMsg:'确认保存?'}">
	<div class="ws-bar right">
		<button type="submit" icon="disk">保存</button>
	</div>
	<div tabs="true" button="left">
		<div title="网络设置">
			<table class="ws-table">
				<tr>
					<th>网络协议</th>
					<td><c:choose>
							<c:when test="${config['wx.net.https']=='true'}">
								<input type="radio" name="wx.net.https" value="false" />
								<label>HTTP</label>
								<input type="radio" name="wx.net.https" value="true" checked="checked" />
								<label>HTTPS</label>
							</c:when>
							<c:otherwise>
								<input type="radio" name="wx.net.https" value="false" checked="checked" />
								<label>HTTP</label>
								<input type="radio" name="wx.net.https" value="true" />
								<label>HTTPS</label>
							</c:otherwise>
						</c:choose></td>
				</tr>
				<tr>
					<th>可信域名<font color="red" tip="true" title="设置BPMT外部访问域名,以便腾讯服务器调用.">(提示)</font></th>
					<td><wcm:widget cmd="text[300px]" name="wx.net.domain" value="${config['wx.net.domain']}" /></td>
				</tr>
			</table>
		</div>

		<div title="开放平台">
			<table class="ws-table">
				<tr>
					<th>开放平台开关</th>
					<td><wcm:widget cmd="radio[YES_NO]" name="wx.open.flag" value="${config['wx.open.flag']}" /></td>
				</tr>
				<tr>
					<th>开放平台appId</th>
					<td><wcm:widget cmd="text[300px]" name="wx.open.appId" value="${config['wx.open.appId']}" /></td>
				</tr>
				<tr>
					<th>开放平台appSecret</th>
					<td><wcm:widget cmd="textarea[300px]" name="wx.open.appSecret" value="${config['wx.open.appSecret']}" /></td>
				</tr>
				<tr>
					<th>访客表</th>
					<td><select name="wx.open.table" class="chosen">
							<option value="">请选择</option>
							<c:forEach items="${openTables}" var="model">
								<c:choose>
									<c:when test="${model.name==config['wx.open.table']}">
										<option value="${model.name}" selected="selected">[${model.name}]${model.description}</option>
									</c:when>
									<c:otherwise>
										<option value="${model.name}">[${model.name}]${model.description}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					</select></td>
				</tr>

				<tr>
					<th>扫码登陆<font color="red" tip="true" title="需要打开开放平台.">(提示)</font></th>
					<td><wcm:widget cmd="radio[YES_NO]" name="wx.web.login.qrcode" value="${config['wx.web.login.qrcode']}" /></td>
				</tr>
				<tr>
					<th>绑定公众号appId<font color="red" tip="true" title="绑定的公众号需要在平台中管理,绑定多个使用分号分隔.">(提示)</font></th>
					<td><wcm:widget cmd="textarea[300px]" name="wx.web.mp.appIds" value="${config['wx.web.mp.appIds']}" /></td>
				</tr>

			</table>
		</div>

		<div title="企业号">
			<table class="ws-table">
				<tr>
					<th>企业号开关</th>
					<td><wcm:widget name="wx.qy.flag" cmd="radio[YES_NO]" value="${config['wx.qy.flag']}" /></td>
				</tr>
				<tr>
					<th>corpId</th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.corpId" value="${config['wx.qy.corpId']}" /></td>
				</tr>
				<tr>
					<th>corpSecret</th>
					<td><wcm:widget cmd="textarea[300px]" name="wx.qy.corpSecret" value="${config['wx.qy.corpSecret']}" /></td>
				</tr>
				<tr>
					<th>通讯录管理模式</th>
					<td><wcm:widget name="wx.qy.contactmode" cmd="radio[@com.riversoft.platform.translate.ContactMode]" value="${config['wx.qy.contactmode']}" /></td>
				</tr>
				<tr>
					<th>默认消息应用Id<font color="red" tip="true" title="用于接收系统发出的默认消息.">(提示)</font></th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.default" value="${config['wx.qy.default']}" /></td>
				</tr>
			</table>
		</div>

		<div title="企业号支付">
			<table class="ws-table">
				<tr>
					<th>企业号支付开关</th>
					<td><wcm:widget name="wx.qy.pay.flag" cmd="radio[YES_NO]" value="${config['wx.qy.pay.flag']}" /></td>
				</tr>
				<tr>
					<th>商户ID</th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.pay.mchId" value="${config['wx.qy.pay.mchId']}" /></td>
				</tr>
				<tr>
					<th>支付秘钥</th>
					<td><wcm:widget cmd="textarea[300px]" name="wx.qy.pay.key" value="${config['wx.qy.pay.key']}" /></td>
				</tr>
				<tr>
					<th>证书路径</th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.pay.certPath" value="${config['wx.qy.pay.certPath']}" /></td>
				</tr>
				<tr>
					<th>证书密码</th>
					<td><input type="password" name="wx.qy.pay.certPassword" value="${config['wx.qy.pay.certPassword']}" /></td>
				</tr>
			</table>
		</div>

	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>