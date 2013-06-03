package nf33.realtime.apirtdroid;


public class CapteurValue
{
	long _timestampCaptureAnd; //capture de la valeur par android
	long _timestampCaptureApi;		//capture de la valeur par l'api
	float[] _values;
	private int _id;
	public CapteurValue(int id, long timestampCaptureAnd, float[] values)
	{
		super();
		this._id = id;
		this._timestampCaptureAnd = timestampCaptureAnd;
		this._values = values;
	}
	
	public CapteurValue()
	{
		super();
	}
	
	public long getTimestampCaptureAnd()
	{
		return _timestampCaptureAnd;
	}
	
	public void setTimestampCaptureAnd(long timestampCaptureAnd)
	{
		this._timestampCaptureAnd = timestampCaptureAnd;
	}
	
	public long getTimestampCaptureApi()
	{
		return _timestampCaptureApi;
	}
	
	public void setTimestampCaptureApi(long timestampCaptureApi)
	{
		this._timestampCaptureApi = timestampCaptureApi;
	}
	
	public float[] getValues()
	{
		return _values;
	}
	
	public void setValues(float[] values)
	{
		this._values = values;
	}
	
	
	
}
