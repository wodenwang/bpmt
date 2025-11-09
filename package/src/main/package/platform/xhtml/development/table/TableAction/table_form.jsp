<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%-- 设置标志,标志是添加还是更新 --%>
<c:set var="editFlag" value="${table!=null}" />
<c:set var="createFlag" value="${param.name==null}" />

<style type="text/css">
/*字段被编辑过的高亮样式*/
td.edited {
	color: red !important;
	font-weight: bold !important;
	font-style: oblique;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var editFlag = '${editFlag}';
		var initData = null;
		var $columnMap = $('textarea:first', $('div[name=columnMap]', $zone));
		if ($columnMap.size() > 0) {
			initData = eval('(' + $columnMap.val() + ')');
		}

		//重载
		Core.fn($zone, 'reload', function() {
			if ('true' == editFlag) {
				Ui.confirm('重置后当前编辑的内容会恢复到初始状态.是否继续?', function() {
					var name = '${table!=null?table.name:null}';
					Ajax.post('${_zone}', '${_acp}/editZone.shtml?name=' + name);
				});
			}
		});

		//打开窗口
		Core.fn($zone, 'openColumnFormWin', function(column) {
			var data = {};
			if (column) {
				data.column = JSON.stringify(column);
			}
			data.parentZone = '${_zone}';//父区域ID,方便弹出窗口能够找到回调函数
			Ajax.win('${_acp}/columnFormZone.shtml', {
				title : '增加字段',
				minWidth : 700,
				minHeight : 400,
				buttons : [ {
					text : '确定',
					click : function() {
						$('form', $(this)).submit();
					}
				} ],
				data : data
			});
		});

		//关闭tab
		Core.fn($zone, 'closeTab', function() {
			Ui.confirm('关闭后当前编辑的信息将不会被保存.是否继续?', function() {
				Ui.closeTab('${_zone}');
			});
		});

		/**
		 * 弹出框回调函数
		 */
		Core.fn($zone, 'addColumn', function(updateFlag, result) {
			if (result == null || result.name == null) {
				Ui.alert('设置数据有误,请重新设置.');
				return false;
			}

			var name = result.name;
			var $table = $('table[name=columns]', $zone);
			if (!updateFlag && $('tr[name="' + name + '"]', $table).size() > 0) {
				Ui.alert('字段名为[' + name + ']的字段已存在,不能重复添加.');
				return false;
			}

			//处理boolean类型的值(暂时先这样做吧)
			if (result.required != undefined && result.required == 1) {
				result.required = true;
			} else {
				result.required = false;
			}
			if (result.primaryKey != undefined && result.primaryKey == 1) {
				result.primaryKey = true;
			} else {
				result.primaryKey = false;
			}
			if (result.autoIncrement != undefined && result.autoIncrement == 1) {
				result.autoIncrement = true;
			} else {
				result.autoIncrement = false;
			}
			//处理长度
			if (result.totalSize == undefined) {
				result.totalSize = 0;
			}
			if (result.scale == undefined) {
				result.scale = 0;
			}

			var location = null;
			//使用模板处理数据
			if (updateFlag) {
				//记录位置(下一行tr的名字)
				var $tmpTr = $('tr[name=' + name + ']', $table).next('tr[name]');
				if ($tmpTr.size() > 0) {
					location = $tmpTr.attr('name');
				}
				//删掉原来的
				$('tr[name=' + name + ']', $table).remove();
			}

			var $tr = $('tr', $('table[name=template]', $zone)).clone(true, true);
			$tr.attr("name", name);
			$(':hidden[name=column]', $tr).val(JSON.stringify(result));
			//按钮
			$('button[icon=trash]', $tr).on('click', Core.fn($zone, 'del'));
			$('button[icon=wrench]', $tr).on('click', Core.fn($zone, 'edit'));
			$('td', $tr).addClass('center');
			$('.ws-group', $tr).buttonset();

			//设置固定值
			$('td[name=name]', $tr).html(result.name);
			//描述
			if (result.memo != '') {
				$('td[name=name]', $tr).attr('title', result.memo);
				$('td[name=name]', $tr).tooltip({
					track : true
				});
			}
			if (initData != null && initData[name] == null) {
				$('td[name=name]', $tr).addClass('edited');
			}

			$('td[name=description]', $tr).html(result.description);
			if (initData != null && (initData[name] == null || initData[name].description != result.description)) {
				$('td[name=description]', $tr).addClass('edited');
			}

			$('td[name=defaultValue]', $tr).html(result.defaultValue != undefined ? result.defaultValue : '');
			if (initData != null && (initData[name] == null || initData[name].defaultValue != result.defaultValue)) {
				$('td[name=defaultValue]', $tr).addClass('edited');
			}

			//主键
			if (result.primaryKey) {
				if (result.autoIncrement) {
					$('td[name=key]', $tr).html('主键,自动递增');
				} else {
					$('td[name=key]', $tr).html('主键');
				}
			} else {
				$('td[name=key]', $tr).html('非主键');
			}

			if (initData != null && (initData[name] == null || initData[name].primaryKey != result.primaryKey || initData[name].autoIncrement != result.autoIncrement)) {
				$('td[name=key]', $tr).addClass('edited');
			}

			//类型
			var $ul = $('ul[name=translateType]', $zone);
			var showName = $('li[code=' + result.mappedTypeCode + ']', $ul).html();
			$('td[name=type]', $tr).html(showName);
			if (initData != null && (initData[name] == null || initData[name].mappedTypeCode != result.mappedTypeCode)) {
				$('td[name=type]', $tr).addClass('edited');
			}

			//是否必须
			if (result.required) {
				$('td[name=required]', $tr).html('<b>必填</b>');
			} else {
				$('td[name=required]', $tr).html('可空');
			}
			if (initData != null && (initData[name] == null || initData[name].required != result.required)) {
				$('td[name=required]', $tr).addClass('edited');
			}

			//长度
			if (result.totalSize != undefined && result.totalSize > 0) {
				$('td[name=length]', $tr).append('长度:' + result.totalSize + ';');
			}
			if (result.scale != undefined && result.scale > 0) {
				$('td[name=length]', $tr).append('精度:' + result.scale + ';');
			}
			if (initData != null && (initData[name] == null || initData[name].totalSize != result.totalSize || initData[name].scale != result.scale)) {
				$('td[name=length]', $tr).addClass('edited');
			}

			if (location != null) {//记录过位置
				$('tr[name=' + location + ']', $table).before($tr);//插入到location前面
			} else {//没有位置信息
				$table.append($tr);
			}

			$table.styleTable();
			return true;
		});

		/**
		 * 删除
		 */
		Core.fn($zone, 'del', function() {
			var $this = $(this);
			var $tr = $this.parents('tr');
			Ui.confirm('是否删除该字段?', function() {
				var isCheck = $(':checkbox[name=keys]', $tr).attr("checked");
				if (isCheck != undefined && isCheck == 'checked') {
					$('select[name=generator]').val('').trigger('liszt:updated');
				}
				$tr.remove();
			});
		});
		/**
		 * 编辑
		 */
		Core.fn($zone, 'edit', function() {
			var $this = $(this);
			var $tr = $this.parents('tr');
			var $hidden = $(':hidden[name=column]', $tr);
			var column = eval('(' + $hidden.val() + ')');
			Core.fn($zone, 'openColumnFormWin')(column);
		});

		$('form', $zone).on("submit", function(event) {
			event.preventDefault();
			var option = {
				errorZone : '${_zone}_error_msg',
				confirmMsg : '是否提交本次操作?'
			};
			Core.fn('${_zone}', 'submitForm')($(this), option);
		});

		if ('true' == editFlag) {
			$('textarea', $('div[name=initColumn]', $zone)).each(function() {
				var val = $(this).val();
				var json = eval('(' + $(this).val() + ')');
				Core.fn($zone, 'addColumn')(true, json);
			});
		}

		//排序功能
		$('table[name=columns] tbody', $zone).sortable({
			helper : function(e, ui) {
				ui.children().each(function() {
					$(this).width($(this).width()); //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了  
				});
				return ui;
			},
			axis : "y"
		});

		//模式建表
		$('select[name=quickSelect]', $zone).on('change', function() {
			var val = this.value;
			if (val != '') {
				Ajax.post($zone, '${_acp}/modeZone.shtml', {
					errorZone : "${_zone}_error_msg",
					data : {
						tableName : $('input[name=name]', $zone).val(),
						mode : val
					}
				});
			}
		});

	});
</script>

<!-- 列map -->
<div style="display: none;" name="columnMap">
	<c:if test="${editFlag}">
		<textarea>${wcm:json(columnMap)}</textarea>
	</c:if>
</div>

<!--  初始化数据  -->
<div style="display: none;" name="initColumn">
	<c:forEach items="${table.tbColumns}" var="column">
		<textarea>${wcm:json(column)}</textarea>
	</c:forEach>
</div>

<%-- 枚举翻译 --%>
<ul style="display: none;" name="translateType">
	<c:forEach items="${types}" var="type">
		<li code="${type.code}">${type.showName}</li>
	</c:forEach>
</ul>

<%-- 模板数据 --%>
<table style="display: none;" name="template">
	<tr style="cursor: move;">
		<td class="ws-group">
			<button type="button" text="false" icon="wrench">编辑</button>
			<button type="button" text="false" icon="trash">删除</button> <input type="hidden" name="column" />
		</td>
		<td name="key"></td>
		<td name="name"></td>
		<td name="description"></td>
		<td name="type"></td>
		<td name="required"></td>
		<td name="length"></td>
		<td name="defaultValue"></td>
	</tr>
</table>

<div id="${_zone}_error_msg"></div>

<form action="${_acp}/submitForm.shtml" method="post" sync="true">
	<input type="hidden" name="createFlag" value="${createFlag?1:0}" /> <input type="hidden" name="cacheFlag" value="0" />
	<table class="ws-table">
		<tr>
			<th>表名</th>
			<td><wcm:widget name="name" cmd="text{required:true,maxlength:29,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}" value="${editFlag?table.name:'RV_'}"
					state="${createFlag?'normal':'readonly'}"></wcm:widget></td>
			<th>展示名</th>
			<td><wcm:widget name="description" cmd="text{required:true,maxlength:20}" value="${editFlag?table.description:''}"></wcm:widget></td>
		</tr>
		<c:if test="${createFlag}">
			<tr>
				<th>快速建表</th>
				<td colspan="3"><select class="chosen" name="quickSelect">
						<option value="">请选择模板</option>
						<c:forEach items="${modes}" var="m">
							<optgroup label="${m.key}">
								<c:forEach items="${m.value}" var="mode">
									<option value="${mode}">${mode}</option>
								</c:forEach>
							</optgroup>

						</c:forEach>
				</select></td>
			</tr>

		</c:if>
	</table>

	<div class="ws-bar">
		<div class="left">
			<button type="button" icon="plus" text="true" onclick="Core.fn('${_zone}','openColumnFormWin')();">增加字段</button>

		</div>
		<div class="right ws-group">
			<button type="button" icon="closethick" text="true" name="closeTab" onclick="Core.fn('${_zone}','closeTab')();">关闭</button>
			<c:if test="${!createFlag}">
				<button type="button" icon="refresh" text="true" onclick="Core.fn('${_zone}','reload')();">重置</button>
			</c:if>
			<button type="submit" icon="check" text="true">提交</button>
		</div>
	</div>

	<table class="ws-table" name="columns">
		<thead>
			<tr>
				<th>操作</th>
				<th>主键类型</th>
				<th>字段名</th>
				<th>展示名</th>
				<th>类型</th>
				<th>是否必须</th>
				<th>长度</th>
				<th>默认值</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>