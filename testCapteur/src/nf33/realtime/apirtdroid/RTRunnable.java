/**
 * 
 */
package nf33.realtime.apirtdroid;

/**
 * @author Seg_fault_
 *
 */
public interface RTRunnable
{
	
	public abstract void periodicEvent(long timeSinceLast);
	public abstract void endConfiguration(Boolean isRunable, float frequence);

}
