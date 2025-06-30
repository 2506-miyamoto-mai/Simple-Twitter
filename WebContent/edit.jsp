<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>簡易Twitter</title>
<link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<!-- ログイン機能実装のためにheader追加 -->
	<div class="header">
		<c:if test="${ empty loginUser }">
			<a href="login">ログイン</a>
			<a href="signup">登録する</a>
		</c:if>
		<c:if test="${ not empty loginUser }">
			<a href="./">ホーム</a>
			<a href="setting">設定</a>
			<a href="logout">ログアウト</a>
		</c:if>
	</div>
	<div class="form-area">
		<form action="edit" method="post">
			つぶやき<br />
			<!-- name…getParamするときに必要 -->
			<textarea name="text" cols="100" rows="5" class="tweet-box"><c:out value="${message.text}" /></textarea>
			<br />
			<button type="submit" name="messageId" value="${message.id}">更新</button>
			<a href="./">戻る</a>
		</form>
	</div>


	<div class="copyright">Copyright(c)MiyamotoMai</div>
</body>
</html>