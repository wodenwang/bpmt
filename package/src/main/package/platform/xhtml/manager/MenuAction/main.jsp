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
			if (node.sysFlag) {
				return;
			}

			if (node.type == 1) {//删除域
				Ui.confirm('是否删除域[' + node.busiName + ']?此操作会让其下属菜单变成无主记录.', function() {
					Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delDomainNode.shtml', {
						data : {
							domainKey : node.domainKey
						},
						callback : function(flag) {
							if (flag) {
								$('button[name=refresh]', $zone).click();
							}
						}
					});
				});
			} else {//删除菜单
				Ui.confirm('是否删除菜单[' + node.busiName + ']?', function() {
					Ajax.post('${_zone}_tree_msg_zone', '${_acp}/delMenuNode.shtml', {
						data : {
							id : node.menuKey
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

		//编辑菜单
		Core.fn('${_zone}_tree_zone', 'editZone', function(node) {
			if (node.type == 1) {//编辑域
				Ajax.post('${_zone}_data_main_zone', '${_acp}/editDomainZone.shtml', {
					data : {
						domainKey : node.domainKey
					}
				});
			} else {
				Ajax.post('${_zone}_data_main_zone', '${_acp}/editMenuZone.shtml', {
					data : {
						id : node.menuKey
					}
				});
			}
		});

		//绑定刷新排序按钮
		$('button[name=refresh]', $zone).click(function() {
			Core.fn('${_zone}_data_main_zone', 'refresh')();
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
					if (!node.unManageFlag) {
						var parentNode = node.getParentNode();
						var obj = {};
						obj.type = node.type;
						if (node.type != 1) {//菜单
							obj.id = node.menuKey;
							obj.domainKey = node.domainKey;
							if (parentNode.type == 1) {
								obj.parentId = null;
							} else {
								obj.parentId = parentNode.menuKey;
							}
						} else {
							obj.domainKey = node.domainKey;
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
		$('button[name=addDomain]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createDomainZone.shtml');
		});

		$('button[name=addMenu]', $zone).click(function() {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/createMenuZone.shtml');
		});

		$('button[name=refresh]', $zone).click();

	});
</script>
<div tabs="true">
	<div title="菜单设置">
		<div class="ws-bar">
			<div class="ws-group left">
				<button type="button" icon="refresh" text="true" name="refresh">重置位置</button>
				<button type="button" icon="disk" text="true" name="save">保存位置</button>
			</div>
			<div class="ws-group right">
				<button type="button" icon="plus" text="true" name="addDomain">新增域</button>
				<button type="button" icon="plus" text="true" name="addMenu">新增菜单</button>
			</div>
		</div>
		<div style="overflow: auto; zoom: 1;">
			<div style="float: left; width: 300px;">
				<div panel="菜单(拖拽调整位置)">
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
			</div>
			<div style="margin-left: 320px; overflow: auto; zoom: 1;"
				id="${_zone}_data_main_zone"></div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>