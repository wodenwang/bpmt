<#if value??>
	<#assign value = (value?html)>
<#else>
	<#assign value = "">
</#if>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		var multiFlag = ${multiFlag};
		var initFn = function(){
			Ajax.post($zone,_cp + '/widget/FileAction/index.shtml',{
				data : {
					mode : "${mode!''}",
					state : "${state}",
					name : '${name}',
					validate : "${validate!''}",
					value : $("#${uuid}_input").val(),
					_params : $("#${uuid}_params").val(),
					checkType : multiFlag ? 'checkbox':'radio'
				}
			});	
		};
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			initFn();
		});
		
		initFn();
	});
</script>

<textarea id="${uuid}_input" style="display:none;" name="_old_value">${value!""}</textarea>
<textarea id="${uuid}_params" style="display:none;" name="_tmp_params">${dyncParams!''}</textarea>
<textarea style="display:none;" name="${name}~ID">${fileId}</textarea>
<div id="${uuid}"></div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />