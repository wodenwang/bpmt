<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>

<%-- js引入  --%>
<script src="../js/jquery-1.11.1.min.js"></script>
<script src="../js/jquery-migrate-1.2.1.min.js"></script>
<script src="../js/jquery-ui-1.11.1.min.js"></script>

</head>
<body>
	<script type="text/javascript">
		$(function() {
			if (window.parent) {
				$('[flow_editor=true]', window.parent.document).parent().find('button.ui-dialog-titlebar-close').click();
			}
			window.close();
		});
	</script>
</body>
</html>
