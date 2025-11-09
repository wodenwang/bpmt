<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="pixel" value="${param.pixel}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('input[name$=".busiName"]', $zone).blur(function() {
			var val = $(this).val();
			if (val != null && val != '') {
				var ztree = $.fn.zTree.getZTreeObj("${param.treeId}");
				var node = ztree.getSelectedNodes();
				if (node.length > 0) {
					node[0].busiName = val;
					ztree.refresh();
					ztree.selectNode(node[0]);
				}
			}
		});

		//复选框事件,使用icheck
		$('[name=columnFunction]', $zone).on('ifChanged', function() {
			var type = $(this).val();
			var flag = $(this).prop("checked");
			if (flag) {//选中
				$("div[" + type + "='true']", $zone).show().prev().show();
				$('input:hidden[name$=".' + type + '"]', $zone).val('1');
			} else {//不选中
				$("div[" + type + "='true']", $zone).hide().prev().hide();
				$('input:hidden[name$=".' + type + '"]', $zone).val('0');
			}
		});

		//初始化复选框
		$('[name=columnFunction]', $zone).each(function() {
			var $this = $(this);
			var type = $this.val();
			var val = $('input:hidden[name$=".' + type + '"]', $zone).val();
			if (val == '1') {
				$this.iCheck('check');
				$("div[" + type + "='true']", $zone).show().prev().show();
			} else {
				$("div[" + type + "='true']", $zone).hide().prev().hide();
			}
		});

	});
</script>

<input type="hidden" name="${pixel}.flag" value="true" />
<input type="hidden" name="${pixel}.name" value="${vo.name}" />

<input type="hidden" name="${pixel}.showFlag" value="${vo!=null?vo.showFlag:1}" />
<input type="hidden" name="${pixel}.formFlag" value="${vo!=null?vo.formFlag:1}" />

<table class="ws-table">
	<tr>
		<th>字段名</th>
		<td>${vo.name}</td>
	</tr>
	<tr>
		<th>展示名</th>
		<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
	</tr>
	<tr>
		<th>字段功能</th>
		<td><input type="checkbox" value="showFlag" name="columnFunction" /><label>做为展示</label><input type="checkbox" value="formFlag" name="columnFunction" /><label>做为表单</label></td>
	</tr>
</table>
<div accordion="true" multi="true">
	<div title="展示" showFlag="true">
		<table class="ws-table">

			<tr>
				<th>展示内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.contentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>展示内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;mode:列表页=1;明细页=2;表单页=3;导出时=4;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]{required:true}" value="${vo.contentScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="表单" formFlag="true">
		<table class="ws-table">
			<tr>
				<th>绑定控件</th>
				<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${vo.widget}"></wcm:widget>
			</tr>
			<tr>
				<th>控件动态入参(脚本类型)</th>
				<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.widgetParamType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>控件动态入参(脚本)<br /> <font color="red" tip="true" title="vo:实体;返回字符串,在自定义控件中在request中通过[_params]命令字获取.">(提示)</font></th>
				<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" value="${vo.widgetParamScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>新增默认值(脚本类型)</th>
				<td><wcm:widget name="${pixel}.widgetContentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.widgetContentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>新增默认值(脚本)<br /> <font color="red" tip="true" title="新增时有效">(提示)</font></th>
				<td><wcm:widget name="${pixel}.widgetContentScript" cmd="codemirror[groovy]" value="${vo.widgetContentScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>填写提示(脚本类型)</th>
				<td><wcm:widget name="${pixel}.tipType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.tipType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>填写提示(脚本)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.tipScript" cmd="codemirror[groovy]" value="${vo.tipScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="处理器">
		<table class="ws-table">
			<tr>
				<th>字段数据处理器(脚本类型)</th>
				<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>字段数据处理器(脚本)<br /> <font color="red" tip="true" title="返回当前字段的期望值.vo:实体;mode:新增时=1;修改时=2;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]" value="${vo.execScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="样式布局">
		<table class="ws-table">
			<tr>
				<th>单元格样式</th>
				<td><wcm:widget name="${pixel}.style" cmd="style" value="${vo.style}"></wcm:widget></td>
			</tr>
			<tr>
				<th>行展示模式</th>
				<td><wcm:widget name="${pixel}.whole" cmd="radio[@com.riversoft.platform.translate.TableLineMode]" value="${vo!=null?vo.whole:0}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="权限" msg="mode!=1时上下文可使用vo.">
		<table class="ws-table">
			<tr>
				<th>功能点(展示)</th>
				<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}" value="${vo.pri}"></wcm:widget></td>
			</tr>
			<tr>
				<th>功能点(录入)</th>
				<td><c:choose>
						<c:when test="${vo.primaryKey&&vo.autoIncrement}">
							<font color="red">(自动主键由系统生成,无需设置权限)</font>
						</c:when>
						<c:otherwise>
							<wcm:widget name="${pixel}.createPri" cmd="pri" value="${vo.createPri}"></wcm:widget>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>功能点(修改)</th>
				<td><c:choose>
						<c:when test="${vo.primaryKey}">
							<font color="red">(主键值不允许修改,无需设置权限)</font>
						</c:when>
						<c:otherwise>
							<wcm:widget name="${pixel}.updatePri" cmd="pri[vo:实体]" value="${vo.updatePri}"></wcm:widget>
						</c:otherwise>
					</c:choose></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>