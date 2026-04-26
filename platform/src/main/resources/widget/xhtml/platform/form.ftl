<#-- 超级表单编辑框(一个字段就是一个表单) --> 

<#assign style = "">
<#if (params[0].name)??&&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
<#else>
	<#assign style = style+"width:auto;">
</#if>

<#if (params[1].name)??&&(params[1].name)!='null'>
	<#assign style = style+"height:"+params[1].name+";">
</#if>

<script src="/ueditor/ueditor.parse.js"></script>

<script type="text/javascript">
	$(function(){
		var $textarea = $('#A${uuid}_text');
		var $div = $('#A${uuid}_div');
		var $form = $textarea.parents('form:first');
		var oldHtml = $div.html();
		
		//取消onmouse事件
		$div.parents('tr:first').unbind('mouseover mouseout');
				
		//设值/取值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $textarea.val();
			}else{
				$div.html(val);
				$textarea.val(val);
		        uParse('#A${uuid}_div',{
		        	rootPath : '/ueditor/'
		        });
				return val;
			}
		});

		//初始化
		Widget._setInit($form,'${name}',function(){
			Widget.val($form,'${name}',oldHtml);
		});
		
		$textarea.val(oldHtml);
        uParse('#A${uuid}_div',{
        	rootPath : '/ueditor/'
        });
        
        <#if state?? && ((state!'') == 'readonly' || (state!'') == 'disabled')>
        	$('textarea',$div).prop('readonly',true);
        	$('textarea',$div).css('background-color','#fff');
		</#if>
		<#if state?? && (state!'') == 'disabled'>
			$textarea.prop("disabled",true);
		</#if>
	});
</script>

<textarea id="A${uuid}_text" name="${name}" style="display:none;"></textarea>
<div id="A${uuid}_div" style="${style}" class="ws-widget-form">
	${value!''}
</div>