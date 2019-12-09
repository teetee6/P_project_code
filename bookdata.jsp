<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="EUC-KR" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Book File IO</title>
<%@ page import="java.io.*" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.util.*" %>

<%
//안드로이드에서 값 받아서 책 판별하는 로직 추가하기
 BufferedReader br = null;
 try{
	 FileReader fr = new FileReader("c:/estory/rabbit.txt");
	 br = new BufferedReader(fr);
	 String lines;
	 StringBuffer all = new StringBuffer();
	 while((lines = br.readLine())!= null){
		 all.append(lines);
		 all.append('\r');	 
 }//while 끝

String allLine = all.toString();
JSONObject result = new JSONObject();
String characters ="거북이 | 토끼";
/*result.put("character1","거북이");
result.put("character2","토끼");*/
result.put("character",characters);
result.put("data",allLine);
System.out.println(result);
//System.out.println(result.get("character"));

 
 }//try 끝
 catch(FileNotFoundException ex){
	  
 }
 

%>

</head>
<body>


</body>
</html>