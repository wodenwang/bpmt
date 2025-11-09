<#-- 带编辑功能文本框 --> 
<#setting number_format="#.####">

<#assign style = "">
<#if (params[0].name)??&&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
<#else>
	<#assign style = style+"width:100%;">
</#if>

<#if (params[1].name)??&&(params[1].name)!='null'>
	<#assign style = style+"height:"+params[1].name+";">
<#else>
	<#assign style = style+"height:200px;">
</#if>

<script>
	$(function(){
		var $editor = $('#${uuid}_textarea');
		var $form = $editor.parents('form:first');
		var oldValue = $editor.val();
		
		var config = {};
		<#if state?? && (state!'') == 'readonly'>
			config = {tools:"Fullscreen,Print",disableContextmenu:true};
		</#if>
		$editor.xheditor(config);
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			var params = $('#${uuid}_params').val();
			if(params==undefined||params==''){//无参数
				$editor.val(oldValue);
			} else {
				try{
					var json = JSON.parse(params);
					if(json.val!=undefined){
						$editor.val(json.val);
					}
				}catch(e){
					//do nothing
				}
			}
		});
	});
</script>

<textarea style="${style}" id="${uuid}_textarea" name="${name}" class="needValid ${validate!''}" <#if state?? && (state!'') == 'readonly'> readonly="readonly"</#if>>${value!''}</textarea>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />