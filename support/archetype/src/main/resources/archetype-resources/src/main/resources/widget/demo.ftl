#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<#-- 测试文本框,界面调用命令: demo --> 
<${symbol_pound}setting number_format="${symbol_pound}.${symbol_pound}${symbol_pound}">

<ul>
	<li>控件名:${symbol_dollar}{name}</li>
	<li>控件状态:${symbol_dollar}{state}</li>
	<li>控件验证规则:${symbol_dollar}{validate}</li>
	<li>控件值:${symbol_dollar}{value}</li>
</ul>

<input type="text" name="${symbol_dollar}{name}" value="${symbol_dollar}{value!''}" class="needValid ${symbol_dollar}{validate!''}" />