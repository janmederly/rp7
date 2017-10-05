package jinput;

import bluetooth.Bluetooth;

public class Prijimac implements Runnable {

	private Bluetooth bluetooth;

	public Prijimac(Bluetooth bluetooth) {
		this.bluetooth = bluetooth;
	}

	@Override
	public void run() {
		for (;;) {
			String riadok = bluetooth.prijmi();
			if (riadok == null) {
				return;
			}
			int iAx = riadok.indexOf("AX:");
			if (iAx >= 0) {
				int iMedzera1 = riadok.indexOf(' ', iAx+1);
				if (iMedzera1 >= 0) {
					int iMedzera2 = riadok.indexOf(' ', iMedzera1+1);
					if (iMedzera2 >= 0) {
						String axS = riadok.substring(iAx+3, iMedzera1);
						String ayS = riadok.substring(iMedzera1+4, iMedzera2);
						String azS = riadok.substring(iMedzera2+4);
						try {
							float ax = Float.parseFloat(axS);
							float ay = Float.parseFloat(ayS);
							float az = Float.parseFloat(azS);
							Volant.akcelerometer(ax, ay, az);
						} catch (NumberFormatException e) {
							System.out.println("Chyba: " + e);
						}
					}
				}
			}
		}
	}
}
