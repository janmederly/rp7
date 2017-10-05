package jinput;

import bluetooth.Bluetooth;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;

public class Volant {
	
	private static final String CONTROLLER_NAME = "Force";

    private static Rumbler[] rumblers;
    
    static boolean trasenie = false;

	public static void main(String[] args) {

        Bluetooth bluetooth = Bluetooth.priprav();
	    
	    Thread zobrazovac = new Thread(new Prijimac(bluetooth));
	    zobrazovac.setDaemon(true);
	    zobrazovac.start();

		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] ca = ce.getControllers();
		Controller controller = null;
		for (int i = 0; i < ca.length; i++) {
			System.out.println("Nazov controllera: " + ca[i].getName());
			if (ca[i].getName().contains(CONTROLLER_NAME)) {
				controller = ca[i];
				break;
			}
		}
		if (controller == null) {
			System.err.println("Controller " + CONTROLLER_NAME + " nebol najdeny.");
			System.exit(0);
		}
		Component[] components = controller.getComponents();
		Component compVolant = null, compPlyn = null;
		Component compSpiatocka = null, compDopredu = null;
		Component compSenzorOff = null, compSenzorOn = null;
		Component compBrzda = null, compPedale = null;
		Component compTrasenieOn = null, compTrasenieOff = null;
		
		for (Component c : components) {
			System.out.println("--> " + c.getName() + ": " + c.getIdentifier());
			Identifier id = c.getIdentifier();
			if (Axis.X.equals(id)) {
				compVolant = c;
			} else if (Axis.Y.equals(id)) {
				compPlyn = c;
			} else if (c.getIdentifier().equals(Identifier.Button._13)) {
			    compDopredu = c;
			} else if (c.getIdentifier().equals(Identifier.Button._12)) {
				compSpiatocka = c;
			} else if (c.getIdentifier().equals(Identifier.Button._7)) {
				compSenzorOff = c;
			} else if (c.getIdentifier().equals(Identifier.Button._8)) {
				compSenzorOn = c;
			} else if (c.getIdentifier().equals(Identifier.Axis.RZ)) {
				compBrzda = c;
			} else if (c.getIdentifier().equals(Identifier.Axis.SLIDER)) {
				compPedale = c;
			} else if (c.getIdentifier().equals(Identifier.Button._3)) {		// O
				compTrasenieOff = c;
			} else if (c.getIdentifier().equals(Identifier.Button._1)) {		// X
				compTrasenieOn = c;
			}
		}

		System.out.println("Volant: " + compVolant);
		System.out.println("Plyn: " + compPlyn);
		System.out.println("Brake: " + compBrzda);
		System.out.println("Pedals: " + compPedale);
		System.out.println("Dopredu: " + compDopredu);
		System.out.println("Spiatocka: " + compSpiatocka);
		System.out.println("SenzorOff: " + compSenzorOff);
		System.out.println("SenzorOn: " + compSenzorOn);
		System.out.println("TrasenieOff: " + compTrasenieOff);
		System.out.println("TrasenieOn: " + compTrasenieOn);
		
		rumblers = controller.getRumblers();
        System.out.println("Nasiel " + rumblers.length + " rumblerov");

	    //bluetooth.vypisy = false;
        
		for (;;) {
			controller.poll();
			System.out.println("Volant = " + compVolant.getPollData() 
				+ ", Plyn = " + compPlyn.getPollData()
				+ ", Brake = " + compBrzda.getPollData()
				+ ", Pedals = " + compPedale.getPollData()
				+ ", Dopredu = " + compDopredu.getPollData()
				+ ", Senzor = " + compSenzorOff.getPollData()
				+ ", SenzorOn = " + compSenzorOn.getPollData()
				+ ", Spiatocka = " + compSpiatocka.getPollData()
				+ ", TrasenieOn = " + compTrasenieOn.getPollData()
				+ ", TrasenieOff = " + compTrasenieOff.getPollData());
			bluetooth.posli("Y" + cisloNaRetazec((int) (-compPedale.getPollData()*1000) ) );
			bluetooth.posli("X" + cisloNaRetazec((int) (compVolant.getPollData()*1000) ) );
			if (compSenzorOff.getPollData() != 0) {
				bluetooth.posli("G");
			}
			if (compSenzorOn.getPollData() != 0) {
				bluetooth.posli("F");
			}
			if (compDopredu.getPollData() != 0) {
				bluetooth.posli("R");
			}
			if (compSpiatocka.getPollData() != 0) {
				bluetooth.posli("S");
			}
			if (compTrasenieOff.getPollData() != 0) {
				trasenie = false;
			}
			if (compTrasenieOn.getPollData() != 0) {
				trasenie = true;
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
//	public static void naraz(String riadok){
//		if (riadok.indexOf("a")){
//			
//			
	//	}

	private static final float max = 10;
	private static int posledny = -1;
	
	public static void akcelerometer(float ax, float ay, float az) {
		float sucet = Math.abs((float) Math.sqrt(ax*ax + ay*ay + az*az) - 9.81f);
		float rumble = sucet / max;
		if (rumblers == null || rumblers.length < 4 || !trasenie) {
			return;
		}
		
		int ktory;
		if (rumble > 0.7) {
			ktory = 0;
		} else if (rumble > 0.4) {
			ktory = 2;
		} else if (rumble > 0.1) {
			ktory = 3;
		} else {
			ktory = -1;
		}
		System.out.println("AX=" + ax + ", AY=" + ay + ", AZ=" + az + " => sucet=" + sucet + ", rumble=" + rumble + ", p/k=" + posledny + "/" + ktory);
		if (ktory == posledny) {
			return;
		}
		if (posledny != -1) {
			rumblers[posledny].rumble(0.0f);
		}
		if (ktory != -1) {
			rumblers[ktory].rumble(1.0f);
		}
		if (rumble > 1.0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		posledny = ktory;
	}
}
