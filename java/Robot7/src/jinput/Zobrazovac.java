package jinput;

import bluetooth.Bluetooth;

public class Zobrazovac implements Runnable {

	private Bluetooth bluetooth;

	public Zobrazovac(Bluetooth bluetooth) {
		this.bluetooth = bluetooth;
	}

	@Override
	public void run() {
		for (;;) {
			String riadok = bluetooth.prijmi();
			if (riadok == null) {
				return;
			}
			//System.out.println("BT -> " + riadok);
		}
	}
}
