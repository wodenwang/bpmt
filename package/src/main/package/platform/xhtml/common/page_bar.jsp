<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/include/common.jsp"%>
<c:if test="${dp.limit>0}">
	<div class="ws-bar page" form="${form}" params="${params}" limit="${defLimit}">
		<div style="float: left; margin-top: 6px; position: absolute;">[${wpf:lan("#:zh[每页记录数]:en[Pagesize]#")}：${dp.limit}] ${wpf:lan("#:zh[总页数/总记录数]:en[TotalPage/TotalRecord]#")}：${dp.totalPage}/${dp.totalRecord}</div>
		<div style="float: right; text-align: right;">
			<span class="ws-group" style="float: left;">
				<button type="button" icon="seek-first" text="false" value="1">${wpf:lan("#:zh[第一页]:en[First page]#")}</button>
				<button type="button" icon="seek-prev" text="false" value="${dp.currentPage >1 ? (dp.currentPage-1) : 1}">${wpf:lan("#:zh[前一页]:en[Previous page]#")}</button>
			</span> <span class="ws-group" style="float: left;"> <c:set var="beginPage" value="${dp.currentPage <=3 ? 1 : dp.currentPage-3}" /> <c:set var="endPage"
					value="${dp.totalPage >= dp.currentPage+3 ? dp.currentPage+3 : dp.totalPage}" /> <c:if test="${beginPage!=1}">
					<button type="button" disabled="disabled">...</button>
				</c:if> <c:forEach begin="${beginPage}" end="${endPage }" varStatus="states">
					<c:choose>
						<c:when test="${states.index == dp.currentPage}">
							<button type="button" value="${states.index}" disabled="disabled">${states.index}</button>
						</c:when>
						<c:otherwise>
							<button type="button" value="${states.index}">${states.index}</button>
						</c:otherwise>
					</c:choose>
				</c:forEach> <c:if test="${endPage!=dp.totalPage}">
					<button type="button" disabled="disabled">...</button>
				</c:if>
			</span> <span class="ws-group" style="float: left;">
				<button type="button" text="false" icon="seek-next" value="${dp.totalPage > dp.currentPage ? (dp.currentPage+1) : dp.totalPage}">${wpf:lan("#:zh[下一页]:en[Next page]#")}</button>
				<button type="button" text="false" icon="seek-end" value="${dp.totalPage}">${wpf:lan("#:zh[最后页]:en[Last page]#")}</button>
			</span>
		</div>
	</div>
</c:if>