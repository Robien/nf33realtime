/**
 * 
 */
package nf33.realtime.apirtdroid;

import android.hardware.Sensor;

/**
 * @author Seg_fault_
 *
 */
public class Capteur
{
	
	private int id;
	private Sensor sensor;
	
	
	public Capteur(int id, Sensor sensor)
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
	public void setSensor(Sensor sensor)
	{
		this.sensor = sensor;
	}
	
	public Sensor getSensor()
	{
		return this.sensor;
	}
	

}
