<#-- 日期时间框 --> 
<#if value??>
	<#if value?is_date>
		<#assign value = (value?string(patten))>
	</#if>
<#else>
	<#assign value = "">
</#if>

<#if param??>
<#else>
	<#assign param = "date">
</#if>

<script>
	$(function(){
		var $text = $('#${uuid}_input');
		var $form = $text.parents('form:first');
		var oldValue = $text.val();
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			var params = $('#${uuid}_params').val();
			if(params==''){//无参数
				$text.val(oldValue);
			}else{
				try{
					var json = JSON.parse(params);
					if(json.val!=undefined){
						if(json.val=='now'){
							var now = new Date();//当前时间
							var param = '${param!'date'}';
							if(param=='date'){
								$text.val(now.format('yyyy-MM-dd'));
							}else if(param=='time'){
								$text.val(now.format('hh:mm:ss'));
							}else if(param=='yearmonth'){
								$text.val(now.format('yyyyMM'));
							}else{
								$text.val(now.format('yyyy-MM-dd hh:mm:ss'));
							}
						}else{
							$text.val(json.val);
						}
					}
				}catch(e){
					//do nothing
				}
			}
		});
		
		//可用/不可用
		Widget._setEnabled($form,'${name}',function(flag){
			if(flag){//生效
				$('#hidden_${uuid}').remove();
				$text.prop('disabled',false);
			}else{//失效
				if($('#hidden_${uuid}').size()<1){
					$text.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$text.val()+'" />'));
				}
				$text.prop('disabled',true);
			}
		});
		
		//设值/取值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $text.val();
			}else{
				$text.val(val);
				return val;
			}
		});
		
		//回调函数
		$text.blur(function(){
			var $this = $(this);
			Widget._getChange($form,'${name}')($this);
		});
		
		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
		
	});
</script>

<script>
	$(function(){	    
		$("#${uuid} span").click(function(){
			$(this).prev().val('');
		});
	});
</script>

<#-- 根据状态不同采用不同的样式 -->
<#if state?? && ((state!'') == 'normal')>
    <#assign cssstyle = "am-input-group">
<#else>
    <#assign cssstyle = "am-form">
</#if>

<#if param=='date'||param=='datetime'||param=='time'>
	<#if param=='datetime'>
		<#assign param = "datetime-local">
	</#if>

	<#-- 日期时间,时间,采用原生 -->
	<div class="${cssstyle}" id="${uuid}">
	    <textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
		<input id="${uuid}_input" type="${param}" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" class="am-form-field"
		value="${value!''}" 
		<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled" </#if> 
		<#-- 判断必填 -->
		<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> 
		<#-- 最大,最小长度 -->
		<#if (validateObj.maxlength)??> maxlength="${validateObj.maxlength!''}"</#if> 
		<#if (validateObj.minlength)??> minlength="${validateObj.minlength!''}"</#if> 
		/>
		<#if state?? && ( (state!'') == 'normal')>
	    <span class="am-input-group-label"><i class="am-icon-close am-icon-fw"></i></span>
	    </#if>
	</div>
<#elseif param=='yearmonth'>
	<#-- 年月,支持 -->
	<div class="${cssstyle}" id="${uuid}">
	    <textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
		<input id="${uuid}_input" type="month" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" class="am-form-field"
		value="${value!''}" 
		<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled" </#if> 
		<#-- 判断必填 -->
		<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> 
		<#-- 最大,最小长度 -->
		<#if (validateObj.maxlength)??> maxlength="${validateObj.maxlength!''}"</#if> 
		<#if (validateObj.minlength)??> minlength="${validateObj.minlength!''}"</#if> 
		/>
	 <#if state?? && ( (state!'') == 'normal')>
	    <span class="am-input-group-label"><i class="am-icon-close am-icon-fw"></i></span>
	 </#if>
	</div>
</#if>
<#if state?? && ( state!'') == 'readonly' >
	<input id="${uuid}_real" type="hidden" name="${name}" value="${value!''}" />
</#if>


<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />