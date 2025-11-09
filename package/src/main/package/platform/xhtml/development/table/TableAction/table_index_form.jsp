<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

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
		var initData = null;
		var $indexMap = $('textarea:first', $('div[name=indexMap]', $zone));
		if ($indexMap.size() > 0) {
			initData = eval('(' + $indexMap.val() + ')');
		}

		//重载
		Core.fn($zone, 'reload', function() {
			Ui.confirm('重置后当前编辑的内容会恢复到初始状态.是否继续?', function() {
				var name = '${table!=null?table.name:null}';
				Ajax.post('${_zone}', '${_acp}/editIndex.shtml?tableName=' + name);
			});
		});

		//打开窗口
		Core.fn($zone, 'openIndexFormWin', function(tableName, index) {
			var data = {};

			data.tableName = tableName;
			if (index) {
				data.index = JSON.stringify(index);
			}

			data.parentZone = '${_zone}';//父区域ID,方便弹出窗口能够找到回调函数
			Ajax.win('${_acp}/indexFormZone.shtml', {
				title : '增加索引',
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
		Core.fn($zone, 'addIndex', function(result, create) {
			if (result == null || result.name == null) {
				Ui.alert('设置数据有误,请重新设置.');
				return false;
			}

			var name = result.name;
			var $table = $('table[name=indexes]', $zone);
			if (create && $('tr[name="' + name + '"]', $table).size() > 0) {
				Ui.alert('索引名为[' + name + ']的索引已存在,不能重复添加.');
				return false;
			}

			//处理boolean类型的值(暂时先这样做吧)
			if (result.unique != undefined && result.unique == 1) {
				result.unique = true;
			} else {
				result.unique = false;
			}

			var location = null;
			//使用模板处理数据
			//记录位置(下一行tr的名字)
			var $tmpTr = $('tr[name=' + name + ']', $table).next('tr[name]');
			if ($tmpTr.size() > 0) {
				location = $tmpTr.attr('name');
			}
			//删掉原来的
			$('tr[name=' + name + ']', $table).remove();

			var $tr = $('tr', $('table[name=template]', $zone)).clone(true, true);
			$tr.attr("name", name);
			$(':hidden[name=index]', $tr).val(JSON.stringify(result));
			//按钮
			$('button[icon=trash]', $tr).on('click', Core.fn($zone, 'del'));
			$('button[icon=wrench]', $tr).on('click', Core.fn($zone, 'edit'));
			$('td', $tr).addClass('center');
			$('.ws-group', $tr).buttonset();

			//name
			$('td[name=name]', $tr).html(result.name);
			if (initData != null && initData[name] == null) {
				$('td[name=name]', $tr).addClass('edited');
			}

			var indexedColumns = result.indexedColumns;
			if (indexedColumns != null && $.isArray(indexedColumns)) {
				var diff = false;
				var columns = [];
				for(var i = 0, len = indexedColumns.length; i < len; ++i) {
					var column = indexedColumns[i];
					columns.push(column.name);
				}

				$('td[name=indexedColumns]', $tr).html(columns.join(','));
				if (initData != null) {
					if(initData[name] == null) {
						diff = true;
					} else{
						var initIndexedColumns = initData[name].indexedColumns;
						if(initIndexedColumns.length == indexedColumns.length) {
							for(var i = 0, len = indexedColumns.length; i < len; ++i) {
								if(indexedColumns[i].name != initIndexedColumns[i].name) {
									diff = true;
									break;
								}
							}
						} else {
							diff = true;
						}
					}
				}
				if(diff) {
					$('td[name=indexedColumns]', $tr).addClass('edited');
				}
			} else{
				//单字段索引
				$('td[name=indexedColumns]', $tr).html(indexedColumns.name);
				if (initData != null && (initData[name] == null || initData[name].indexedColumns.name != indexedColumns.name)) {
					$('td[name=indexedColumns]', $tr).addClass('edited');
				}
			}


			//是否唯一
			if (result.unique) {
				$('td[name=unique]', $tr).html('<b>唯一</b>');
			} else {
				$('td[name=unique]', $tr).html('不唯一');
			}
			if (initData != null && (initData[name] == null || initData[name].unique != result.unique)) {
				$('td[name=unique]', $tr).addClass('edited');
			}

			//描述
			$('td[name=description]', $tr).html(result.description);
			if (initData != null && (initData[name] == null || initData[name].description != result.description)) {
				$('td[name=description]', $tr).addClass('edited');
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
			Ui.confirm('是否删除该索引?', function() {
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

			var $hiddenTable = $(':hidden[name=tableName]', $zone);
			var tableName = $hiddenTable.val();

			var $hiddenIndex = $(':hidden[name=index]', $tr);
			var index = eval('(' + $hiddenIndex.val() + ')');

			Core.fn($zone, 'openIndexFormWin')(tableName, index);
		});

		$('form', $zone).on("submit", function(event) {
			event.preventDefault();
			var option = {
				errorZone : '${_zone}_error_msg',
				confirmMsg : '是否提交本次操作?'
			};
			Core.fn('${_zone}', 'submitForm')($(this), option);
		});

		$('textarea', $('div[name=initIndex]', $zone)).each(function() {
			var val = $(this).val();
			var json = eval('(' + $(this).val() + ')');
			Core.fn($zone, 'addIndex')(json, false);
		});

		//排序功能
		$('table[name=indexes] tbody', $zone).sortable({
			helper : function(e, ui) {
				ui.children().each(function() {
					$(this).width($(this).width()); //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了  
				});
				return ui;
			},
			axis : "y"
		});

	});
</script>

<!-- 列map -->
<div style="display: none;" name="indexMap">
	<textarea>${wcm:json(indexMap)}</textarea>
</div>

<!--  初始化数据  -->
<div style="display: none;" name="initIndex">
	<c:forEach items="${table.tbIndexes}" var="index">
		<textarea>${wcm:json(index)}</textarea>
	</c:forEach>
</div>

<%-- 模板数据 --%>
<table style="display: none;" name="template">
	<tr style="cursor: move;">
		<td class="ws-group">
			<button type="button" text="false" icon="wrench">编辑</button>
			<button type="button" text="false" icon="trash">删除</button> <input type="hidden" name="index" />
		</td>
		<td name="name"></td>
		<td name="indexedColumns"></td>
		<td name="unique"></td>
		<td name="description"></td>
	</tr>
</table>

<div id="${_zone}_error_msg"></div>

<form action="${_acp}/submitIndexForm.shtml" method="post" sync="true">
	<input type="hidden" name="tableName" value="${table.name}" />

	<div class="ws-bar">
		<div class="left">
			<button type="button" icon="plus" text="true" onclick="Core.fn('${_zone}','openIndexFormWin')('${table.name}');">增加索引</button>

		</div>
		<div class="right ws-group">
			<button type="button" icon="closethick" text="true" name="closeTab" onclick="Core.fn('${_zone}','closeTab')();">关闭</button>
			<button type="button" icon="refresh" text="true" onclick="Core.fn('${_zone}','reload')();">重置</button>
			<button type="submit" icon="check" text="true">提交</button>
		</div>
	</div>

	<table class="ws-table" name="indexes">
		<thead>
		<tr>
			<th>操作</th>
			<th>索引名</th>
			<th>索引字段</th>
			<th>是否唯一</th>
			<th>描述</th>
		</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>