<script>
	$(function(){
		var agent = '${agent}';
		var multiFlag = ${multiFlag};
		
		//预览文件
		var showFile = function($a){
			var urls = [];
			$.each($('#${uuid}_ul a[name=showFile]'),function(){
				var url = $(this).attr('fileurl');
				var pixel = $(this).attr('pixel');
				if(url && (pixel=='png'||pixel=='jpg'||pixel=='gif'||pixel=='jpeg')){
					urls.push(url);
				}
			});
		
			var url = $a.attr('fileurl');
			var pixel = $a.attr('pixel');
			var sysName = $a.attr('filesysname');
			var fileName = encodeURI($a.attr('filename'));
			var type = $a.attr('filetype');
			if(url && (pixel=='png'||pixel=='jpg'||pixel=='gif'||pixel=='jpeg')){//调用微信预览接口
				wx.previewImage({
					current: url,
					urls: urls
				});
			}else if(agent!='' && agent!='0' && sysName && (pixel=='pdf'||pixel=='doc'||pixel=='docx'||pixel=='ppt'||pixel=='pptx'||pixel=='xls'||pixel=='xlsx'||pixel=='txt')){
				Wxui.confirm('将文件下发到微信?',function(){
					Wxui.json('${cp}/widget/FileAction/send2wx.shtml?name='+sysName+'&type='+type+'&fileName='+fileName,function(json){
						if(json.error==0){
							Wxui.toast(json.msg);
						}
					});
				});
			}else{
				Wxui.toast('此文件无法预览.','normal');
			}
		};
		
		//删除文件
		var delFile = function($a){
			var fileName = $a.text();
			var allFiles = $('#${uuid}_text').val();
			if(!allFiles){
				allFiles = '[]';
			}
			var items = eval('(' + allFiles + ')');

			var remainedNames = new Array();
			$.each(items, function(index, val) {
				if (val.name != fileName) {
					remainedNames.push(val);
				}
			});

			if (remainedNames.length > 0) {
				$('#${uuid}_text').val(JSON.stringify(remainedNames));
			} else {
				$('#${uuid}_text').val('');
			}
			
			$a.parents('li:first').remove();
		};
		
		//增加一个文件
		var addFile = function(json){
			var items = [];
			
			if(multiFlag){//多文件
				var allFiles = $('#${uuid}_text').val();
				if(!allFiles){
					allFiles = '[]';
				}
				items = eval('(' + allFiles + ')');
				if(!items){
					items = [];
				}
			}else{
				$("#${uuid}_ul li").remove();
			}
			
			items.push(json);
			
			//渲染js
			var $li = $('<li></li>');
			var $span = $('<span></span>');
			var pixel = /[^\.]+$/.exec(json.name);
			$span.append('<img width="16" height="16" border="0" src="${cp}/css/icon/filetype/'+pixel+'.png">');
			var $a = $('<a style="margin-left:5px;color:red;" href="javascript:void(0);"></a>');
			$a.attr('pixel',pixel);
			$a.attr('fileurl',json.url);
			$a.text(json.name);
			$a.attr('name','showFile');
			$a.bind('click',function(){
				showFile($(this));
			});
			$span.append($a);
			<#if state == 'normal'>
			var $delA = $('<a class="am-close" name="delFile" style="float:right;">&times;</a>');
			$delA.bind('click',function(){
				delFile($(this).prev());
			});
			$span.append($delA);
			</#if>
			$li.append($span);
			$("#${uuid}_ul").append($li);

			if (items.length > 0) {
				$('#${uuid}_text').val(JSON.stringify(items));
			} else {
				$('#${uuid}_text').val('');
			}
		};
		
		
		//绑定查看函数
		$('#${uuid} a[name=showFile]').click(function(){
			showFile($(this));
		});
		
		//绑定删除
		$('#${uuid} a[name=delFile]').click(function(){
			delFile($(this).prev());
		});
		
		//IOS调用uploadImage要用递归,真坑爹
		var syncUploadImage = function(localIds){
			var localId = localIds.pop();
			wx.uploadImage({
			    localId: localId, 
			    isShowProgressTips: 1,
			    success: function (r) {
			        var serverId = r.serverId; 
			        //系统上传
			        $.getJSON("${cp}/widget/FileAction/uploadFromWX.shtml?mode=${mode!''}&mediaId="+serverId+"&_data_type=json&_random="+ Math.random(), function(json){
			        	json.url = localId;
			        	addFile(json);
			        });
			        
			        if(localIds.length > 0){
			        	syncUploadImage(localIds);
			        }
			    }
			});			
		};
		
		//选择图片
		$("#${uuid}_open").click(function(){
			wx.chooseImage({
			  success: function (res) {
			  	if(!multiFlag){
				  	if(res.localIds.length>1){
				  		alert("只允许上传一张图片.");
				  		return;
				  	}
			  	}
			  	syncUploadImage(res.localIds);
			  }
			});
		});
		
		//选择文件
	    $('#${uuid}_fileinput').on('change', function() {
	    	$.each(this.files, function() {
		    	var data = new FormData();
		    	var fileName = this.name;
		    	data.append("file", this);
		    	data.append("fileName",this.name);
		    	if(this.size>(10*1024*1024)){//限制10m提交
		    		alert('文件['+this.name+']超过系统限制[10M]大小.');
		    		return;
		    	}
				$.ajax("${cp}/widget/FileAction/uploadFile.shtml?mode=${mode!''}&_data_type=json&_random=" + Math.random(), {
					dataType : 'json',
					timeout : 60000,//超时时间
					type: 'POST', 
					data : data,
					contentType: false,  
					processData: false,  
					beforeSend : function(XMLHttpRequest) {
						Wxui.showLoading();
					},
					success : function(json){
						Wxui.hideLoading();
						json.url = null;
				        addFile(json);
					},
					error : function(res) {
						Wxui.hideLoading();
						Wxui.toast('文件['+ fileName +']上传失败.', 'error');
					}
				});	    	
	    	});
	    });
    		
	});
</script>

<div id="${uuid}">
	<ul class="am-list am-list-static" id="${uuid}_ul">
		<#list list as vo>
			<li class="am-text-truncate">
				<span>
					<#assign pixel = vo.name?substring(vo.name?last_index_of('.')+1)>
					<img width="16" height="16" border="0" src="${cp}/css/icon/filetype/${pixel}.png">
					<a style="margin-left:5px;color:blue;" href="javascript:void(0);" filename="${vo.name}" filetype="${vo.type}" filesysname="${vo.sysName}" pixel="${pixel}" fileurl="${cp}/widget/FileAction/download.shtml?name=${vo.sysName}&type=${vo.type}" name="showFile">${vo.name}</a>
				</span>
				<#if state == 'normal'><a class="am-close" name="delFile" style="position: absolute;top:0;right:0;">&times;</a></#if>
			</li>
		</#list>
	</ul>
	
	<#if state == 'normal'>
		<div class="am-cf">
			<div class="am-btn-group am-fr">
				<button id="${uuid}_open" class="am-btn am-btn-success am-btn-xs" type="button"><i class="am-icon-weixin"></i> 相册</button>
				<div class="am-form-file">
				  <button type="button" class="am-btn am-btn-success am-btn-xs">
				    <i class="am-icon-cloud-upload"></i> 文件</button>
				  <input id="${uuid}_fileinput" type="file" <#if multiFlag=='true'> multiple</#if> />
				</div>	  
			</div>
		</div>
	</#if>
	
	<textarea name="${name}" id="${uuid}_text" style="display: none;">${value!""}</textarea>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />