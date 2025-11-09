<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $mainZone = $('#${_zone}_main_zone');

		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
			data : {
				key : {
					name : "description"
				},
				simpleData : {
					enable : true,
					idKey : "name"
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					//先隐藏
					$mainZone.children().hide();
					//查找在不在
					var $target = $('div[name="' + treeNode.name + '"]', $mainZone);
					if ($target.size() > 0) {
						//存在
						$target.show();
					} else {
						//不存在
						$target = $('<div name="'+treeNode.name+'"></div>');
						$mainZone.append($target);
						var url;
						var data = {
							widgetKey : '${param.widgetKey}',
							name : treeNode.name
						};
						Ajax.post($target, '${_acp}/extForm.shtml', {
							data : data
						});
					}
				}
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		zTree.expandAll(true);

		//重新加载
		$('button[name=refresh]', $zone).click(function() {
			Ui.confirm('确认重新加载?', function() {
				Ajax.post($zone, '${_acp}/extConfig.shtml', {
					data : {
						widgetKey : '${param.widgetKey}'
					}
				});
			});
		});

	});
</script>

<input type="hidden" name="hasColumns" value="true" />
<div class="ws-bar">
	<div class="right">
		<button type="button" icon="refresh" name="refresh">重新加载</button>
	</div>
</div>

<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="控件类型">
			<ul id="${_zone}_tree">
				<textarea>${wcm:json(list)}</textarea>
			</ul>
		</div>
	</div>
	<div style="margin-left: 310px; overflow: auto; zoom: 1;" id="${_zone}_main_zone"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>