/*
 * Task 5: Priority Scheduling (50%)
 * 
 * Implement Priority Scheduling by completing the PriorityScheduler class. Remember to use the nachos.conf 
 * file that uses the PriorityScheduler class. When implementing priority scheduling, you will find a point where 
 * you can easily compute the effective priority of thread, but the computation takes a long time. To get full 
 * credit, you need to speed this up by caching the effective priority and only recalculating it when it may be 
 * possible for it to change.
 * 
 * As before, do not modify any classes to solve this task. The solution should involve creating a subclass of 
 * ThreadQueue that will work with the existing Lock, Semaphore, and Condition classes.
 * 
 * You should implement priority donation first, then add donation, and finally restoration.
 * 
 */

package nachos.threads;

import nachos.machine.*;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A scheduler that chooses threads based on their priorities.
 *
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 *
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 *
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
    /**
     * Allocate a new priority scheduler.
     */
    public PriorityScheduler() {
    }
    
    /**
     * Allocate a new priority thread queue.
     *
     * @param	transferPriority	<tt>true</tt> if this queue should
     *					transfer priority from waiting threads
     *					to the owning thread.
     * @return	a new priority thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
    	return new PriorityQueue(transferPriority);
    }

    public int getPriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());
			       
		return getThreadState(thread).getPriority();
    }

    public int getEffectivePriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());
			       
		return getThreadState(thread).getEffectivePriority();
    }

    public void setPriority(KThread thread, int priority) {
		Lib.assertTrue(Machine.interrupt().disabled());
			       
		Lib.assertTrue(priority >= priorityMinimum &&
			   priority <= priorityMaximum);
		
		getThreadState(thread).setPriority(priority);
    }

    public boolean increasePriority() {
		boolean intStatus = Machine.interrupt().disable();
			       
		KThread thread = KThread.currentThread();
	
		int priority = getPriority(thread);
		if (priority == priorityMaximum)
		    return false;
	
		setPriority(thread, priority+1);
	
		Machine.interrupt().restore(intStatus);
		return true;
    }

    public boolean decreasePriority() {
		boolean intStatus = Machine.interrupt().disable();
			       
		KThread thread = KThread.currentThread();
	
		int priority = getPriority(thread);
		if (priority == priorityMinimum)
		    return false;
	
		setPriority(thread, priority-1);
	
		Machine.interrupt().restore(intStatus);
		return true;
    }

    /**
     * The default priority for a new thread. Do not change this value.
     */
    public static final int priorityDefault = 1;
    /**
     * The minimum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMinimum = 0;
    /**
     * The maximum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMaximum = 7;    

    /**
     * Return the scheduling state of the specified thread.
     *
     * @param	thread	the thread whose scheduling state to return.
     * @return	the scheduling state of the specified thread.
     */
    protected ThreadState getThreadState(KThread thread) {
		if (thread.schedulingState == null)
		    thread.schedulingState = new ThreadState(thread);
	
		return (ThreadState) thread.schedulingState;
    }

    /**
     * A <tt>ThreadQueue</tt> that sorts threads by priority.
     */
    protected class PriorityQueue extends ThreadQueue {
    	PriorityQueue(boolean transferPriority) {
		    this.transferPriority = transferPriority;
		}

    	public void waitForAccess(KThread thread) {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    getThreadState(thread).waitForAccess(this);
		}

    	public void acquire(KThread thread) {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    getThreadState(thread).acquire(this);
		}

    	
    	// MODIFY
    	// Remove the highest priority thread from the wait queue
    	public KThread nextThread() {
    		Lib.assertTrue(Machine.interrupt().disabled());
    		ThreadState thread = pickNextThread();
    		if (thread == null)
    		{
    			return null;
    		}
    		
    		}
    	// implement me
		/**
		 * Return the next thread that <tt>nextThread()</tt> would return,
		 * without modifying the state of this queue.
		 *
		 * @return	the next thread that <tt>nextThread()</tt> would
		 *		return.
		 */
    	// MODIFY
		protected ThreadState pickNextThread() {
			Lib.assertTrue(Machine.interrupt().disabled());
		    // implement me
			ThreadState nextThread = null;
    		if ()
    		{
    			return null;
    		}
    		
		}
		
		public void print() {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    System.out.println("Priority Queue: ");
		    
		    // implement me (if you want)
		}
	
		/**
		 * <tt>true</tt> if this queue should transfer priority from waiting
		 * threads to the owning thread.
		 */
		public boolean transferPriority;
    }

    /**
     * The scheduling state of a thread. This should include the thread's
     * priority, its effective priority, any objects it owns, and the queue
     * it's waiting for, if any.
     *
     * @see	nachos.threads.KThread#schedulingState
     */
    protected class ThreadState {
    	
    	
    	
	/**
		 * Allocate a new <tt>ThreadState</tt> object and associate it with the
		 * specified thread.
		 *
		 * @param	thread	the thread this state belongs to.
		 */
		public ThreadState(KThread thread) {
		    this.thread = thread;
		    this.activeResources = new LinkedList<PriorityQueue>();
	    	    this.futureResources = new LinkedList<PriorityQueue>();
		    this.threadsWaiting = null;
	    	    effectivePriority = priorityMinimum;
		    setPriority(priorityDefault);
		}
	
		/**
		 * Return the priority of the associated thread.
		 *
		 * @return	the priority of the associated thread.
		 */
		public int getPriority() {
		    return priority;
		}
	
		/**
		 * Return the effective priority of the associated thread.
		 *
		 * @return	the effective priority of the associated thread.
		 */
		// MODIFY
		public int getEffectivePriority() {
		    	// implement me
			
			// IF no resources return this priority
			// Otherwise find the max priority and donate it here

			if (activeResources.isEmpty()) {
				return priority;
			} else {
				effectivePriority = priorityMinimum;
               		 	for (final ThreadState curr : this.threadsWaiting) {
                			effectivePriority = Math.max(effectivePriority, curr.getEffectivePriority());
               		 	}
			}

			
		    return effectivePriority;
		}
	
		/**
		 * Set the priority of the associated thread to the specified value.
		 *
		 * @param	priority	the new priority.
		 */
		// MODIFY
		public void setPriority(int priority) {
		    if (this.priority == priority)
			return;
		    
		    this.priority = priority;
		    // implement me
		}
	
		/**
		 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
		 * the associated thread) is invoked on the specified priority queue.
		 * The associated thread is therefore waiting for access to the
		 * resource guarded by <tt>waitQueue</tt>. This method is only called
		 * if the associated thread cannot immediately obtain access.
		 *
		 * @param	waitQueue	the queue that the associated thread is
		 *				now waiting on.
		 *
		 * @see	nachos.threads.ThreadQueue#waitForAccess
		 */
		// MODIFY
		// DONE
		public void waitForAccess(PriorityQueue waitQueue) {
			Lib.assertTrue(Machine.interrupt().disabled());
		    // 
			futureResources.add(waitQueue);
			activeResources.remove(waitQueue);
		}
	
		/**
		 * Called when the associated thread has acquired access to whatever is
		 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
		 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
		 * <tt>thread</tt> is the associated thread), or as a result of
		 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
		 *
		 * @see	nachos.threads.ThreadQueue#acquire
		 * @see	nachos.threads.ThreadQueue#nextThread
		 */
		// MODIFY
		public void acquire(PriorityQueue waitQueue) {
		    // implement me
			futureResources.remove(waitQueue);
			activeResources.add(waitQueue);
			
		}	
	
		/** The thread with which this object is associated. */	   
		protected KThread thread;
		/** The priority of the associated thread. */
		protected int priority;
		
		protected final List<PriorityQueue> activeResources;
		protected final List<PriorityQueue> futureResources;
	    	protected final List<ThreadState> threadsWaiting;
		protected int effectivePriority = priorityMinimum;
    }
}
