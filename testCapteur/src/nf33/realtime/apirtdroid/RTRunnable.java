/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.util.ArrayList;

/**
 * @author Seg_fault_
 *
 */
public interface RTRunnable
{
	
	public abstract void periodicEvent(long timeSinceLast, ArrayList<CapteurValue> capteursValues);
	public abstract void endConfiguration(Boolean isRunable, Long frequence, Long wcet);

}
