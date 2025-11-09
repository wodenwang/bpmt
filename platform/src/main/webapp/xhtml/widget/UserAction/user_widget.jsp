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
		var delValue = function(uid) {
			$('option[value="' + uid + '"]', $select).remove();
			showValue();

			//回调
			Core.fn($zone, 'change')($select);
		};

		//显示
		var showValue = function() {
			$span.children().remove();

			$("option:selected", $select).each(function() {
				var uid = $(this).val();
				var busiName = $(this).text();

				var $tmp = $('<span style="margin-right:10px;color:blue;"></span>');
				if ('${param.codeFlag}' == '1') {//需要code
					$tmp.html("[" + uid + "]" + busiName);
				} else {
					$tmp.html(busiName);
				}

				//删除按钮
				if ('${param.state}' == 'normal') {
					var $del = $('<a href="javascript:void(0);" style="color:red;margin-left:5px;">[删]</a>');
					$del.attr('name', uid);
					$del.click(function() {
						var uid = $(this).attr('name');//唯一标识
						delValue(uid);
					});
					$tmp.append($del);
				}

				//图标
				{
					var $img = $('<img width="16" height="16" border="0"/>');
					$img.attr('src', _cp + '/css/icon/user.png');
					$tmp.prepend($img);
				}

				$span.append($tmp);
			});
		};

		var confirmFn = function($this, o) {
			if ('${param.checkType}' == 'radio') {//单选时才清空
				$select.children().remove();
			}

			if (o != undefined) {//选中
				var $option = $('<option selected="selected" value="' +o.val + '">' + o.busiName + '</option>');
				$select.append($option);
			} else {
				var $checkbox = $('[name=uids]:checked', $this);
				$.each($checkbox, function() {
					var busiName = $(this).parents('tr:first').attr("showName");
					var uid = $(this).val();
					if ($('option[value=' + uid + ']', $select).size() <= 0) {
						var $option = $('<option selected="selected" value="' +uid + '">' + busiName + '</option>');
						$select.append($option);
					}
				});
			}
			showValue();
			$this.dialog("close");

			//回调
			Core.fn($zone, 'change')($select);
		}

		$('button', $zone).click(function() {
			var selectedArray = [];
			$("option:selected", $select).each(function() {
				selectedArray.push($(this).val());
			});

			var $win = Ajax.win('${_acp}/userSelect.shtml', {
				title : '${wpf:lan("#:zh[组织用户]:en[Organize user]#")}',
				minWidth : 1025,
				data : {
					rootKey : '${param.rootKey}',
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
				<option value="${vo.uid}" selected="selected">${vo.busiName}</option>
			</c:forEach>
		</select>
	</c:when>
	<c:otherwise>
		<select style="display: none;" name="${param.name}" class="needValid ${param.validate}">
			<c:forEach items="${list}" var="vo">
				<option value="${vo.uid}" selected="selected">${vo.busiName}</option>
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