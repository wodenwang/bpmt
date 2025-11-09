<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//刷新资源数量
		Core.fn($zone, 'getCounts', function() {
			Ajax.json('${_acp}/getResourceCount.shtml', function(result) {
				Ui.changeCurrentTitle('${_zone}_image_tab', '图片<font color="red">[' + result.image_count + ']</font>');
				Ui.changeCurrentTitle('${_zone}_voice_tab', '语音<font color="red">[' + result.voice_count + ']</font>');
				Ui.changeCurrentTitle('${_zone}_video_tab', '视频<font color="red">[' + result.video_count + ']</font>');
				Ui.changeCurrentTitle('${_zone}_file_tab', '文件<font color="red">[' + result.file_count + ']</font>');
				Ui.changeCurrentTitle('${_zone}_mpnews_tab', '图文<font color="red">[' + result.mpnews_count + ']</font>');
			}, {
				errorZone : '${_zone}_loading_zone',
				data : {
					agentKey : '${param.agentKey}'
				}
			});
		});

		//删除资源
		Core.fn($zone, 'delete', function($target, mediaId) {
			Ui.confirm('确认删除资源[' + mediaId + ']?', function() {
				Ajax.post('${_zone}_msg_zone', '${_acp}/deleteResource.shtml', {
					data : {
						agentKey : '${param.agentKey}',
						mediaId : mediaId
					},
					callback : function(flag) {
						if (flag) {
							Ajax.post($target, $target.attr('loaded'));
							Core.fn($zone, 'getCounts')();
						}
					}
				});
			});
		});

		Core.fn('${_zone}_image_tab', 'delete', function(mediaId) {
			Core.fn($zone, 'delete')($("#${_zone}_image_tab"), mediaId);
		});
		Core.fn('${_zone}_voice_tab', 'delete', function(mediaId) {
			Core.fn($zone, 'delete')($("#${_zone}_voice_tab"), mediaId);
		});
		Core.fn('${_zone}_video_tab', 'delete', function(mediaId) {
			Core.fn($zone, 'delete')($("#${_zone}_video_tab"), mediaId);
		});
		Core.fn('${_zone}_file_tab', 'delete', function(mediaId) {
			Core.fn($zone, 'delete')($("#${_zone}_file_tab"), mediaId);
		});
		Core.fn('${_zone}_mpnews_tab', 'delete', function(mediaId) {
			Core.fn($zone, 'delete')($("#${_zone}_mpnews_tab"), mediaId);
		});

		//上传窗口
		$('button[name=upload]', $zone).click(function() {
			var $win = Ajax.win('${_acp}/addResource.shtml', {
				title : '资源上传(请将资源拖拽到窗口中)',
				minWidth : 1024,
				data : {
					agentKey : '${param.agentKey}'
				}
			});

			Core.fn($win, 'refresh', function() {
				$win.dialog("close");
				Ajax.post($zone, '${_acp}/resource.shtml?agentKey=${param.agentKey}');
			});
		});

		//初始化调用
		Core.fn($zone, 'getCounts')();

	})
</script>

<div class="ws-bar">
	<div class="left">
		<button type="button" icon="image" name="upload">上传资源</button>
	</div>
</div>

<div id="${_zone}_loading_zone"></div>
<div id="${_zone}_msg_zone"></div>

<div tabs="true" button="left">
	<div title="图片" id="${_zone}_image_tab" init="${_acp}/listResource.shtml?type=image&agentKey=${param.agentKey}"></div>
	<div title="语音" id="${_zone}_voice_tab" init="${_acp}/listResource.shtml?type=voice&agentKey=${param.agentKey}"></div>
	<div title="视频" id="${_zone}_video_tab" init="${_acp}/listResource.shtml?type=video&agentKey=${param.agentKey}"></div>
	<div title="文件" id="${_zone}_file_tab" init="${_acp}/listResource.shtml?type=file&agentKey=${param.agentKey}"></div>
	<div title="图文" id="${_zone}_mpnews_tab" init="${_acp}/listResource.shtml?type=mpnews&agentKey=${param.agentKey}"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>