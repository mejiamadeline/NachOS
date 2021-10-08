package nachos.threads;

import nachos.machine.*;

import java.util.*; 

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
	
	private Lock conditionLock;
	private int val;
	private LinkedList wait;
	private ThreadQueue waitTQ = ThreadedKernel.scheduler.newThreadQueue(false);

	
	
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
    	this.conditionLock = conditionLock;
    	wait = new LinkedList();
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically re acquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
    	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    	val = 0;
    	wait.add(val);
    	conditionLock.release();
    	
    	boolean status = Machine.interrupt().disable();
    	if(val == 0) {
    		waitTQ.waitForAccess(KThread.currentThread());
    		KThread.sleep();
    	}else 
    		val--;
    	
    	Machine.interrupt().restore(status);
    	conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
    	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    	
    	boolean status = Machine.interrupt().disable();
    	if(!wait.isEmpty()) {
    		wait.removeFirst();
    		KThread thread = waitTQ.nextThread();
    		if(thread != null) 
    			thread.ready();
    		else
    			val++;
    	}
    	
    	Machine.interrupt().restore(status);

    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
    	
    	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    	while(!wait.isEmpty()) {
    		wake();
    	}
    	
    }
    
}
