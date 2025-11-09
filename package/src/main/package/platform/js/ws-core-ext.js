if (Core == undefined) {
	Core = {};
}

/**
 * 前置处理
 * 
 * @param {}
 *            zone
 */
Core.initBefore = function(zone) {
	// 初始化分割线
	$.each($(zone + 'table.ws-table[group=true]'), function() {
		var $table = $(this);

		var col = $table.attr('col');
		if (col == undefined) {
			col = 2;
		}
		if ($('tr:first', $table).attr('group') != 'true') {// 首行不是分割线则插入分割线
			$table.prepend('<tr group="true"><th>(未分组)</th></tr>');
		}
		var $div = $('<div accordion="true" multi="true"></div>');
		$table.after($div);
		$.each($('tr', $table), function() {
			var $tr = $(this);
			if ($tr.parents('table:first').attr('group') == 'true') {
				if ($tr.attr('group') == 'true') {// 分割线
					var show = $tr.attr('show');
					if (show == undefined) {
						show = 'true';// 默认显示
					}
					var title = $('th', $tr).html();
					var msg = $('td', $tr).size() > 0 ? $('td', $tr).html() : '';
					$div.append('<div title="' + title + '" msg="' + msg + '" show="' + show + '"></div>');
				} else if ($tr.attr('self') == 'true') {// 独立一个区域
					var $last = $('div[msg]:last>:last', $div);
					var $tmpDiv = $('<div style="margin-bottom: 5px;" class="ws-scroll"></div>');
					$tmpDiv.html($('td', $tr).html());
					if ($last.size() < 1) {
						$('div[msg]:last', $div).append($tmpDiv);
					} else {
						$last.after($tmpDiv);
					}
				} else {
					var $last = $('div[msg]:last>:last', $div);
					if ($last.size() < 1) {
						var $tmpTable = $('<table class="ws-table" col="' + col + '"></table>');
						$tmpTable.append($tr);
						$('div[msg]:last', $div).append($tmpTable);
					} else if ($last.attr("col") == undefined) {
						var $tmpTable = $('<table class="ws-table" col="' + col + '"></table>');
						$tmpTable.append($tr);
						$last.after($tmpTable);
					} else {
						$last.append($tr);
					}
				}
			}
		});
		$table.remove();
	});

	// 初始化多行table
	$.each($(zone + 'table.ws-table[col]'), function() {
		var $table = $(this);
		var col = $table.attr('col');
		$('tbody>tr:first', $table).siblings().attr('remove', 'true');// 添加删除标识
		$('tbody>tr:first', $table).attr('remove', 'true');
		var $tr = null;
		$.each($('tr[remove=true]', $table), function() {
			if ($tr != null && $tr.attr("total") >= col) {
				$table.append($tr);
				$tr = null;
			}

			var $currentTr = $(this);
			if ($currentTr.attr('whole') == 'true') {// 占据整行
				if ($tr != null) {// 补全
					var total = $tr.attr('total');
					if (total < col) {
						for (var i = 0; i < col - total; i++) {
							$tr.append('<th></th>').append('<td></td>');
						}
					}
					$table.append($tr);
				}
				$tr = $('<tr total="' + (parseInt(col) - 1) + '" class="whole"></tr>');
			} else if ($tr == null) {
				$tr = $('<tr total="0" class="row"></tr>');
			}

			// 到这里不为null了
			$tr.append($currentTr.html());
			$tr.attr('total', parseInt($tr.attr('total')) + 1);
		});
		if ($tr != null) {
			var total = $tr.attr('total');
			if (total < col) {
				for (var i = 0; i < col - total; i++) {
					$tr.append('<th></th>').append('<td></td>');
				}
			}
			$table.append($tr);
		}

		$('tr[remove=true]', $table).remove();

		// 设置比例
		var thWidth = parseInt(100 / col * 0.3);
		var tdWidth = parseInt(100 / col * 0.7);
		$('tr.row', $table).children('th').css('width', thWidth + '%').css("min-width", "100px");
		$('tr.row', $table).children('td').css('width', tdWidth + '%');
		$('tr.whole', $table).children('th').css('width', thWidth + '%').css("min-width", "100px");
		$('tr.whole', $table).children('td').attr("colspan", (col * 2 - 1)).css('width', (100 - thWidth) + '%');
	});
};

/**
 * 后置处理
 * 
 * @param {}
 *            zone
 */
Core.initAfter = function(zone) {
	// reset按钮
	$(zone + 'button:reset').click(function(event) {
		event.preventDefault();
		var $this = $(this);
		var $form = $this.parents('form:first');
		Widget.initAll($form);
	});

};