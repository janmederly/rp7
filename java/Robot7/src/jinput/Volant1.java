package jinput;

import bluetooth.Bluetooth;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Volant1 {
	
	public static void main(String[] args) {
	    Bluetooth bluetooth = Bluetooth.priprav();
	    
	    Thread zobrazovac = new Thread(new Zobrazovac(bluetooth));
	    zobrazovac.setDaemon(true);
	    zobrazovac.start();
	    
		float x = 0;
		float y = 0;
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] ca = ce.getControllers();
		Controller hama = null;
		for (int i = 0; i < ca.length; i++) {
			if (ca[i].getName().contains("HAMA")) {
				hama = ca[i];
				break;
			}
		}
		if (hama == null) {
			System.err.println("HAMA controller not found, exiting.");
			System.exit(0);
		}
		System.out.println("Subcontrollers: " + hama.getControllers().length);
		Component[] components = hama.getComponents();
		Component compVolant = null, compPlyn = null;
		Component compSpiatocka = null, compDopredu = null;
		
		for (Component c : components) {
			System.out.println("--> " + c.getName() + ": " + c.getIdentifier());
			Identifier id = c.getIdentifier();
			if (Axis.X.equals(id)) {
				compVolant = c;
			} else if (Axis.Y.equals(id)) {
				compPlyn = c;
			} else if (c.getIdentifier().equals(Identifier.Button._1)) {
			    compDopredu = c;	
			} else if (c.getIdentifier().equals(Identifier.Button._0)) {
				compSpiatocka = c;
			}
		}
		System.out.println("Volant: " + compVolant);
		System.out.println("Plyn: " + compPlyn);
		System.out.println("Dopredu: " + compDopredu);
		System.out.println("Spiatocka: " + compSpiatocka);
		
		for (;;) {
			hama.poll();
			System.out.println("Volant = " + compVolant.getPollData() 
				+ ", Plyn = " + compPlyn.getPollData()
				+ ", Dopredu = " + compDopredu.getPollData()
				+ ", Spiatocka = " + compSpiatocka.getPollData());
			bluetooth.posli("X" + cisloNaRetazec((int) (compVolant.getPollData()*1000) ) );
			bluetooth.posli("Y" + cisloNaRetazec((int) (compPlyn.getPollData()*1000) ) );
			if (compDopredu.getPollData() != 0) {
				bluetooth.posli("R");
			}
			if (compSpiatocka.getPollData() != 0) {
				bluetooth.posli("S");
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String cisloNaRetazec(int n) {
		char[] a = new char[5];
		if (n >= 0) {
			a[0] = '+';
		} else {
			a[0] = '-';
			n = -n;
		}
		a[4] = (char) (n % 10 + 48);
		a[3] = (char) ((n / 10) % 10 + 48);
		a[2] = (char) ((n / 100) % 10 + 48);
		a[1] = (char) ((n / 1000) % 10 + 48);
		return new String(a);
	}
}
