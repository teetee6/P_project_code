<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<%@ page import="java.io.*" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.util.*" %>

<%

String param = request.getParameter("type");

if(param.equals("rabbit")){
	BufferedReader br = null;
	 try{
		 FileReader fr = new FileReader("c:/estory/rabbit.txt");
		 br = new BufferedReader(fr);
		 String lines;
		 StringBuffer all = new StringBuffer();
		 while((lines = br.readLine())!= null){
			 all.append(lines);
					 
	 }
		 String allLine = all.toString();
		 JSONObject result = new JSONObject();
		 result.put("char1","거북이");
		 result.put("char2","토끼");
		 result.put("data",allLine);
		 out.print(result.toString());
	 }catch(Exception e){
		 
		 
	 }
}
else if(param.equals("cat")){
	BufferedReader br = null;
	 try{
		 FileReader fr = new FileReader("c:/estory/boy_and_cat.txt");
		 br = new BufferedReader(fr);
		 String lines;
		 StringBuffer all = new StringBuffer();
		 while((lines = br.readLine())!= null){
			 all.append(lines);
					 
	 }
		 String allLine = all.toString();
		 JSONObject result = new JSONObject();
		 result.put("char1","boy");
		 result.put("char2","cat");
		 result.put("data",allLine);
		 out.print(result.toString());
	 }catch(Exception e){
		 
		 
	 }
	
	
}

%>

</head>
<body>


</body>
</html>