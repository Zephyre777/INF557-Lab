/*Author: Zhuoyao LIN
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Xurl {
	static Socket socket = null;
	// private static Socket socket;
	private static Socket socket1;
	private static String path;
	private static int port;
	private static String protocol;
	private static String host;
	private static String prehost;
	private static BufferedReader buffer;
	private static PrintWriter printStream;
	private static String filepath;
	private static MyURL url;
	private static InetSocketAddress addr;

	public static void EstablishConnection(String host, int port) throws UnknownHostException, IOException {

		// Use socket to bind the host and port.
		// The port needs to be an integer.
		// And establish the TCP connection here.
		/*
		 * if(host == "localhost") { host = "127.0.0.1"; }
		 */
		// socket1 = new Socket();
		// SocketChannel sc = SocketChannel.open();
		// socket = new Socket(host,port);

		/*
		 * try { Socket socket = new Socket(); socket.setSoTimeout(2000);
		 * InetSocketAddress addr = new InetSocketAddress(host, port);
		 * 
		 * socket.bind(addr); socket.connect(new InetSocketAddress(host,port));
		 * 
		 * //socket.connect(addr); } catch (IOException e) { throw new
		 * RuntimeException(e);} }
		 */
		socket = new Socket(host, port);
		socket.setSoTimeout(2000);
		/*
		 * try { socket = new Socket(host, port); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace();}
		 */
	}

	public static void sendRequest(String path, String host, int port) throws IOException {
		// use socket.getOutoutStream to return an output stream
		// where we write bytes to the socket channel
		// use printwriter to provide method for directly using the printf to wirte
		// several strings
		printStream = new PrintWriter(socket.getOutputStream(), true);// Auto flush
		// "%" to put path and host to the GET command respectively, /r/n means enter
		// I actuall write :Get "path:port" HTTP/1.1 <CR><CF>
		// Host: "hostname" : port <CR><CF>

		//printStream.printf("GET %s HTTP/1.1\r\nHost: %s:%s \r\n\r\n", path, host, port);
		//System.out.printf("GET %s HTTP/1.1\r\nHost: %s:%s \r\n\r\n", path, host, port);
		// printStream.flush();(I quit this cause I've set auto-flashing
	}

	public static void getReply() throws IOException {
		// use bufferReader to read the character-input stream
		// use InputStreamReader to read bytes-input stream
		
		buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public static void parseHeader(String path, String host, int port,String test,String test1) throws IOException {
		printStream.printf("GE%s %s %s/1.1\r\nHost: %s:%s \r\nGE %s HTTP/1.1\r\n\r\n",test, path, test1, host, port, path);
		System.out.printf("GE%s %s %s/1.1\r\nHost: %s:%s \r\n\r\n", test, path,test1, host, port);
		String currentLine;
		StringBuffer reply = new StringBuffer();
		String result;
		String[] temp;
		String newurl;
		do {
			currentLine = buffer.readLine();// read the reply message until receive the http reply
		} while (!currentLine.contains("HTTP/1"));
		int status_code = 0;// Initialize the status_code
		Pattern pattern = Pattern.compile("HTTP/1.1 (\\d+)+"); // (\\d+)+ stands for the status code
		Matcher matcher = pattern.matcher(currentLine);
		if (matcher.find()) { // Attempts to find the next subsequence of the input sequence that matches the
								// pattern.
			status_code = Integer.parseInt(matcher.group(1));
		}

		switch (status_code) {
		case 200:
			System.out.println("Successfully connected");
			break;
		/*case 301:
			System.err.println("URL moved Permenantly");
			do {
				currentLine = buffer.readLine();
			} while (!currentLine.contains("Location: "));
			temp = currentLine.split("Location: ", 2);
			newurl = temp[1];
			System.out.println(newurl);
			run(newurl);*/
		/*case 302:
			System.err.println("URL moved Temporarily");
			do {
				currentLine = buffer.readLine();
			} while (!currentLine.contains("Location: "));
			temp = currentLine.split("Location: ", 2);
			newurl = temp[1];
			System.out.println(newurl);
			run(newurl);
			break;*/
		case 400:
			System.out.println("Bad Request");
			// System.exit(0);
			break;
		case 404:
			System.out.println("Not Found");
			// System.exit(0);
			break;
		}
		/*
		 * do{ reply.append(currentLine); reply.append("\r\n"); currentLine =
		 * buffer.readLine(); }while(currentLine != null); result = reply.toString();
		 * System.out.println(result);
		 */

	}

	public static void savefile() throws IOException {
		PrintStream file = new PrintStream(new File(filepath));
		// PrintWriter file = new PrintWriter(new File(filepath));
		String line;
		String[] temp;
		int contentlength = 0;

		/*
		 * do { line = buffer.readLine(); }while(!line.contains("Content-Length"));
		 * //buffer.readLine(); //buffer.readLine(); temp =
		 * line.split("Content-Length: "); int contentlength =
		 * Integer.parseInt(temp[1]);
		 */

		// }while(line.isEmpty());*/
		while ((line = buffer.readLine()) != null) { // the last line of header is null
			if (line.contains("Content-Length")) {
				temp = line.split("Content-Length: ");
				contentlength = Integer.parseInt(temp[1]);
			}
			System.out.println(line);
			if (line.length() == 0) {
				System.out.println("finish receving!");
				break;
			}
		}
		if (contentlength != 0) {
			for (int i = 0; i < contentlength; i++) {
				file.print((char) buffer.read());
			}
			// file.print("\r");
		} else {
			do {
				file.println(buffer.readLine());
			} while (buffer.ready());
		}
		file.close();
	}

	public static void disconnect() throws IOException {
		socket.close();
		printStream.close();
		buffer.close();
		System.exit(0);
	}

	public static void run(String[] url_o) {
		url = new MyURL(url_o[0]);
		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		path = url.getPath();
		filepath = !path.substring(path.lastIndexOf("/") + 1).isEmpty() ? path.substring(path.lastIndexOf("/") + 1)
				: "index"; // Set default fileName to index

		if (port == -1) {
			port = 80;
		}
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(2000);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] test = {"T","T","T"};
		String[] test1 = {"HTTP","HTTP","HTTP"};
		for (int i = 0; i < 3; i++) {
			url = new MyURL(url_o[i]);
			protocol = url.getProtocol();
			host = url.getHost();
			port = url.getPort();
			path = url.getPath();
			filepath = !path.substring(path.lastIndexOf("/") + 1).isEmpty() ? path.substring(path.lastIndexOf("/") + 1)
					: "index"+i; // Set default fileName to index

			if (port == -1) {
				port = 80;
			}
			try {
				sendRequest(path, host, port);
				getReply();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				// disconnect();
				// EstablishConnection(host, port);
				//sendRequest(path, host, port);
				//getReply();
				parseHeader(path,host,port,test[i],test1[i]);
				savefile();
				// disconnect();
				//return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static int getPort(String url) {

		String[] Port = url.split("://", 2);
		if (Port[1].contains(":")) { // check if the port is given in the url
			String[] Port_1 = url.split("://|:", 3);
			Port = Port_1[2].split("/", 3);
			// Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$:"); //compile
			char test[] = Port[0].toCharArray();
			// System.out.println(Port[0]);

			// if(!pattern.matcher(Port[2]).matches())
			// { //check if the given port number is an interger
			// throw new IllegalArgumentException("The port contains illegal statements,
			// please correct.");
			// }
			for (int i = 0; i < test.length; i++) {
				if (!Character.isDigit(test[i])) { // check if the given port number is an interger
					throw new IllegalArgumentException("The port contains illegal statements, please correct.");
				}
			}
			// System.out.println(Port[2]); //The port is correctly given, print the given
			// port
			port = Integer.parseInt(Port[0]);
		} else {
			// System.out.println("-1"); //The port is not given, print the default value -1
			port = 80;
		}
		return port;
	}

	static String getPath(String url_o) {
		String[] Path = url_o.split("://", 2);
		if (!Path[1].contains("/")) { // Throw the error if the path is not given by "/"
			throw new IllegalArgumentException("The URL doesn't contain path, please correct.");
		} else {
			if (Path[1].contains(":")) {
				Path = url_o.split("://|:|/", 4);
			} else {
				Path = url_o.split("://|/", 3);
			} // Print the path
			int order = Path.length;
			path = "/" + Path[order - 1];
			// System.out.print("/");
			// System.out.println(Path[order - 1]);
		}
		return path;
	}

	public static void main(String[] args) {
		// TODO The whole process is:
		// Parse the url passing from the args
		// Establish the TCP connection
		// Send the request to the server, with information about the
		// protocol,path,port,hostname
		// Receive the reply from the server
		// Check the header information returned by server
		// Write the HTML to file


		run(args);
	}

}

class MyURL {
	private String protocol;
	private String path;
	private int port;
	private String hostname;

	MyURL(String url) {
		// extract protocol
		String[] protocol_o = url.split("://", 2);
		// if the protocol part is empty or contain ":" or "/", throw the error.
		if (protocol_o[0].isEmpty() || protocol_o[0].contains(":") || protocol_o[0].contains("/")) {
			throw new IllegalArgumentException("The protocol contains illegal statements, please correct.");
		}
		this.protocol = protocol_o[0];

		// extract Host
		String[] Hostname = url.split("://|:|/", 4);
		if (Hostname.length < 2) {
			throw new IllegalArgumentException("The hostname is missing.");
		}
		// if the Hostname is empty, throw the error.
		if (Hostname[1].isEmpty()) {
			throw new IllegalArgumentException("The hostname is illegal, please correct.");
		}
		// System.out.println(Hostname[1]);
		this.hostname = Hostname[1];

		// extract port
		String[] Port = url.split("://", 2);
		if (Port[1].contains(":")) { // check if the port is given in the url
			String[] Port_1 = url.split("://|:", 3);
			Port = Port_1[2].split("/", 3);
			// Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$:"); //compile
			char test[] = Port[0].toCharArray();
			// System.out.println(Port[0]);

			// if(!pattern.matcher(Port[2]).matches())
			// { //check if the given port number is an interger
			// throw new IllegalArgumentException("The port contains illegal statements,
			// please correct.");
			// }
			for (int i = 0; i < test.length; i++) {
				if (!Character.isDigit(test[i])) { // check if the given port number is an interger
					throw new IllegalArgumentException("The port contains illegal statements, please correct.");
				}
			}
			// System.out.println(Port[2]); //The port is correctly given, print the given
			// port
			this.port = Integer.parseInt(Port[0]);
		} else {
			// System.out.println("-1"); //The port is not given, print the default value -1
			this.port = 80;
		}

		// extract path
		String[] Path = url.split("://", 2);
		if (!Path[1].contains("/")) { // Throw the error if the path is not given by "/"
			throw new IllegalArgumentException("The URL doesn't contain path, please correct.");
		} else {
			if (Path[1].contains(":")) {
				Path = url.split("://|:|/", 4);
			} else {
				Path = url.split("://|/", 3);
			} // Print the path
			int order = Path.length;
			this.path = "/" + Path[order - 1];
			// System.out.print("/");
			// System.out.println(Path[order - 1]);
		}

	}

	public static void main(String[] args) {
		// <protocol>://<hostname>[:<port>]/<path>
		// System.out.println("Please enter the String: ");
		// Scanner sc = new Scanner(System.in);
		// String url_o = sc.next(); //Get the input url
		// String url_o = "";
		// for(int j=0;j<args.length;j++)
		// {
		// url_o=url_o+args[j];
		// }
		MyURL test = new MyURL("hther://other:12345/othere");
		// String url_o = args[0];
		String protocol = test.getProtocol();
		String hostname = test.getHost();
		int port = test.getPort();
		String path = test.getPath();

		// System.out.print("Protocol:");
		// System.out.println(protocol);
		// System.out.print("Hostname:");
		// System.out.println(hostname);
		// System.out.print("Port:");
		// System.out.println(port);
		// System.out.print("Path:");
		// System.out.println(path);
	}

	public String getProtocol() {
		return this.protocol;
	}

	public String getHost() {
		return this.hostname;
	}

	public int getPort() {
		return this.port;
	}

	public String getPath() {
		return this.path;
	}
}
