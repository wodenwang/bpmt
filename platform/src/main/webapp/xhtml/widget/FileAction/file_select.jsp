<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$(':radio', $zone).on("click", function(event) {
			var type = $(this).val();
			Ajax.post('${_zone}_tree_zone', '${_acp}/currentFileTree.shtml', {
				data : {
					zone : '${_zone}',
					type : type
				}
			});
		});

		Core.fn("${_zone}_tree_zone", 'showDetail', function(name) {
			Ajax.post('${_zone}_detail_zone', '${_acp}/fileDetail.shtml?fileName=' + name);
		});

		Core.fn("${_zone}_tree_zone", 'delete', function(name) {
			Ajax.post('${_zone}_msg', '${_acp}/delete.shtml', {
				data : {
					fileName : name
				},
				callback : function(flag) {
					$(':radio:checked', $zone).click();
				}
			});
		});

		Core.fn($zone, 'getCheckNode', function() {
			var zTree = $.fn.zTree.getZTreeObj("${_zone}_tree_zone_tree");
			var array = zTree.getCheckedNodes(true);
			if (array.length < 1) {
				return null;
			}
			return array;
		});

		$(':radio:first', $zone).click();
	});
</script>

<div style="position: relative;">

	<div class="ws-bar">
		<div class=" ws-group left">
			<input type="radio" name="type" value="BY_DATE" icheck="false" /><label>${wpf:lan("#:zh[按时间]:en[By time]#")}</label> <input type="radio" name="type" value="BY_FILE_TYPE" icheck="false" /><label>${wpf:lan("#:zh[按类型]:en[By type]#")}</label> <input type="radio"
				name="type" value="BY_FILE_SIZE" icheck="false" /><label>${wpf:lan("#:zh[按大小]:en[By size]#")}</label> <input type="radio" name="type" value="BY_CLIENT" icheck="false" /><label>${wpf:lan("#:zh[按客户端]:en[By client]#")}</label>
		</div>
	</div>

	<div style="position: absolute; float: left; width: 450px;">
		<div id="${_zone}_tree_zone" checkType="${checkType}"></div>
	</div>

	<div style="margin-left: 460px; height: 300px;">
		<div id="${_zone}_detail_zone"></div>
	</div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>