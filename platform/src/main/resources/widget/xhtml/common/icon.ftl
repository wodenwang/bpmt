<#if params[0]?? && params[0].name=='sys'>
	<#assign iconType = "sysIcon">
<#else>
	<#assign iconType = "jqueryIcon">
</#if>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		Ajax.post('${uuid}',_cp+'/widget/IconAction/${iconType}.shtml',{
			data : {
				name : '${name}',
				validate : '${validate!''}',
				value : '${value!''}',
				state : '${state}'
			}
		});
	});
</script>
<div id="${uuid}"></div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />
