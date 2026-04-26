<#if value??>
	<#assign value = (value?html)>
<#else>
	<#assign value = "">
</#if>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		
		var initFn = function(val){
			Ajax.post($zone,_cp + '/widget/UserAction/userMain.shtml',{
				data : {
					state : "${state}",
					name : '${name}',
					codeFlag : '${codeFlag}',
					validate : "${validate!''}",
					value : val!=undefined?val:$("#${uuid}_input").val(),
					_params : $("#${uuid}_params").val(),
					checkType : 'checkbox'
				}
			});	
		};
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			initFn();
		});

		//设值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				var $val = $('[name=${name}]',$zone);
				if($val.size()>0){
					return $val.val();
				}else{
					return $("#${uuid}_input").val();
				}
			}else{
				initFn(val);
			}
		});
		
		//设置回调
		Core.fn($zone,'change',function($select){
			Widget._getChange($form,'${name}')($select);
		});
					
		initFn();
	});
</script>

<textarea id="${uuid}_params" style="display:none;" name="_tmp_params">${dyncParams!''}</textarea>
<textarea id="${uuid}_input" style="display:none;" name="_tmp_value">${value!""}</textarea>
<div id="${uuid}"></div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />