/**
 * 
 */
package nf33.realtime.apirtdroid;

/**
 * @author Seg_fault_
 *
 */
public final class Tools
{
	//Conversion de Nanoseconde a Milliseconde
	static final float convertNanoToMilli = 1000000;
	
	//endore le thead pendant x nanoseconde
	static public void nanoWait(long nanos) throws InterruptedException
	{
		try
		{
				Thread.sleep(toMilli(nanos),remainderNano(nanos));	//attend la fin de la fenetre de capture des données capteurs
			
		}
		catch (InterruptedException e)
		{
			throw(e);
		}
	
	}
	
		//convertisseur de nanoseconde en milliseconde
		static public long toMilli(long nano)
		{
			return (long)(nano/convertNanoToMilli);
		}
	
	
		//donne le reste de nanoseconde sans les milliseconde
		static public int remainderNano(long nano)
		{
			return (int)(nano%convertNanoToMilli);
		}
		
		//convertie une date en nanoseconde en string
		static public String timeToString(long nano)
		{
			return "(" + toMilli(nano) + "ms" + remainderNano(nano) + "ns)";
		}
	
}
