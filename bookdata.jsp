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
//�ȵ���̵忡�� �� �޾Ƽ� å �Ǻ��ϴ� ���� �߰��ϱ�
 BufferedReader br = null;
 try{
	 FileReader fr = new FileReader("c:/estory/rabbit.txt");
	 br = new BufferedReader(fr);
	 String lines;
	 StringBuffer all = new StringBuffer();
	 while((lines = br.readLine())!= null){
		 all.append(lines);
		 all.append('\r');	 
 }//while ��

String allLine = all.toString();
JSONObject result = new JSONObject();
String characters ="�ź��� | �䳢";
/*result.put("character1","�ź���");
result.put("character2","�䳢");*/
result.put("character",characters);
result.put("data",allLine);
System.out.println(result);
//System.out.println(result.get("character"));

 
 }//try ��
 catch(FileNotFoundException ex){
	  
 }
 

%>

</head>
<body>


</body>
</html>