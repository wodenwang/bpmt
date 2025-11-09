<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//加载树
		Core.fn($zone, 'loadTree', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/tagTree.shtml');
		});

		//重新加载
		Core.fn('${_zone}_data_main_zone', 'refresh', function() {
			Core.fn($zone, 'loadTree')();
		});

		//删除节点
		Core.fn('${_zone}_tree_zone', 'delNode', function(node) {
			Ui.confirmPassword('是否删除标签[' + node.busiName + ']?.', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delTag.shtml', {
					data : {
						tagKey : node.tagKey
					},
					callback : function(flag) {
						if (flag) {
							$('button[name=refresh]', $zone).click();
						}
					}
				});
			});
		});

		//编辑节点
		Core.fn('${_zone}_tree_zone', 'editZone', function(node) {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/editTagZone.shtml', {
				data : {
					tagKey : node.tagKey
				}
			});
		});

		//绑定刷新按钮
		$('button[name=refresh]', $zone).click(function() {
			Core.fn($zone, 'loadTree')();
			$('#${_zone}_data_main_zone').html('');
		});

		//新增按钮
		$('button[name=add]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createTagZone.shtml');
		});
		
		//覆盖标签按钮
		$('button[name=cover]', $zone).click(function() {
			Ui.confirmPassword('是否全覆盖企业号标签?', function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/coverTag.shtml');
			});
		});
		
		//更新标签按钮
		$('button[name=upgrade]', $zone).click(function() {
			Ui.confirmPassword('是否安全更新企业号标签?', function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/upgradeTag.shtml');
			});
		});
		

		Core.fn($zone, 'loadTree')();

	});
</script>
<div class="ws-bar">
	<div class="ws-group left">
		<button type="button" icon="refresh" text="true" name="refresh">刷新</button>
	</div>
	<div class="ws-group right">
	    <button type="button" icon="transferthick-e-w" text="true" name="cover">覆盖标签</button>
	    <button type="button" icon="transferthick-e-w" text="true" name="upgrade">更新标签</button>
		<button type="button" icon="plus" text="true" name="add">新增标签</button>
	</div>
</div>
<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="标签管理">
			<div id="${_zone}_tree_msg_zone"></div>
			<div id="${_zone}_tree_zone"></div>
		</div>
	</div>
	<div style="margin-left: 320px; overflow: auto; zoom: 1;" id="${_zone}_data_main_zone"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>