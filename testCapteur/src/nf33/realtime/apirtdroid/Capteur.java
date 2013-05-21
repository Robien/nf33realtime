/**
 * 
 */
package nf33.realtime.apirtdroid;

/**
 * @author Seg_fault_
 *
 */
public class Capteur
{
	
	private int id;
	
	
	public Capteur(int id)
	{
		setId(id);
	
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return this.id;
	}
	

}
