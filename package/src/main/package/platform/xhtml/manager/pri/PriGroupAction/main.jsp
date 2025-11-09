<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//加载树
		Core.fn($zone, 'loadTree', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/tree.shtml');
		});

		//删除节点
		Core.fn('${_zone}_tree_zone', 'delNode', function(groupId) {
			Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delNode.shtml', {
				data : {
					groupId : groupId
				},
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'loadTree')();
					}
				}
			});
		});

		//重新加载
		Core.fn('${_zone}_data_main_zone', 'refresh', function() {
			Core.fn($zone, 'loadTree')();
		});

		//编辑节点
		Core.fn('${_zone}_tree_zone', 'editZone', function(groupId) {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/editZone.shtml', {
				data : {
					groupId : groupId
				}
			});
		});

		//绑定刷新排序按钮
		$('button[name=refresh]', $zone).click(function() {
			Core.fn($zone, 'loadTree')();
			$('#${_zone}_data_main_zone').html('');
		});

		//保存排序按钮
		$('button[name=save]', $zone).click(function() {
			Ui.confirm('是否保存位置?', function() {
				var $ul = $('ul', $('#${_zone}_tree_zone'));
				var zTree = $.fn.zTree.getZTreeObj($ul.attr('id'));
				var nodes = zTree.transformToArray(zTree.getNodes());
				var tree = {};
				tree.nodes = new Array();//最终提交到服务端的数组
				$.each(nodes, function(i, node) {
					var parentNode = node.getParentNode();
					var obj = {};
					obj.groupId = node.groupId;
					if (parentNode != null) {
						obj.parentId = parentNode.groupId;
					} else {
						obj.parentId = null;
					}
					obj.sort = i;
					tree.nodes.push(obj);
				});

				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/saveSort.shtml', {
					data : {
						tree : JSON.stringify(tree)
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'loadTree')();
						}
					}
				});
			});
		});

		//新增按钮
		$('button[name=add]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createZone.shtml?leafFlag=1');
		});

		//新增文件夹
		$('button[name=addFolder]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createZone.shtml?leafFlag=0');
		});

		Core.fn($zone, 'loadTree')();

	});
</script>
<div tabs="true">
	<div title="权限组管理">
		<div class="ws-bar">
			<div class="ws-group left">
				<button type="button" icon="refresh" text="true" name="refresh">重置位置</button>
				<button type="button" icon="disk" text="true" name="save">保存位置</button>
			</div>
			<div class="ws-group right">
				<button type="button" icon="folder-collapsed" text="true" name="addFolder">新增文件夹</button>
				<button type="button" icon="key" text="true" name="add">新增权限组</button>
			</div>
		</div>
		<div style="overflow: auto; zoom: 1;">
			<div style="float: left; width: 300px;">
				<div panel="权限组(拖拽调整位置)">
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
			</div>
			<div style="margin-left: 320px; overflow: auto; zoom: 1;" id="${_zone}_data_main_zone"></div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>