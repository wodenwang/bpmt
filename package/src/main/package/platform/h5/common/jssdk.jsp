<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/include/common.jsp"%>

<c:if test="${fromWx&&signature!=null}">
	<script>
		wx.config({
		    debug: false, 
		    appId: '${signature.appId}', 
		    timestamp: ${signature.timestamp},
		    nonceStr: '${signature.nonce}',
		    signature: '${signature.signature}',
		    jsApiList: ${apiList}
		});
		
		/*
		wx.error(function(res){
			alert('微信SDK验证不成功,您可能无法使用部分功能.');
		});
		*/
	</script>
</c:if>
