<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="updateFlag" value="${index!=null}" />

<style type="text/css">
	ul.sortList {
		list-style-type: none;
		padding: 0;
		margin-top: 40px;
		width: 100%;
		position: relative;
		overflow: hidden;
		zoom: 1;
		min-height: 30px;
	}

	ul.sortList li {
		margin: 5px;
		padding: 3px;
		float: left;
		position: relative;
		cursor: move;
	}

	.portlet-placeholder {
		border: 1px dotted black;
		margin: 0 1em 1em 0;
		height: 20px;
		width: 80px;
	}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var updateFlag = '${updateFlag}';
		var $form = $('form', $('#${_zone}'));
		var valiForm = $form.validate({
			errorPlacement : function(error, element) { // 错误信息位置设置方法
				var $parent = element.parents(':not(.ui-spinner):first');
				error.appendTo($parent); // 这里的element是录入数据的对象
			}
		});
		$form.data("validator").settings.ignore += ':not(.chzn-done)';
		$form.on("submit", function() {
			if (valiForm.form()) {
				var jsonArray = $form.serializeArray();
				var json = {};
				json['indexedColumns'] = [];

				$.each(jsonArray, function(i, field) {
					var key = field.name;
					var val = field.value;
					if(val != null) {
						if(key == 'indexedColumns') {
							val = $.parseJSON(val);
							json['indexedColumns'].push(val);
						} else{
							if (json[key] == null) {
								json[key] = val;
							} else {
								if (!json[key].push) {
									json[key] = [ json[key] ];
								}
								json[key].push(val);
							}
						}
					}

				});

				if (Core.fn('${param.parentZone}', 'addIndex')(json, ${!updateFlag})) {//回调
					$('#${_zone}').dialog("close");
				}
			}
			return false;
		});

		var sort = function() {
			var $wait = $('ul[name=wait]', $zone);
			var $result = $('ul[name=result]', $zone);
			$('textarea', $wait).prop('disabled', true);
			$('textarea', $result).prop('disabled', false);
		};

		$(".sortList", $zone).sortable({
			connectWith : ".sortList",
			cursor : 'move',
			placeholder : "portlet-placeholder",
			stop : function(event, ui) {
				sort();
			}
		});

		$("div", $zone).disableSelection();

		//初始化数据
		if (updateFlag == 'true') {
			var index = eval('(' + $('#${_zone}_index_data', $zone).val() + ')');

			var $form = $('form', $('#${_zone}'));
			$.each(index, function(key, value) {
				if (key == 'unique') {
					$(":radio[name='" + key + "'][value='" + (value ? 1 : 0) + "']", $form).iCheck("check");
				} else if (key == "name" || key == "description"){
					$("[name='" + key + "']", $form).val(value);
				}
			});
			$('input[name="name"]', $form).attr('readonly', 'readonly');
		}
	});
</script>

<!-- index数据 -->
<textarea style="display: none;" id="${_zone}_index_data">${index!=null?index:'{}'}</textarea>

<form sync="true">
	<table class="ws-table">
		<tr>
			<th>索引名</th>
			<td title="只允许使用大写英文,数字和下划线" tip="true">
				<wcm:widget name="name" cmd="text{required:true,maxlength:20,pattern2:['[A-Z]{1}[A-Z0-9_]*','只允许使用大写英文,数字和下划线']}"></wcm:widget>
			</td>
		</tr>

		<tr>
			<th>索引字段</th>
			<td>
				<div style="border-style: dashed; border-width: 1px; background-color: #ffffee;">
					<span class="ui-state-default" style="font-weight: bold; float: right;">已选字段</span>
					<ul name="result" class="sortList">
						<c:forEach items="${selectedColumns}" var="vo">
							<li class="ui-state-default"><font color="red" style="font-weight: bold;">${vo.name}</font> <textarea style="display: none;" name="indexedColumns">{"tableName":"${vo.tableName}","name":"${vo.name}"}</textarea></li>
						</c:forEach>
					</ul>
				</div>

				<div style="border-style: dashed; border-width: 1px; background-color: #ffffff; margin-top: 5px;">
					<span class="ui-state-default" style="font-weight: bold; float: right;">待选字段<font color="blue" tip="true" title="非主键且非空字段才能使用索引">(提示)</font></span>
					<ul name="wait" class="sortList">
						<c:forEach items="${allColumns}" var="vo">
							<li class="ui-state-default"><font color="blue" style="font-weight: bold;">${vo.name}</font> <textarea disabled="disabled" style="display: none;" name="indexedColumns">{"tableName":"${vo.tableName}","name":"${vo.name}"}</textarea></li>
						</c:forEach>
					</ul>
				</div>
			</td>
		</tr>

		<tr>
			<th>是否唯一</th>
			<td><wcm:widget name="unique" cmd="radio[YES_NO]{required:true}" value="0"></wcm:widget></td>
		</tr>

		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea"></wcm:widget></td>
		</tr>
	</table>
	<!-- 附属属性 -->
	<div id="${_zone}_field_type_zone"></div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>