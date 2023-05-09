<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Izvodjenja na sceni</title>
</head>
<body>
<form action="/Pozoriste/predstave/getIzvodjenjaZaScenu" method="get">
		Scena:<select name="idScena">
			<c:forEach items="${scene}" var="s">
				<option value="${s.idScena} "> ${s.naziv}</option>
			</c:forEach>
		</select>
		<input type="submit" value="Prikaz">
	</form>
	<c:if test="${! empty izvodjenja }">
		<table border="1">
			<tr><th>Datum</th><th>Naziv</th><th>Trajanje</th><th>Uloge</th></tr>
			<c:forEach items="${izvodjenja}" var="i">
				<tr>
					<td>${i.datum}</td>
					<td>${i.predstava.naziv }</td>
					<td>${i.predstava.trajanje }</td>
					<td><a href="/Pozoriste/predstave/getUlogeZaIzvodjenje?idIzvodjenja=${i.idIzvodjenje}">Uloge</a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</body>
</html>