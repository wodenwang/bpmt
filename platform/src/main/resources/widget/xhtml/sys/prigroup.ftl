<#-- 普通文本框 --> 
<#setting number_format="#">
<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $text = $('textarea', $zone);
		var $span = $('span.show', $zone);
		$('button[name=basic]', $zone).click(function() {
			Ajax.win(_cp + '/widget/PriAction/relate.shtml', {
				title : '权限点附加设置',
				minWidth : 700,
				data : {
					groupId : '${groupId}',
					priKey : '${priKey}',
					json : $text.val()
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '确定',
					click : function() {
						var vo = Core.getFn($(this), 'buildJson')();
						if (vo.checkScript.replace(/(^\s*)|(\s*$)/g, "") != '') {
							$span.css('color', 'red');
							$span.html('[附加配置]');
						} else {
							$span.html('');
						}
						$text.val(JSON.stringify(vo));
						$(this).dialog("close");
					}
				} ]
			});
		});
		
		$('button[name=function]',$zone).click(function(){
			Ajax.win(_cp+'/widget/PriAction/find.shtml',{
				title : '功能点查看',
				minWidth : 700,
				data : {
					priKey : '${pri.priKey}'
				},
				buttons : [{
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}] 		
			});
		});
	});
</script>

<div id="${uuid}">
	<span style="font-width:blod;" class="show">
		<#if value??>
			<#if value.checkScript ?? && value.checkScript != ''>
				[附加配置]
			</#if>
		</#if>
	</span>
	
	<textarea name="${name}" style="display:none;" class="${validate!''} needValid ">${stringValue!''}</textarea>
	
	<#if pri.type!=2>
		<button name="basic" icon="gear" text="false" type="button" "<#if (state?? && (state!'') == 'readonly')> disabled="disabled"</#if> tip="${tip??}" title="${tip}">附加配置</button>
	</#if>
	
	<#if pri.type==4>
		<span style="font-width:blod;color:blue;" tip="true" title="或模式">( or )</span>
	<#elseif pri.type==3>
		<span style="font-width:blod;color:blue;" tip="true" title="且模式">(and)</span>
	</#if>
	
	<#if pri.type!=1>
		<button name="function" icon="flag" text="false" type="button" "<#if (state?? && (state!'') == 'readonly')> disabled="disabled"</#if>>查看功能点</button>
	</#if>
	
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />