/**
 * 
 */
package nf33.realtime.apirtdroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * @author Seg_fault_
 * 
 */
public class Capteur
{

	private int id;
	private Sensor sensor;
	private boolean isUsed;
	private Long maxPeriod;
	private String name;
	private SensorEvent lastSensorEvent;
	private CapteurValue capteurValue;

	public Capteur(int id, Sensor sensor, Boolean isUsed)
	{
		init(id, sensor, isUsed);
	}
	public Capteur(int id, Sensor sensor)
	{
		init(id, sensor, false);
	}
	
	private void init(int id, Sensor sensor, Boolean isUsed)
	{
		
		setIsUsed(isUsed);
		setId(id);
		setSensor(sensor);
		maxPeriod = 0l;
		name = new String("");
		lastSensorEvent = null;
		capteurValue = new CapteurValue();
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
	
	public void setMaxPeriode(Long maxPeriode)
	{
		this.maxPeriod = maxPeriode;
	}
	
	public Long getMaxPeriode()
	{
		return maxPeriod;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public SensorEvent getLastSensorEvent()
	{
		return lastSensorEvent;
	}

	public void setLastSensorEvent(SensorEvent lastSensorEvent)
	{
		capteurValue.setWithEvent(lastSensorEvent);
		this.lastSensorEvent = lastSensorEvent;
	}
	
	public CapteurValue getCapteurValue()
	{
		return capteurValue;
	}
	public void setRandomValues()
	{
		capteurValue.setRandomValues();
	}
}
