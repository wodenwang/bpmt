<#-- 日期时间框 --> 
<#if value??>
	<#if value?is_date>
		<#assign value = (value?string(patten))>
	</#if>
<#else>
	<#assign value = "">
</#if>

<#if width??&&width!='null'>
	<#assign style = style+"width:"+width+";">
</#if>

<script type="text/javascript">
	$(function(){
		var $text = $('#${uuid}');
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
				$('#${uuid}_clear_btn').button( "option", "disabled", false );
			}else{//失效
				if($('#hidden_${uuid}').size()<1){
					$text.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$text.val()+'" />'));
				}
				$text.prop('disabled',true);
				$('#${uuid}_clear_btn').button( "option", "disabled", true );
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
<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
<input id="${uuid}" type="text" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>__</#if>" value="${value!''}" class="needValid ${param!'date'} ${validate!''} <#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>disable</#if>" 
	style="${style!''}" <#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>
	defaultDate="${defaultDate!''}"	/>

<#if state?? && ( (state!'') == 'readonly')>
	<input name="${name}" value="${value!''}" type="hidden" />
</#if>

<#if state?? && ( (state!'') == 'normal')>
	<button type="button" icon="trash" text="false" onclick="$('#${uuid}').val('')" id="${uuid}_clear_btn">${cm.lan("#:zh[清空]:en[empty]#")}</button>
</#if>
<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />