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
	private static final float convertNanoToMilli = 1000000;
	// sleep android milli
	public static final int WAIT_MILLI = 0;
	// sleep android milli + nano
	public static final int WAIT_NANO = 1;
	// sleep actif
	public static final int WAIT_ACTIVE = 2;
	//Type d'attente
	private static int _type_wait = WAIT_NANO;
	// pas d'attente de compensation
	private static boolean _pendingCompensation = true;

	//Constante nombre d'iteration du pendant le calcul du WCET
	public static final int NB_ITER_WCET_UTIL = 1000;
	public static final int NB_ITER_WCET_API = 20;
	public static final int MAX_WAIT_WCET_CAPTEUR = 10000;
	
	//sauvegarde des log activé
	private static boolean _logActived = false;
	
	// endore le thead pendant x nanoseconde
	static public void nanoWait(long nanos) throws InterruptedException
	{

		Thread.sleep(toMilli(nanos), remainderNano(nanos)); // attend la fin de la fenetre de capture des données capteurs
	}

	// endore le thread pendant x nanoseconde
	static public void waitTime(long nanos) throws InterruptedException
	{
		switch (_type_wait)
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
		switch (_type_wait)
		{
		case WAIT_MILLI:
			toMilli(nanos);
			break;
		case WAIT_ACTIVE:
			@SuppressWarnings("unused") //non utlisé parce que c'est une simmulation
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
		return "(" + toMilli(nano) + "ms " + remainderNano(nano) + "ns)";
	}

	// convertie le type de précision en string
	static public String typeWaitToString()
	{
		switch (_type_wait)
		{
		case WAIT_MILLI:
			return "WAIT_MILLI";
		case WAIT_ACTIVE:
			return "WAIT_ACTIVE";
		default:
			return "WAIT_NANO";
		}
	}

	public static int getTypeWait()
	{
		return _type_wait;
	}

	public static void setTypeWait(int typewait)
	{
		Tools._type_wait = typewait;
	}

	public static boolean isLogActived()
	{
		return _logActived;
	}

	public static void setLogActived(boolean logActived)
	{
		Tools._logActived = logActived;
	}

	public static boolean isPendingCompensation()
	{
		return _pendingCompensation;
	}

	public static void setPendingCompensation(boolean pending_compensation)
	{
		Tools._pendingCompensation = pending_compensation;
	}
	
	
 
}
