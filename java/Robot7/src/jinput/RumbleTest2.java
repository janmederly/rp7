/*
 * RumbleTest.java
 *
 * Created on 01 December 2003, 23:02
 */
package jinput;

import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Rumbler;
import net.java.games.input.Version;

/**
 *
 * @author  Jeremy
 */
public class RumbleTest2 {
    
    /** Creates a new instance of RumbleTest */
    public RumbleTest2() throws Exception {
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] ca = ce.getControllers();
		Controller controller = null;
		for (int i = 0; i < ca.length; i++) {
			System.out.println("controller name: " + ca[i].getName());
			if (ca[i].getName().contains("Force")) {
				controller = ca[i];
				break;
			}
		}
		if (controller == null) {
			System.err.println("Controller not found, exiting.");
			System.exit(0);
		}

        Rumbler[] rumblers = controller.getRumblers();
        System.out.println("Found " + rumblers.length + " rumblers");
//        for (int i = 0; i < rumblers.length; i++) {
//        	Rumbler r = rumblers[i];
//        	rumble(r, i, 0.25f, 1000);
//        	rumble(r, i, 0.5f, 1000);
//        	rumble(r, i, 0.75f, 1000);
//        	rumble(r, i, 1.0f, 1000);
//        	rumble(r, i, 2.0f, 1000);
//        	rumble(r, i, 4.0f, 1000);
//        }
        while (1 == 1)  {
        	rumble(rumblers[3], 3, 1.0f, 4000, false);
        	rumble(rumblers[4], 4, 0.5f, 4000, true);
        }

        
//        rumble(rumblers[3], 3, 1.0f, 2000, true);
//        rumble(rumblers[2], 2, 1.0f, 2000, true);
//        rumble(rumblers[0], 0, 1.0f, 2000, true);
//        rumble(rumblers[3], 3, 1.0f, 2000, true);
//        rumble(rumblers[2], 2, 1.0f, 2000, true);
//        rumble(rumblers[0], 0, 1.0f, 2000, true);
//        rumble(rumblers[3], 3, 1.0f, 2000, true);
//        rumble(rumblers[2], 2, 1.0f, 2000, true);
//        rumble(rumblers[0], 0, 1.0f, 2000, true);
//        rumble(rumblers[0], 0, 1.0f, 100, true);
//        Thread.sleep(1000);
//        rumble(rumblers[0], 0, 1.0f, 100, true);
//        Thread.sleep(1000);
//        rumble(rumblers[0], 0, 1.0f, 100, true);
//        Thread.sleep(1000);
//        rumble(rumblers[0], 0, 1.0f, 100, true);
//        Thread.sleep(1000);
//        rumble(rumblers[0], 0, 1.0f, 100, true);
//        Thread.sleep(1000);
    }
    
    private void rumble(Rumbler r, int i, float intensity, int delay, boolean off) throws Exception {
    	System.out.println("Rumbler " + i + ", intensity " + intensity);
		r.rumble(intensity);
		Thread.sleep(delay);
		if (off) {
			r.rumble(0f);
		}
	}

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new RumbleTest2();
    }
    
}
