<#if value??>
	<#assign value = (value?html)>
<#else>
	<#assign value = "">
</#if>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $input = $('#${uuid}_input');
		if($input.val()!=''){
			Ajax.post($zone,_cp+'/widget/FileAction/showimg.shtml',{
				data:{
					files : $input.val(),
					width : '${width}',
					height : '${height!''}'
				}
			});
		}
	});
</script>

<textarea id="${uuid}_input" style="display:none;">${value}</textarea>
<div id="${uuid}"></div>