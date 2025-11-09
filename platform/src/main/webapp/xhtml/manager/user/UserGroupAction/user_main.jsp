<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//加载树
		Core.fn($zone, 'loadTree', function(type, search) {
			if (!search) {
				search = '';
			}
			Ajax.post('${_zone}_tree_zone', '${_acp}/userTree.shtml', {
				data : {
					type : type,
					search : search
				}
			});
		});

		Core.fn('${_zone}_tree_zone', 'loadTree', function(type, search) {
			Core.fn($zone, 'loadTree')(type, search);
		});

		//重新加载
		Core.fn('${_zone}_data_main_zone', 'refresh', function() {
			var val = $(':radio[name=type]:checked', $zone).val();
			Core.fn($zone, 'loadTree')(val);
		});

		//删除节点
		Core.fn('${_zone}_tree_zone', 'delNode', function(node) {
			Ui.confirmPassword('是否删除用户[' + node.busiName + ']?.', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delUser.shtml', {
					data : {
						uid : node.uid
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
			Ajax.post('${_zone}_data_main_zone', '${_acp}/editUserZone.shtml', {
				data : {
					uid : node.uid
				}
			});
		});

		//保存排序
		Core.fn('${_zone}_tree_zone', 'saveSort', function() {
			Ui.confirm('是否保存位置?', function() {
				var $ul = $('ul', $('#${_zone}_tree_zone'));
				var zTree = $.fn.zTree.getZTreeObj($ul.attr('id'));
				var nodes = zTree.transformToArray(zTree.getNodes());
				var tree = {};
				tree.nodes = new Array();//最终提交到服务端的数组
				$.each(nodes, function(i, node) {
					var obj = {};
					obj.uid = node.uid;
					obj.sort = i;
					tree.nodes.push(obj);
				});

				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/saveUserSort.shtml', {
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

		//绑定刷新排序按钮
		$('button[name=refresh]', $zone).click(function() {
			Core.fn('${_zone}_data_main_zone', 'refresh')();
			$('#${_zone}_data_main_zone').html('');
		});

		//新增按钮
		$('button[name=add]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createUserZone.shtml');
		});

		//用户树查看类型
		$(':radio[name=type]', $zone).change(function() {
			var val = $(this).val();
			Core.fn($zone, 'loadTree')(val);
		});

		$(':radio[name=type]:first', $zone).click();

	});
</script>
<div class="ws-bar">
	<div class="left">
		<span class="ws-group"> <input type="radio" name="type" value="1" icheck="false" /><label>按用户名分类</label> <input type="radio" name="type" value="2" icheck="false" /><label>平铺排序</label>
		</span>
		<button name="refresh" icon="refresh" type="button">刷新</button>
	</div>
	<div class="ws-group right">
		<button type="button" icon="plus" text="true" name="add">新增用户</button>
	</div>
</div>
<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="用户列表">
			<div id="${_zone}_tree_msg_zone"></div>
			<div id="${_zone}_tree_zone"></div>
		</div>
	</div>
	<div style="margin-left: 320px; overflow: auto; zoom: 1;" id="${_zone}_data_main_zone"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>