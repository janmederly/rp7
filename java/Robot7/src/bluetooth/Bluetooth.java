package bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class Bluetooth {

	private PrintWriter writer;
	private BufferedReader reader;
	private StreamConnection streamConnection;
	
	public boolean vypisy = true;
	
	private Bluetooth(String url) throws IOException {
		System.out.println("Pripajam sa na " + url);
		streamConnection = (StreamConnection) Connector.open(url);
		OutputStream outStream = streamConnection.openOutputStream();
		InputStream inStream = streamConnection.openInputStream();
		writer = new PrintWriter(new OutputStreamWriter(outStream));
		reader = new BufferedReader(new InputStreamReader(inStream));
		System.out.println("Uspesne pripojeny.");
	}
	
	public static Bluetooth priprav() {
		//String url = "btspp://201411272918:1;authenticate=false;encrypt=false;master=false";	// HC06
		String url = "btspp://98D331FD1F5B:1;authenticate=false;encrypt=false;master=false";	// Robot
		try {
			return new Bluetooth(url);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void posli(String s) {
		if (vypisy) {
			System.out.println("Posielam: '" + s + "'");
		}
		writer.print(s);
		writer.flush();
	}
	
	public String prijmi() {
		try {
			String s = reader.readLine();
			if (vypisy) {
				System.out.println("Prijal som: '" + s + "'");
			}
			return s;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void zatvor() {
		try {
			streamConnection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Bluetooth bluetooth = Bluetooth.priprav();
		bluetooth.posli("A");
		for (;;) {
			System.out.println(bluetooth.prijmi());
		}
	}

}
