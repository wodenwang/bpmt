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

		//重新加载
		Core.fn('${_zone}_data_main_zone', 'refresh', function() {
			Core.fn($zone, 'loadTree')();
		});

		//删除节点
		Core.fn('${_zone}_tree_zone', 'delNode', function(node) {
			if (node.roleKey) {//删除角色关系
				var parentNode = node.getParentNode();
				Ui.confirm('确认删除组织[' + parentNode.busiName + ']下的角色[' + node.busiName + ']?', function() {
					Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delGroupRole.shtml', {
						data : {
							groupKey : parentNode.groupKey,
							roleKey : node.roleKey
						},
						callback : function(flag) {
							if (flag) {
								$('button[name=refresh]', $zone).click();
							}
						}
					});
				});
			} else {
				Ui.confirmPassword('是否删除组织[' + node.busiName + ']?本次操作会让该组织下属所有直接挂到根路径.', function() {
					Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delGroup.shtml', {
						data : {
							groupKey : node.groupKey
						},
						callback : function(flag) {
							if (flag) {
								$('button[name=refresh]', $zone).click();
							}
						}
					});
				});
			}

		});

		//编辑节点
		Core.fn('${_zone}_tree_zone', 'editZone', function(node) {
			if (node.roleKey) {//编辑角色关系
				var parentNode = node.getParentNode();
				Ajax.post('${_zone}_data_main_zone', '${_acp}/editGroupRoleZone.shtml', {
					data : {
						groupKey : parentNode.groupKey,
						roleKey : node.roleKey,
						errorZone : '${_zone}_data_msg_zone'
					}
				});
			} else {
				Ajax.post('${_zone}_data_main_zone', '${_acp}/editGroupZone.shtml', {
					data : {
						groupKey : node.groupKey,
						errorZone : '${_zone}_data_msg_zone'
					}
				});
			}
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
					if (node.groupKey) {
						var obj = {};
						var parentNode = node.getParentNode();
						obj.groupKey = node.groupKey;
						if (parentNode != null) {
							obj.parentKey = parentNode.groupKey;
						} else {
							obj.parentKey = null;
						}
						obj.sort = i;
						tree.nodes.push(obj);
					}
				});

				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/saveSort.shtml', {
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
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createGroupZone.shtml', {
				data : {
					errorZone : '${_zone}_data_msg_zone'
				}
			});
		});

		//同步组织架构到企业号
		$('button[name=syncWx]').click(function() {
			Ui.confirmPassword('确认同步系统组织架构到微信企业号?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/syncGroup.shtml');
			});
		});

		Core.fn($zone, 'loadTree')();

	});
</script>
<div tabs="true">
	<div title="组织架构管理">
		<div class="ws-bar">
			<div class="left ws-group">
				<button type="button" icon="refresh" text="true" name="refresh">重置位置</button>
				<button type="button" icon="disk" text="true" name="save">保存位置</button>
			</div>
			<div class="right">
				<button type="button" icon="transferthick-e-w" text="true" name="syncWx">同步至企业号</button>
				<button type="button" icon="plus" text="true" name="add">新增组织</button>
			</div>
		</div>
		<div style="overflow: auto; zoom: 1;">
			<div style="float: left; width: 300px;">
				<div panel="组织结构(拖拽调整位置)">
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
			</div>
			<div style="margin-left: 320px; overflow: auto; zoom: 1;">
				<div id="${_zone}_data_msg_zone"></div>
				<div id="${_zone}_data_main_zone"></div>
			</div>
		</div>
	</div>
	<div title="角色管理" init="${_acp}/roleManage.shtml"></div>
	<div title="标签管理" init="${_acp}/tagManage.shtml"></div>
	<div title="用户管理" init="${_acp}/userManage.shtml"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>