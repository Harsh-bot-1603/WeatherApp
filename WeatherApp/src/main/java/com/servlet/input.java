package com.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@WebServlet("/input")
public class input extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String city = request.getParameter("city");
		String apiKey = "2cbe56433e73e3d5df01cc99da0476b2";
		String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q="
		        + URLEncoder.encode(city, "UTF-8")
		        + "&appid=" + apiKey
		        + "&units=metric";
		
//		API integration
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		Scanner scanner = new Scanner(reader);
		StringBuilder responseContent = new StringBuilder();
		
		while(scanner.hasNext()) {
			responseContent = responseContent.append(scanner.nextLine());
		}
		scanner.close();
		
//		Type Casting
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(),JsonObject.class);
	
		JsonArray list = jsonObject.getAsJsonArray("list");
		// Get first object inside list
		JsonObject firstItem = list.get(0).getAsJsonObject();

		JsonObject mainObject = firstItem.getAsJsonObject("main");
		JsonObject windObject = firstItem.getAsJsonObject("wind");
		JsonArray weatherArray = firstItem.getAsJsonArray("weather");

		long dateTimeStamp = firstItem.get("dt").getAsLong();
		String date = new Date(dateTimeStamp * 1000).toString(); // multiply by 1000 (IMPORTANT)

		double tempratureCelsius = mainObject.get("temp").getAsDouble();
		

		int humidity = mainObject.get("humidity").getAsInt();

		double windSpeed = windObject.get("speed").getAsDouble();

		String weatherCondition = weatherArray
		        .get(0)
		        .getAsJsonObject()
		        .get("main")
		        .getAsString();
		
		request.setAttribute("Date",date);
		request.setAttribute("City",city);
		request.setAttribute("Temprature",tempratureCelsius);
		request.setAttribute("Humidity",humidity);
		request.setAttribute("WindSpeed",windSpeed);
		request.setAttribute("WeatherCondition",weatherCondition);
		
		connection.disconnect();
		
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
