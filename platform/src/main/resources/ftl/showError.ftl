<#-- ActionAspect.doAround 调用 --> 
<script type="text/javascript">
	$(function() {
		var $close = $('a.ui-corner-all', $('#${uuid}'));
		$close.click(function() {
			$('#${uuid}').fadeOut(500);
		});
		$close.hover(function() {
			$(this).toggleClass('ui-state-hover');
		});
		
		$('#${uuid}_a').click(function(event){
			event.preventDefault();
			$('#${uuid}_win').dialog({
				title:'异常描述',
				minWidth:800,
				modal : true,
				buttons:[{
					text:'关闭',
					click:function(){
						$(this).dialog('close');
					}
				}]
			}).dialogExtend({
				"closable" : true,
				"maximizable" : true,
				"minimizable" : false,
				"minimizeLocation" : 'left',
				"collapsable" : false,
				"dblclick" : 'maximize'
			});
		});
	});
</script>

<style type="text/css">
a.ui-corner-all.msgPage {
	float: right;
	position: absolute;
	right: 10px;
	top: 15px;
	cursor: pointer;
}
</style>

<div id="${uuid}">
	<div class="ws-msg error" style="position: relative;">
		${msg} 
		<br/>
		<p style="margin-left:20px;">
			<strong style="margin-right:10px;">摘要:</strong>${title}
			<a href="javascript:void(0);" id="${uuid}_a">[查看详情]</a>
		</p>
		<a href="#" class="ui-corner-all msgPage" role="button"><span
			class="ui-icon ui-icon-closethick">close</span></a>
	</div>
	<div id="${uuid}_win" style="display:none;">
		<div tabs="true">
			<div title="详情分析">
				<textarea style="width:100%;min-height:200px;">${trace}</textarea>
			</div>
			<#if scripts??>
				<#list scripts as o>
					<div title="脚本[${o.method!'初始调用'}]">
						<textarea style="width:100%;min-height:200px;" mode="groovy" code="true" class="CodeMirror-normal" option="{readOnly:true}">${o.script}</textarea>
					</div>
				</#list>
			</#if>
		</div>
	</div>
</div>