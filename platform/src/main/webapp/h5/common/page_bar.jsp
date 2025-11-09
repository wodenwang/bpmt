<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/include/common.jsp"%>

<c:if test="${form!=null}">

	<script type="text/javascript">
		$(function() {
			var formid = '${form}';
			var $form = $("#" + formid);

			// 插入隐藏page
			if ($('input[name=_page]', $form).size() < 1) {
				$form.append('<input name="_page" type="hidden"/>');
			}
			// 插入隐藏limit
			if ($('input[name=_limit]', $form).size() < 1) {
				$form.append('<input name="_limit" type="hidden" value="${dp.limit}"/>');
			}

			// 按钮
			$('#${form}_pagination').find('a[page]').on('click', function() {
				var page = $(this).attr('page');
				$('input[name=_page]', $form).val(page);
				$form.submit();
			});

			//选择
			$('#${form}_pagination').find('select').on('change', function() {
				var page = $(this).val();
				$('input[name=_page]', $form).val(page);
				$form.submit();
			});
		});
	</script>

	<ul data-am-widget="pagination" class="am-pagination am-pagination-select" id="${form}_pagination">
		<li class="am-pagination-prev "><a href="#" class="" page="${dp.currentPage >1 ? (dp.currentPage-1) : 1}">上一页</a></li>
		<li class="am-pagination-select"><select>
				<c:forEach begin="1" end="${dp.totalPage}" varStatus="states">
					<c:choose>
						<c:when test="${states.index == dp.currentPage}">
							<option value="${states.index}" class="" selected="selected">${states.index}/${dp.totalPage}</option>
						</c:when>
						<c:otherwise>
							<option value="${states.index}" class="">${states.index}/${dp.totalPage}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
		</select></li>
		<li class="am-pagination-next "><a href="#" class="" page="${dp.totalPage > dp.currentPage ? (dp.currentPage+1) : dp.totalPage}">下一页</a></li>
	</ul>

</c:if>