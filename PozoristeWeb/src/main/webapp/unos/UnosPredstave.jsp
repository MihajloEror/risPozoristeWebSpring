<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Unos predstave</title>
</head>
<body>
<form action="/Pozoriste/predstave/savePredstava" method="post">
		<table>
			<tr><td>Naziv:</td><td><input name="naziv"></td></tr>
			<tr><td>Trajanje:</td><td><input name="trajanje"></td></tr>
			<tr><td>Opis:</td><td><input name="opis"></td></tr>
			<tr><td>Reziser:</td>
				<td>
					<select name="reziser">
						<c:forEach items="${reziseri}" var="r">
							<option value="${r.idReziser }">${r.ime } ${r.prezime }</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr><td>Zanr:</td>
				<td>
					<select name="zanrovi" multiple>
						<c:forEach items="${zanrovi}" var="z">
							<option value="${z.idZanr}">${z.naziv }</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr><td><input type="submit" value="Sacuvaj"></td></tr>
		</table>
	</form>
	${poruka }
</body>
</html>