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
	// Conversion de Nanoseconde a Milliseconde
	static final float convertNanoToMilli = 1000000;
	// sleep android milli
	public static final int WAIT_MILLI = 0;
	// sleep android milli + nano
	public static final int WAIT_NANO = 1;
	// sleep actif
	public static final int WAIT_ACTIVE = 2;
	public static int type_wait = WAIT_NANO;

	// endore le thead pendant x nanoseconde
	static public void nanoWait(long nanos) throws InterruptedException
	{

		Thread.sleep(toMilli(nanos), remainderNano(nanos)); // attend la fin de
															// la fenetre de
															// capture des
															// données capteurs
	}

	// endore le thread pendant x nanoseconde
	static public void waitTime(long nanos) throws InterruptedException
	{
		switch (type_wait)
		{
		case WAIT_MILLI:
			Thread.sleep(toMilli(nanos));
			break;
		case WAIT_ACTIVE:

			long waitDate = System.nanoTime() + nanos;

			while (System.nanoTime() < waitDate)
			{
				Thread.sleep(0, 1);
			}

			break;
		default:
			Thread.sleep(toMilli(nanos), remainderNano(nanos));
			break;
		}
	}

	// simule l'endormissement le thread pendant x nanoseconde
	static public void simulewaitTime(long nanos) 
	{
		switch (type_wait)
		{
		case WAIT_MILLI:
			toMilli(nanos);
			break;
		case WAIT_ACTIVE:
			long waitDate = System.nanoTime() + nanos;
			break;
		default:
			toMilli(nanos);
			remainderNano(nanos);
			break;
		}
	}

	// convertisseur de nanoseconde en milliseconde
	static public long toMilli(long nano)
	{
		return (long) (nano / convertNanoToMilli);
	}

	// donne le reste de nanoseconde sans les milliseconde
	static public int remainderNano(long nano)
	{
		return (int) (nano % convertNanoToMilli);
	}

	// convertie une date en nanoseconde en string
	static public String timeToString(long nano)
	{
		return "(" + toMilli(nano) + "ms" + remainderNano(nano) + "ns)";
	}

	// convertie le type de précision en string
	static public String typeWaitToString()
	{
		switch (type_wait)
		{
		case WAIT_MILLI:
			return "WAIT_MILLI";
		case WAIT_ACTIVE:
			return "WAIT_ACTIVE";
		default:
			return "WAIT_NANO";
		}
	}

}
