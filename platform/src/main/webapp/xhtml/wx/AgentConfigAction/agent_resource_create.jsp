<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div id="${_zone}_upload">
	<p>你的浏览器不支持上传控件.请安装flash插件,或更换其他支持html5的浏览器.</p>
</div>

<style type="text/css">
.plupload_message i {
	font-style: normal;
	line-height: 25px;
}

.plupload_file_size, .plupload_file_size {
	width: 80px !important;
}

.plupload_file_status {
	right: 120px !important;
}
</style>

<script type="text/javascript">
	// Initialize the widget when the DOM is ready
	$(function() {
		var $zone = $('#${_zone}');

		var list = [];//保存最后文件json

		var opt = {
			// General settings
			url : '${_acp}/submitResource.shtml',

			max_file_count : 20,
			chunk_size : '1mb',//断点大小

			filters : {
				max_file_size : '5mb',//最大文件限制
				mime_types : [ {
					title : "文档文件",
					extensions : "doc,docx,xls,xlsx,ppt,pptx,pdf,txt,zip,xml"
				}, {
					title : "图片文件",
					extensions : "jpg,jpeg,png"
				}, {
					title : "媒体文件",
					extensions : "avi,rmvb,mp4,amr,mp3,wav,wma"
				} ]
			},

			rename : true,
			multi_selection : true,
			sortable : true,
			dragdrop : true,

			views : {
				list : true,
				thumbs : false, // Show thumbs
				active : 'list'
			},
			multipart_params : {
				'agentKey' : '${param.agentKey}'
			},

			flash_swf_url : '${_cp}/js/plupload/Moxie.swf',
			silverlight_xap_url : '${_cp}/js/plupload/Moxie.xap'
		};

		var $upload = $("#${_zone}_upload").plupload(opt);

		var uploader = $upload.plupload("getUploader");
		uploader.bind('FileUploaded', function(up, file, res) {
			var json = JSON.parse(res.response);
			var o = {};
			o.name = json.fileName;
			o.type = 'temp';
			o.size = file.size / 1024 / 1024;//mb
			list.push(o);
		});

		uploader.bind('ChunkUploaded', function(up, file, res) {
			if (res.status != 200) {
				var chunk = plupload.parseSize(uploader.getOption("chunk_size"));
				file.loaded -= chunk;
				$upload.plupload("stop");
				$('.plupload_header', $upload).children('.plupload_message').remove();
				$upload.plupload("notify", "error", "文件[" + file.name + "]由于服务器或网络原因上传失败,你可以过一段时间后重试.");
			} else {
				var json = JSON.parse(res.response);
				if (json.code != 0) {//上传失败
					if (json.reset) {//需要重置
						file.loaded = 0;
					} else {
						var chunk = plupload.parseSize(uploader.getOption("chunk_size"));
						file.loaded -= chunk;
					}
					$upload.plupload("stop");
					$('.plupload_header', $upload).children('.plupload_message').remove();
					$upload.plupload("notify", "error", "上传文件[" + file.name + "]时失败.");
				}
			}
		});

		//出错提示
		$upload.bind('error', function(event, o) {
			//删掉原有的消息
			$('.plupload_header', $upload).children('.plupload_message').remove();
		});

		//屏蔽close按钮
		$upload.bind('start', function(event, o) {
			$('button.ui-dialog-titlebar-close.ui-button-icon-only', $zone.parent()).hide();
		});

		//屏蔽close按钮
		$upload.bind('stop', function(event, o) {
			$('button.ui-dialog-titlebar-close.ui-button-icon-only', $zone.parent()).show();
		});

		//完成
		$upload.bind('complete', function(event, o) {
			Core.fn($zone, 'refresh')();
		});

	});
</script>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>