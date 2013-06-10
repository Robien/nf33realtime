package nf33.realtime.apirtdroid;


public class CapteurValue
{
	private long _timestampCaptureAnd; //capture de la valeur par android
	private long _timestampCaptureApi;		//capture de la valeur par l'api
	private float[] _values;
	private int _id;
	private int _type;
	
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

	public int getType()
	{
		return _type;
	}

	public void setType(int type)
	{
		this._type = type;
	}
	
	
	
}
