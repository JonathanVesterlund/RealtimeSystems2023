import se.lth.control.realtime.AnalogIn;
import se.lth.control.realtime.AnalogOut;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeListener; 


public class RegulStinaLinnea extends Thread {
    private PID pi = new PID("name");

    private ReferenceGenerator refGen;
    private GUI gui;
        
    private AnalogIn analogInAngle;
    private AnalogIn analogInPosition;
    private AnalogOut analogOut;

    private int priority;
    private boolean shouldRun = true;
    private long startTime;

    private ModeMonitor modeMon; //ERSÄTT MED NY MODE-MOTSVARIGHETEN FRÅN GUI 
    

   //tas från swing 
    //relay parametrar, beror på mode, tas från swing
    private double hyst;
    private double relayAmp;

    //amplitude and period time when stabilizes system
    private double amp;
    private double timePeriod;

    //private double yRef = 0;
    //private double y = 0;
    //private double u = 0;

    public RegulStinaLinnea(int pri, ModeMonitor modeMon) {
        priority = pri;
        setPriority(priority);
        this.modeMon = modeMon;

        try {
            // In order to initialize or interact with the 
            // analog interface, a try catch is needed.
            analogInAngle = new AnalogIn(0); 
            analogInPosition = new AnalogIn(1);
            analogOut = new AnalogOut(0);


        } catch (Exception e) {
            System.out.print("Error: IOChannelException: ");
            System.out.println(e.getMessage());
        }
    }

    /** Sets OpCom (called from main) */
    // public void setOpCom(OpCom opCom) {
    //this.opCom=opCom;
    // }

    @Override
    public void run(){

        long duration;
        long t = System.currentTimeMillis();
        startTime = t;
        boolean on=false; 
       
        List<Double> Yval=new ArrayList<Double>();  //list with y-values at sign change 
        List<Double> PerTime=new ArrayList<Double>();  //list with time-period at sign change 

        double currentTime=0;
        double lastTime=0; 
        double y; double yref; double u; double yold=0; //to be caluclated 
        double maxDiffAmp=1; //amplitude limit
        double maxDiffTime=0.1; //time limit 
        boolean stable=false;
        boolean sign=true; 

        while (shouldRun) {
            hyst=modeMon.getHysteresis();
            relayAmp=modeMon.getrelayAmp(); 

            switch (modeMon.getMode()) { //Byt ut mot nya motsvarigheten till mode
               
                case 1: {  
                    
                    synchronized (pi) { // To avoid parameter changes in between compute control signal
                    y=yold;
                    y=readInput(analogInAngle); 
                    yref=refgen.getRef();
                    u = pi.calculateOutput(y, yref);
                    }
//-----------------------------------------------Comparision---------------------------------------------------
                   
                        if(sign && y-yold<=-hyst || !sign && y-yold>=hyst && stable==false){ //sign of slope changes (distinct change- hyst or other value??) 
                        sign=-sign; 
                        Yval.append(Math.abs((y+yold)/2)); //approximated peak value 
                        lastTime=currentTime; 
                        currentTime=System.currentTimeMillis();
                        PerTime.append(currentTime-lastTime); //time between sign-change 

                        if(Yval.size()>=6){
                            Yval.remove(0);  //removes oldest y-value if more than 5 values in list  
                            PerTime.remove(0);
                            
                            //STABILITY CHECK- All peak values are within the limit (and measured more than 5 cycles)
                            if(Collections.max(Yval)-Collections.min(Yval)<=maxDiffAmp && Collections.max(PerTime)-Collections.min(PerTime)<=maxDiffTime ){ 
                                stable=true; 
                                
                        }
                     } 

                        if (stable==true){ //PI-controller
                            for(int i = 0; i < Yval.size(); i++){
                                    amp=amp+Yval.get(i);
                                    timePeriod=timePeriod+PerTime.get(i);
                            }
                            //average time period and amplitude 
                            amp=amp/(Yval.size());
                            timePeriod=timePeriod*2/(PerTime.size());

                            PIParameters p=AstromHagglund(amp, relayAmp, timePeriod); //change parameters 
                            pi.setParameters(p); //changes PI- parameters according to Astrom-Hagglund- method
                            u = pi.calculateOutput(readInput(analogInAngle), yref);
                            u=limit(u);
                            pi.updateState(u);

                        }
                        else{ //UNSTABLE -->RELAY

                          /*   if(Math.abs(yref-y)<=hyst) {  
                                newstate=false; 
                            }
                            else if (y>yref) {
                                if ()
        
                            }
                            */

                        }
                   
                }


                case 2: { //upper tank // motsvarar ball and beam uppgift 3 
                  
                }

                case 3: { //lower tank//  motsvarar inre reguatorn endast 
                        
                }

                default: {
                    System.out.println("Error: Illegal mode.");
                    break;
                }
               
                sendDataToGUI(yref, y, u);
                writeOutput(u);


            

            //sleep
            t = t + inner.getHMillis();
            duration = t - System.currentTimeMillis();
            if(duration > 0) {
                try {
                    sleep(duration);
                } catch (InterruptedException x) {}
            } else {
                System.out.println("Lagging behind...");
            }
        }

        writeOutput(0); //Set control signal to zero before exiting run loop */
        }

    }




// Writes the control signal u to the output channel: analogOut
// @throws: IOChannelException
    private void writeOutput(double u) {
        try {
        analogOut.set(u);
        } catch (IOChannelException e) {
            e.printStackTrace();
        }
    }

    public PIParameters AstromHagglund(int amplitude, int relayAmp, int timePeriod ){
        PIParameters p=new PIParameters();
        
        //Åström-Hägglund
        double K180=Math.PI*amplitude/(4*relayAmp); 
        double Ku=1/K180;
        double Tu=timePeriod;
        
        //Ziegel-Nichols
        p.K=0.4*Ku;
        p.Ti=0.8*Tu;
    
        //hmm ska dessa användas även denna gång?
        p.Td=1;
        p.N=1;
        p.Tr=10; //tracking signal(=yref)
        p.Beta=1;
        p.H=0.1; //0.02-->0.1 
        p.integratorOn=false; //Integrator=false -->Ti och Tr orelevanta 
        
        return p;
    }

    /** Sets ReferenceGenerator (called from main) */
    public void setRefGen(ReferenceGenerator refGen) {
        this.refGen = refGen;
    }

    // Called in every sample in order to send plot data to OpCom
    private void sendDataToGUI(double yRef, double y, double u) {
        double x = (double) (System.currentTimeMillis() - startTime) / 1000.0;
        opCom.putControlData(x, u);
        opCom.putMeasurementData(x, yRef, y);
    }

    // Sets the controller's parameters
    //public void setParameters(PIDParameters p) {
    //    pi.setParameters(p);
    //}

    // Gets the controller's parameters
    public PIDParameters getParameters() {
        return pi.getParameters();
    }

    // Called from OpCom when shutting down
    public void shutDown() {
        shouldRun = false;
    }

    // Saturation function
    private double limit(double v) {
        return limit(v, -10, 10);
    }


    // Private method for implementing the relay with hysteresis
    private class Relay {

        private double reference;
        private double hysteresis;
        private double control;
        private boolean relayState;

        public HysteresisController(double reference, double hysteresis) {
            this.reference = reference;
            this.hysteresis = hysteresis;
        }

        public boolean control(double input) 
            if (input > reference + hysteresis && !relayState) {
                relayState = true;
            } else if (input < reference - hysteresis && relayState) {
                relayState = false;
            }
            return relayState; // To indicate on or off signal
        }

    }

