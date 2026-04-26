<#if list??>
	<ul data-am-widget="gallery" class="am-gallery am-avg-sm-1 am-avg-md-2 am-avg-lg-4 am-gallery-overlay" data-am-gallery="{pureview: true}">
		<#list list as vo>
			<li>
				<div class="am-gallery-item">
					<a href="${cp}/widget/FileAction/download.shtml?name=${vo.sysName}&type=${vo.type}&fileName=${vo.name}" class="">
						<#if vo.name?lower_case?ends_with(".jpg") || vo.name?lower_case?ends_with(".png") || vo.name?lower_case?ends_with(".gif") || vo.name?ends_with(".jpeg")>
							<img src="${cp}/widget/FileAction/download.shtml?name=${vo.sysName}&type=${vo.type}&fileName=${vo.name}" />
						<#else>
							<img src="data:image/png;base64,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" />
						</#if>
						<h3 class="am-gallery-title">${vo.name}</h3>
						<div class="am-gallery-desc">${vo.size}</div>
					</a>
				</div>
			</li>
		</#list>
	</ul>
</#if>
