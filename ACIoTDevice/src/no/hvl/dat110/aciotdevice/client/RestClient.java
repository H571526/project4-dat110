package no.hvl.dat110.aciotdevice.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

public class RestClient {

	private static int port = 8080;
	private static String host = "localhost";
	Gson gson = new Gson();

	private static String logpath = "/accessdevice/log";
	private static String codepath = "/accessdevice/code";

	public RestClient() {
	}

	public void doPostAccessEntry(String message) {

		try (Socket s = new Socket(host, port)) {
			// construct the GET request
			String msgjson = "{\n   \"message\": \"" + message + "\"\n}";
			String httppostrequest = "POST " + logpath + " HTTP/1.1\r\n" + "Accept: application/json\r\n"
					+ "Host: localhost\r\n" + "Connection: close\r\n" + "Content-type: application/json\r\n"
					+ "Content-length: " + msgjson.length() + "\r\n" + msgjson + "\r\n";

			// sent the HTTP request
			OutputStream output = s.getOutputStream();
			PrintWriter pw = new PrintWriter(output, false);

			pw.print(httppostrequest);
			pw.flush();

			// read the HTTP response
			InputStream in = s.getInputStream();
			Scanner scan = new Scanner(in);
			StringBuilder jsonresponse = new StringBuilder();

			boolean header = true;

			while (scan.hasNext()) {
				String nextline = scan.nextLine();
				if (header) {
					System.out.println(nextline);
				} else {
					jsonresponse.append(nextline);
				}
				// simplified approach to identifying start of body: the empty line
				if (nextline.isEmpty()) {
					header = false;
				}
			}
			String jsonres = jsonresponse.toString();
			System.out.println("Post-connection reply: " + jsonres);
			scan.close();

		} catch (IOException ex) {
			System.err.println(ex);
		}

	}

	public AccessCode doGetAccessCode() {

		AccessCode code = new AccessCode();
		try (Socket s = new Socket(host, port)) {
			// construct the GET request
			String httpgetrequest = "GET " + codepath + " HTTP/1.1\r\n" + "Accept: application/json\r\n"
					+ "Host: localhost\r\n" + "Connection: close\r\n" + "\r\n";

			// sent the HTTP request
			OutputStream output = s.getOutputStream();
			PrintWriter pw = new PrintWriter(output, false);

			pw.print(httpgetrequest);
			pw.flush();

			// read the HTTP response
			InputStream in = s.getInputStream();
			Scanner scan = new Scanner(in);
			StringBuilder jsonresponse = new StringBuilder();

			boolean header = true;

			while (scan.hasNext()) {
				String nextline = scan.nextLine();
				if (header) {
					System.out.println(nextline);
				} else {
					jsonresponse.append(nextline);
				}
				// simplified approach to identifying start of body: the empty line
				if (nextline.isEmpty()) {
					header = false;
				}
			}
			AccessCode newCode = gson.fromJson(jsonresponse.toString(), AccessCode.class);
			int[] c = newCode.getAccesscode();
			code.setAccesscode(c);

			scan.close();
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return code;
	}
}
