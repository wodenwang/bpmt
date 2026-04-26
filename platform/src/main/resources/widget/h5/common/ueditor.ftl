<script src="${_cp}/js/arteditor/arteditor.js"></script>
<link href="${_cp}/js/arteditor/arteditor.css" rel="stylesheet" type="text/css">

<#-- 多行文本框 -->
<#assign type = "">
<#if ((validateObj.number)??&&(validateObj.number)) || ((validateObj.digits)??&&(validateObj.digits)) || ((validateObj.integer)??&&(validateObj.integer))>
	<#assign type = "number">
<#elseif ((validateObj.email)??&&(validateObj.email))>
	<#assign type = "email">
<#elseif ((validateObj.url)??&&(validateObj.url))>
	<#assign type = "url">
<#else>
    <#assign type = "text">
</#if>

<#assign style = "">
<#if (params[0].name)?? &&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
</#if>
<#if (params[1].name)?? &&(params[1].name)!='null'>
	<#assign style = style+"height:"+params[1].name+";">
</#if>

<#if state?? && ( state!'') == 'readonly' >
	<textarea id="${uuid}_real" style="display:none;" name="${name}">${value!''}</textarea>
</#if>

<script>
	$(function () {
		var $text = $('#${uuid}');
		var $form = $text.parents('form:first');
		
		var oldValue = $text.val();
		//初始化
		Widget._setInit ($form, '${name}', function () {
			var params = $('#${uuid}_params').val();
			if (params == '') {
			    //无参数
				$text.val(oldValue);
			} else {
				try {
					var json = JSON.parse(params);
					if (json.val != undefined) {
						$text.val(json.val);
					}
				} catch (e) {
					//do nothing
				}
			}
		});
		
		//可用/不可用
		Widget._setEnabled($form, '${name}', function (flag) {
			if (flag) {
			    //生效
				$('#hidden_${uuid}').remove();
				$text.prop('disabled', false);
			} else {
			    //失效
				if($('#hidden_${uuid}').size() < 1) {
					$text.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$text.val()+'" />'));
				}
				$text.prop('disabled', true);
			}
		});
		
		//设值
		Widget._setVal($form, '${name}', function(val){
			if(val==undefined){
				return $text.val();
			}else{
				$text.val(val);
			}
		});
		
		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
		
		//回调函数
		$text.blur(function(){
			var $this = $(this);
			Widget._getChange($form,'${name}')($this);
		});
		
		$('#${uuid}_content').artEditor({
			imgTar: '#${uuid}_imageUpload',
			limitSize: 2, // 兆
			showServer: false,
			uploadUrl: '',
			data: {},
			uploadField: 'image',
			placeholader: '<p style="color: #999">请输入内容</p>',
			validHtml: ["<br/>"],
			formInputId: '${uuid}',
			uploadSuccess: function(res) {
			 	// 上传成功后清除img信息
				return res.path;
			},
			uploadError: function(res) {
				// something error
			}
		});
	});
</script>

<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>

<div class="publish-article-content">
	<input type="hidden" id="${uuid}" name="${name}" required="required" />
	<div class="article-content" id="${uuid}_content" name="${uuid}_content" style="font-size: 15px;">
	</div>
	<div class="footer-btn g-image-upload-box">
		<div class="upload-button">
			<span class="upload"><i class="upload-img"></i>插入图片</span>
			<input class="input-file" id="${uuid}_imageUpload" type="file" name="fileInput" capture="camera" accept="image/*" style="position:absolute;left:0;opacity:0;width:100%;">
		</div>
	</div>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />