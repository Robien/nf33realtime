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
	//methode appel�e avant l'execution de la methode utilisateur
	public abstract void init();
	//methode d'execution
	public abstract void periodicEvent(long timeSinceLast, ArrayList<CapteurValue> capteursValues);
	//methode appel�e � la fin de la configuration
	public abstract void endConfiguration(Boolean isRunable, Long frequence, Long wcet);

}
