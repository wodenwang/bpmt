<#assign style = "">
<#if (params[0].name)??&&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
</#if>

<script>
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
						$text.val(json.val);
					}
				}catch(e){
					//do nothing
				}
			}
		});
		
		//可用/不可用
		Widget._setEnabled($form,'${name}',function(flag){
			if(flag){//生效
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

		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
		
		// 扫码
		$("#${uuid}_scan").click(function(){
			wx.scanQRCode({
				    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
				    scanType: ["qrCode", "barCode"], // 可以指定扫二维码还是一维码，默认二者都有
				    success: function (res) {
					    var resultStr = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
					    $('#${uuid}').val(resultStr);
					    $('#hidden_${uuid}').val(resultStr);
						// 回调函数
						Widget._getChange($form,'${name}')('${uuid}');
					}
			});
		});
	});
</script>

<#if state?? && ((state!'') == 'normal')>
    <#assign cssstyle = "am-input-group">
<#else>
    <#assign cssstyle = "am-form">
</#if>

<div  class="${cssstyle}"	<#if style?? && style!''> style="${style}"</#if>>
	<input id="hidden_${uuid}" type="hidden" name="${name}" value="${value!''}"/>
	<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
	<input id="${uuid}" class="am-form-field" type="${type}" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" 
		value="${value!''}" 
		<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>
			 disabled="disabled"
		<#else>
			readonly="readonly"
		</#if> 
		<#-- 判断必填 -->
		<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> 
		<#-- 最大,最小长度 -->
		<#if (validateObj.maxlength)??> maxlength="${validateObj.maxlength!''}"</#if> 
		<#if (validateObj.minlength)??> minlength="${validateObj.minlength!''}"</#if> 
	/>
	
	<#if state?? && ( state!'') == 'readonly' >
		<input id="${uuid}_real" type="hidden" name="${name}" value="${value!''}" />
	</#if>
	
	<#if state == 'normal'>
		<span class="am-input-group-btn">
			<button id="${uuid}_scan" class="am-btn am-btn-default" type="button"><i class="am-icon-qrcode"></i></button>
		</span>
	</#if>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />