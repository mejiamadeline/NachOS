/**
 * Task 4: Communicator (30%)
 * 
 * 
 * Implement synchronous send and receive of one word (32-bit) messages (also known as Ada-style rendezvous), 
 * using condition variables (don’t use semaphores! -- IE USE Condition2.java). 
 * 
 * Implement the Communicator class with operations, void speak(int word) and int listen(). 
 * 
 * speak() atomically waits until listen() is called on the same Communicator object, and then transfers 
 * the word over to listen(). IE waits until the buffer is empty to run 
 * 
 * Once the transfer is made, both can return. 
 * 
 * Similarly, listen() waits until speak() is called, at which point the transfer is made, and both can return 
 * (listen() returns the word). 
 * 
 * This means that neither thread may return from listen() or speak() until the word transfer has been made. 
 * 
 * Your solution should work even if there are multiple speakers and listeners for the same Communicator 
 * (note: this is equivalent to a zero-length bounded buffer; since the buffer has no room, the producer 
 * and consumer must interact directly, requiring that they wait for one another). 
 * 
 * Each communicator should only use exactly one lock. 
 * 
 * If you’re using more than one lock, you’re making things too complicated.
 */



package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator 
{
    /**
     * Allocate a new communicator.
     */
	
	// Normal buffer, contains the info to be returned by listen()
	private int buffer;
	// Lock connected to the buffer
	private Lock communicatorLock;
	// Control if speak is asleep or awake
	private Condition2 speakCondition;
	// Control if listen is asleep or awake
	private Condition2 listenCondition;
	
	
    public Communicator() 
    {
    	// Declare variables
    	buffer = 0;
    	communicatorLock = new Lock();
    	speakCondition = new Condition2(communicatorLock);
    	listenCondition = new Condition2(communicatorLock);
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) 
    {
    	// Get the lock
    	communicatorLock.acquire();
    	
    	// Wait until buffer is free (a thread is now listening to this.communicator)
    	// In the meantime pause this.speak()
    	while (buffer != 0) speakCondition.sleep();
    	
    	// Store the input variable into the buffer
    	buffer = word;
    	// Signal listen to wake
    	listenCondition.wake();
    	
    	// Release the lock 
    	communicatorLock.release();	
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() 
    {
    	// Get the lock
    	communicatorLock.acquire();
    	
    	// Wait until the buffer is being used (because this.speak has put the input variable onto the buffer)
    	// Until then pause this.listen()
    	while (buffer == 0) listenCondition.sleep();
    	
    	// Store the data saved in the buffer and then reset it 
    	int word = buffer;
    	buffer = 0;
    	
    	// Signal speak() to wake now that the buffer is empty
    	speakCondition.wake();
    	
    	// Release the lock 
    	communicatorLock.release();	
    	
    	return word;
    }
}
