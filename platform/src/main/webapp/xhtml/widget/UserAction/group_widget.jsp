<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var $select = $('select', $zone);
		var $span = $('#${_zone}_span_zone', $zone);

		//删除
		var delValue = function(groupKey) {
			$('option[value="' + groupKey + '"]', $select).remove();
			showValue();

			//回调
			Core.fn($zone, 'change')($select);
		};

		//显示
		var showValue = function() {
			$span.children().remove();

			$("option:selected", $select).each(function() {
				var groupKey = $(this).val();
				var busiName = $(this).text();

				var $tmp = $('<span style="margin-right:10px;color:blue;"></span>');
				$tmp.html(busiName);

				//删除按钮
				if ('${param.state}' == 'normal') {
					var $del = $('<a href="javascript:void(0);" style="color:red;margin-left:5px;">[删]</a>');
					$del.attr('name', groupKey);
					$del.click(function() {
						var groupKey = $(this).attr('name');//唯一标识
						delValue(groupKey);
					});
					$tmp.append($del);
				}

				//图标
				{
					var $img = $('<img width="16" height="16" border="0"/>');
					$img.attr('src', _cp + '/css/icon/group.png');
					$tmp.prepend($img);
				}

				$span.append($tmp);
			});
		};

		//确认按钮
		var confirmFn = function($this) {
			var zTree = $.fn.zTree.getZTreeObj($('ul.ztree', $this).attr('id'));
			$select.children().remove();
			var nodes = zTree.getCheckedNodes();
			if (nodes != undefined) {
				$.each(nodes, function(index, node) {
					var $option = $('<option selected="selected" value="'+node.groupKey+'">' + node.busiName + '</option>');
					$select.append($option);
				});
			}
			showValue();
			$this.dialog("close");

			//回调
			Core.fn($zone, 'change')($select);
		};

		$('button', $zone).click(function() {
			var selectedArray = [];
			$("option:selected", $select).each(function() {
				selectedArray.push($(this).val());
			});

			var $win = Ajax.win('${_acp}/groupSelect.shtml', {
				title : '${wpf:lan("#:zh[组织选择]:en[Organization selection]#")}',
				minWidth : 300,
				minHeight : 400,
				data : {
					value : selectedArray,
					_params : $('textarea[paramName=true]', $zone).val(),
					checkType : '${param.checkType}'
				},
				buttons : [ {
					icons : {
						primary : "ui-icon-close"
					},
					text : '${wpf:lan("#:zh[取消]:en[Cancel]#")}',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					icons : {
						primary : "ui-icon-check"
					},
					text : '${wpf:lan("#:zh[确认]:en[Confirm]#")}',
					click : function() {
						var $this = $(this);
						confirmFn($this);
					}
				} ]
			});
			Core.fn($win, 'confirmFn', confirmFn);
		});

		//初始化展示
		showValue();
	});
</script>

<textarea style="display: none;" paramName="true" name="_tmp_params">${param._params}</textarea>
<c:choose>
	<c:when test="${param.checkType=='checkbox'}">
		<select style="display: none;" name="${param.name}" class="needValid ${param.validate}" multiple="multiple">
			<c:forEach items="${list}" var="vo">
				<option value="${vo.groupKey}" selected="selected">${vo.busiName}</option>
			</c:forEach>
		</select>
	</c:when>
	<c:otherwise>
		<select style="display: none;" name="${param.name}" class="needValid ${param.validate}">
			<c:forEach items="${list}" var="vo">
				<option value="${vo.groupKey}" selected="selected">${vo.busiName}</option>
			</c:forEach>
		</select>
	</c:otherwise>
</c:choose>

<span id="${_zone}_span_zone"></span>

<c:if test="${param.state=='normal'}">
	<button type="button" icon="arrowthick-1-nw" text="true">${wpf:lan("#:zh[选择]:en[Select]#")}</button>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>