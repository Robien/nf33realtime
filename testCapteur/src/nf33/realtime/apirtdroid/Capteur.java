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
	private boolean isUsed;

	public Capteur(int id, Sensor sensor, Boolean isUsed)
	{
		setId(id);
		setSensor(sensor);
		setIsUsed(isUsed);
	}
	public Capteur(int id, Sensor sensor)
	{
		setId(id);
		setSensor(sensor);
		setIsUsed(false);
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

	public void setIsUsed(Boolean isUsed)
	{
		this.isUsed = isUsed;
	}

	public Boolean isUsed()
	{
		return this.isUsed;
	}

}
