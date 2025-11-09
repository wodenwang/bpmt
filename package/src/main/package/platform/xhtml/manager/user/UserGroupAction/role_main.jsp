<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//加载树
		Core.fn($zone, 'loadTree', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/roleTree.shtml');
		});

		//重新加载
		Core.fn('${_zone}_data_main_zone', 'refresh', function() {
			Core.fn($zone, 'loadTree')();
		});

		//删除节点
		Core.fn('${_zone}_tree_zone', 'delNode', function(node) {
			Ui.confirmPassword('是否删除角色[' + node.busiName + ']?.', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delRole.shtml', {
					data : {
						roleKey : node.roleKey
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
			Ajax.post('${_zone}_data_main_zone', '${_acp}/editRoleZone.shtml', {
				data : {
					roleKey : node.roleKey
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
					var obj = {};
					obj.roleKey = node.roleKey;
					obj.sort = i;
					tree.nodes.push(obj);
				});

				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/saveRoleSort.shtml', {
					data : {
						tree : JSON.stringify(tree)
					},
					callback : function(flag) {
						if (flag) {
							$('button[name=refresh]', $zone).click();
						}
					}
				});
			});
		});

		//新增按钮
		$('button[name=add]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createRoleZone.shtml');
		});

		Core.fn($zone, 'loadTree')();

	});
</script>
<div class="ws-bar">
	<div class="ws-group left">
		<button type="button" icon="refresh" text="true" name="refresh">重置位置</button>
		<button type="button" icon="disk" text="true" name="save">保存位置</button>
	</div>
	<div class="ws-group right">
		<button type="button" icon="plus" text="true" name="add">新增角色</button>
	</div>
</div>
<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="角色设置(拖拽调整排序)">
			<div id="${_zone}_tree_msg_zone"></div>
			<div id="${_zone}_tree_zone"></div>
		</div>
	</div>
	<div style="margin-left: 320px; overflow: auto; zoom: 1;" id="${_zone}_data_main_zone"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>