/**
 *  class ModeMonitor:
 *
 * Monitor class to keep track of the current execution mode (OFF, BEAM, or BALL)
 */
public class ModeMonitor {

    private double relayHysteresis;
    private double relayAmp;
    private int processType;
    private Mode mode = Mode.OFF;

    /**
     * Synchronized method setMode:
     *
     * @param:
     *     newMode (Mode): Sets the new mode to be newMode
     */
    public synchronized void setMode(double relayAmp, double relayHysteresis, int processType) {
        
        this.relayHysteresis = relayHysteresis;
        this.relayAmp=relayAmp;
        if(processType ==0){
	    mode = Mode.OFF;
	}
	else if(processType == 1){
	    mode = Mode.BEAM;
	}
	else if(processType == 2){
	    mode = Mode.UPPERTANK;
	}
	else{
	    mode = Mode.LOWERTANK;
	}

    }

    /**
     * Synchronized method getMode:
     *
     * @return:
     *     Mode: The current mode in the ModeMonitor
     */
    public synchronized Mode getMode() {
        return mode;
    }
   
    public synchronized double  getHysteresis() {
        return relayHysteresis;
    }
   

    public synchronized double getrelayAmp() {
        return relayAmp;
    }
  public enum Mode{
      OFF, BEAM, UPPERTANK, LOWERTANK;
  }
}
