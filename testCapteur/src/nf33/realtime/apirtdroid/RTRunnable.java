/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.util.ArrayList;

/**
 * @author Seg_fault_
 * Classe interface d'application utilisateur
 * 
 */
public interface RTRunnable
{
	//methode appelée avant l'execution de la methode utilisateur
	public abstract void init();
	//methode appelée avant l'execution de la methode utilisateur
	public abstract void progressConfiguration(int etape, int percent);
	//methode d'execution
	public abstract void periodicEvent(long timeSinceLast, ArrayList<CapteurValue> capteursValues);
	//methode appelée à la fin de la configuration
	public abstract void endConfiguration(Boolean isRunable, Long frequence, Long wcet);

}
