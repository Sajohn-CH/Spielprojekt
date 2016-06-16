
package mygame;

import com.jme3.app.state.AbstractAppState;

/**
 *
 * @author florianwenk
 */
public class Clock extends AbstractAppState{
	private static long time;
	private static long startTime;
	private static long passedTime;
	private static boolean running;
	
	public Clock(){
            startTime = 0;
            passedTime = 0;
            running = false;
	}
	
        @Override
        public void update(float tpf){
            if(running){
                time = System.currentTimeMillis()-startTime + passedTime;
            }
        }
        
        public void startClock(){
            running = true;
            startTime = System.currentTimeMillis();
        }
        
        public void pauseClock(){
            running = false;
            passedTime += System.currentTimeMillis()-startTime;
        }
        
        public void setPaused(boolean paused){
            if(paused){
                pauseClock();
            } else {
                startClock();
            }
        }
        
        public long getTime(){
            return time;
        }

}
