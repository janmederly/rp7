package robot;

import bluetooth.Bluetooth;

public class Zobrazovac implements Runnable {

	private Bluetooth bluetooth;
	private HlavneOkno hlavneOkno;

	public Zobrazovac(Bluetooth bluetooth, HlavneOkno hlavneOkno) {
		this.bluetooth = bluetooth;
		this.hlavneOkno = hlavneOkno;
	}

	@Override
	public void run() {
		for (;;) {
			String riadok = bluetooth.prijmi();
			if (riadok == null) {
				return;
			}
			hlavneOkno.prisielRiadok(riadok);
		}
	}
	
}
