<#-- FlowHelper.showOpinion 调用 --> 
<#include "classpath:/ftl/common.ftl"/>

<ul class="am-comments-list am-comments-list-flip">
<#list list as vo>
	<#assign u = user.findUser(vo.OPR_USER)>
	<li class="am-comment">
		<@wpf.image value="${u.wxAvatar!u.busiName}" cssClass="am-comment-avatar" style="width:48;height:48"></@wpf.image>
		<div class="am-comment-main">
			<header class="am-comment-hd">
				<div class="am-comment-meta">
					${u.busiName} [${vo.CREATE_DATE}]
				</div>
			</header>
			<div class="am-comment-bd">
				${vo.OPINION!''}
			</div>
			<footer class="am-comment-footer am-cf">
				<div class="am-comment-actions am-fr">[${vo.ACTIVITY_NAME!''}]节点执行[${vo.SEQUENCE_FLOW_NAME!''}]操作</div>
			</footer>
		</div>
	</li>
</#list>
</ul>


