<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

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

		$("select[name='quick_select']", $zone).change(function() {
			var name = $(this).val();
			if (name != '') {
				var busiName = $("option[value='" + name + "']", $(this)).attr('busiName');
				$("input[name$='name']", $zone).val(name);
				$("input[name$='busiName']", $zone).val(busiName);
				$("input[name$='busiName']", $zone).blur();
				$("select[name$='contentType']", $zone).val('1').trigger("liszt:updated");
				$("textarea[name$='contentScript']", $zone).val('return vo?.' + name + ';');
				$("textarea[name$='contentScript']", $zone).blur();
			}
		});

	});
</script>

<c:set var="pixel" value="${param.pixel}" />
<input type="hidden" name="${pixel}.flag" value="true" />
<table class="ws-table">
	<tr>
		<th>快速关联</th>
		<td><select name="quick_select" class="chosen">
				<option value="">组合字段</option>
				<c:forEach items="${table.tbColumns}" var="column">
					<option value="${column.name}" busiName="${column.description}">[${column.name}]${column.description}</option>
				</c:forEach>
		</select></td>
	</tr>
	<tr>
		<th>字段名</th>
		<td><wcm:widget name="${pixel}.name" cmd="text{required:true}" value="${vo.name}"></wcm:widget></td>
	</tr>
	<tr>
		<th>展示名</th>
		<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
	</tr>
</table>
<div accordion="true" multi="true">
	<div title="表单">
		<table class="ws-table">
			<tr>
				<th>绑定控件</th>
				<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${vo!=null?vo.widget:'text'}"></wcm:widget>
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
				<th>表单内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.contentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>表单内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]" value="${vo.contentScript}"></wcm:widget></td>
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
				<th>数据处理器(脚本类型)</th>
				<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.execType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>数据处理器(脚本)<br /> <font color="red" tip="true" title="返回当前字段的期望值.mode:1:新增时,2:修改时;vo:实体;fo:节点工作流信息;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]" value="${vo.execScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="样式布局">
		<table class="ws-table">
			<tr>
				<th>行展示模式</th>
				<td><wcm:widget name="${pixel}.whole" cmd="radio[@com.riversoft.platform.translate.TableLineMode]" value="${vo!=null?vo.whole:0}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="高级展示" msg="[展示]页中节点字段展示的内容.若留空则默认展示表单内容." show="false">
		<table class="ws-table">
			<tr>
				<th>展示内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.showContentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.showContentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>展示内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.showContentScript" cmd="codemirror[groovy]" value="${vo.showContentScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="条件">
		<table class="ws-table">
			<tr>
				<th>展示条件(脚本类型)</th>
				<td><wcm:widget name="${pixel}.decideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo!=null?vo.decideType:1}"></wcm:widget></td>
			</tr>
			<tr>
				<th>展示条件(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程对象;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.decideScript" cmd="codemirror[groovy]{required:true}" value="${vo!=null?vo.decideScript:'return true;'}"></wcm:widget></td>
			</tr>
			<tr>
				<th>可编辑条件(脚本类型)</th>
				<td><wcm:widget name="${pixel}.editDecideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo!=null?vo.editDecideType:1}"></wcm:widget></td>
			</tr>
			<tr>
				<th>可编辑条件(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程对象;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.editDecideScript" cmd="codemirror[groovy]{required:true}" value="${vo!=null?vo.editDecideScript:'return true;'}"></wcm:widget></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>