<#--自定义明细控件--> 
<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			Ajax.post($zone,_cp+'/widget/DetailAction/index.shtml',{
				data : {
					_params : $('#${uuid}_params').val(),
					_name : '${name}',
					widgetKey : '${widgetKey}',
					list :  $('#${uuid}_old_data').val(),
					state : '${state}'
				}
			});
		});
		
		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
		
		//生效与实效
		Widget._setEnabled($form,'${name}',function(flag){
			if(flag){//生效
				$('button[name=edit]',$zone).button( "option", "disabled", false );
				$('button[name=batch]',$zone).button( "option", "disabled", false );
			}else{//失效
				$('button[name=edit]',$zone).button( "option", "disabled", true );
				$('button[name=batch]',$zone).button( "option", "disabled", true );
			}
		});
			
		//设值/取值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $('textarea[name=${name}]',$zone).val();
			}else{
				$('#${uuid}_old_data').val(val);
				Widget.init($form,'${name}');
				return val;
			}
		});
		
		Widget.init($form,'${name}');
		
	});
</script>

<textarea style="display:none;" id="${uuid}_old_data">${value}</textarea>
<textarea style="display:none;" id="${uuid}_params">${dyncParams!''}</textarea>

<div id="${uuid}"></div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />