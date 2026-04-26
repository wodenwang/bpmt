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

<#assign mode = "normal">
<#if (params[2].name)??&&(params[2].name)!='null'>
	<#assign mode = params[2].name>
</#if>

<script src="/js/ueditor/dialog/textarea/textarea.js"></script>
<script type="text/javascript">
	$(function(){
		var $text = $('#A${uuid}');
		var $form = $text.parents('form:first');
		var oldValue = $text.html();
		
		//取消onmouse事件
		$text.parents('tr:first').unbind('mouseover mouseout');
		
		var opt = {
			toolbars: [[
				'undo', 'redo', '|',
				'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', '|',
	            'customstyle', 'paragraph', 'fontfamily', 'fontsize'
			] ,[
				'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
	            'directionalityltr', 'directionalityrtl', 'indent', '|',
	            'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
	            'link', 'unlink',  '|', 
	            'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts'
	        ], [
	            'source','simpleupload', 'scrawl', 'snapscreen' ,'wordimage','imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
	            'horizontal', 'date', 'time', 'spechars', 
	            'searchreplace','print', 'preview'
			]],
			autoTransWordToList : true,
	        enableAutoSave :false,
	        saveInterval :1000000,
		    serverUrl : '/ueditor/controller.jsp',
		    codeMirrorJsUrl : '/js/codemirror/lib/codemirror.js',
		    codeMirrorCssUrl : '/js/codemirror/lib/codemirror.css',
		    autoHeightEnabled: true,
		    autoFloatEnabled: false,
		    elementPathEnabled: false,
		    sourceEditor : 'textarea',
		    initialStyle : 'p{line-height:1em} textarea {width: 120px;	height: 30px;font-size: 12px;	color: #555;	border : 0;background-color: #DBEEF3;}',
		    zIndex : 0
		};
		<#if mode=="dev">
			//开发模式
			opt.toolbars[1].push('insertcode');
		</#if>
		<#if mode=="mini">
			//最小模式
			opt.toolbars = [[
				'undo', 'redo', '|',
				'bold', 'italic', 'underline', 'forecolor', 'backcolor',
	            'fontfamily', 'fontsize'
			]];
		</#if>
		
		<#if state?? && ((state!'') == 'readonly' || (state!'') == 'disabled')>
			//opt.readonly = true;
			opt.wordCount = false;
			opt.toolbars = [[]];
			opt.initialStyle = 'p{line-height:1em} textarea {width: 120px;	height: 30px;font-size: 12px;	color: #555;	border : 0;background-color: #fff;}';
		</#if>

		if($text.parents('div[win=true]').size()<=0){//非窗口允许全屏
			opt.toolbars[0].push('fullscreen');
		}
				
		<#-- 开发模式增加编辑框按钮 -->
		<#if mode=="dev">
			ueditorTextarea('A${uuid}');
		</#if>
		
		var ue = UE.getEditor('A${uuid}', opt);
		ue.addListener("fullscreenchanged", function(name,flag) {
			if(flag){
				$('#A${uuid}').find('.edui-editor').css('z-index','9989');
				 if(UE.browser.gecko){
				 	$('#A${uuid}').find('.edui-editor').css('top',0);
				 }
			}else{
				$('#A${uuid}').find('.edui-editor').css('z-index','0');
			}
		 });
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			ue.setContent(oldValue);
		});
		
		//可用/不可用
		Widget._setEnabled($form,'${name}',function(flag){
			if(flag){//生效
				 ue.setEnabled();
			}else{//失效
				ue.setDisabled(['print','fullscreen']); 
			}
		});
		
		//设值/取值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return ue.getContent();
			}else{
				ue.setContent(val);
				return val;
			}
		});
		
		//初始执行
		ue.ready(function() {
			<#if state?? && ((state!'') == 'readonly' || (state!'') == 'disabled')>
			ue.setDisabled(['print','fullscreen']); 
			</#if>
			Widget.getFn($form , '${name}','ready')($text);
		});

	});
</script>

<script style="${style}" id="A${uuid}" name="${name}" class="needValid ${validate!''}" type="text/plain" >${value!''}</script>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />