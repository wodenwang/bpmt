<#-- 普通文本框 --> 
<#setting number_format="#">
<script>
	function openPriWin(btn){
		var $this = $(btn);
		var $textarea = $this.prev();
		var $span = $textarea.prev();
		var opt = {title:'功能点设置',minWidth:700};
		var data = {};
		data.pri = $textarea.val();
		opt.data = data;
		opt.buttons = [{
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				},
				{
					text : '确定',
					click : function() {
						var pri = buildPriJson($(this));
						var tmpVal = $textarea.val();
						$textarea.val(JSON.stringify(pri));
						if(tmpVal!=$textarea.val()){
							$span.css('font-style','oblique');
							$span.css('color','blue');
						}
						{
							var busiName;
							if(pri.busiName!=null&&pri.busiName!=''){
								busiName = pri.busiName;
							}else{
								busiName = '(系统自动生成)';
							}
							if(!pri.scriptOnly){
								$span.html(busiName);
							}else{
								$span.html('<font color="red"><b>'+busiName+'</b></font>');
							}						
						}
						$(this).dialog("close");
					}
				}];
		Ajax.win(_cp+'/widget/PriAction/index.shtml',opt);
	}
</script>
<span>
	<#if value.type == 1 >
		${value.busiName!'(系统自动生成)'}
	<#else>
        <font color="red"><b>${value.busiName!'(系统自动生成)'}</b></font>
	</#if>
</span>
<textarea name="${name}" style="display:none;" class="${validate!''} needValid ">${stringValue!''}</textarea>
<button icon="gear" type="button" onclick="openPriWin(this);" <#if state?? && (state!'') == 'readonly'> disabled="disabled"</#if>>设置</button>
<#if tip??>
	<font color="red" tip="true" title="${tip}" style="font-weight:blod;cursor:help;">(提示)</font>
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />