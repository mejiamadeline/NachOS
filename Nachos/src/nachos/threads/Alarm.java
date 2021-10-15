package nachos.threads;
import java.util.*;
import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    private KThread currentThread;
    LinkedList<KThread> readyQueue = new LinkedList<>(); //keep track of threads
    LinkedList<Long> timingQueue = new LinkedList<>(); //keep track of times of threads
    
    public void timerInterrupt() {
    	
    	long timeCheck = Machine.timer().getTime(); // gets the the current time ticks
    	
    	boolean status = Machine.interrupt().disable(); // disables interrupts
    	
    	if (timeCheck >= timingQueue.peek()) //checks if the current time is great or equal to the first time in the queue 
    	{
    		readyQueue.remove(); //removes the first thread from the list 
    		timingQueue.remove(); //removes the first time from the list 
    	} //this code block is to wake up the thread 
    	
    	Machine.interrupt().restore(status); // re-enables interrupts
    	
    	KThread.currentThread().yield(); //yields the current thread and context switches 
    	
    	
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
    
	long wakeTime = Machine.timer().getTime() + x; //a variable with current ticks plus x ticks
	
	timingQueue.add(wakeTime); //adds the wake time to the timing queue in order to be able to be used in the timerInterrupt function
	
	currentThread = KThread.currentThread(); //a variable with the current thread 
	
	readyQueue.add(currentThread); //adds the current thread to the ready queue 
	
	currentThread.sleep(); // puts the current thread to sleep 
	//while (wakeTime > Machine.timer().getTime()); not sure if needed anymore 
	// KThread.yield(); not sure if need anymore
    }
}
