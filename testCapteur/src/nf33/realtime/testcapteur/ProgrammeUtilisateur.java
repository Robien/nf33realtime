package nf33.realtime.testcapteur;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import nf33.realtime.apirtdroid.CapteurValue;
import nf33.realtime.apirtdroid.RTDroid;
import nf33.realtime.apirtdroid.RTRunnable;

public class ProgrammeUtilisateur implements RTRunnable
{
	private Handler mHandler;
	private RTDroid rtdroid;
	
	ProgrammeUtilisateur(RTDroid rtdroid, Handler handler)
	{
		this.rtdroid = rtdroid;
		mHandler = handler;
	}
	



	@Override
	public void endConfiguration(Boolean isRunable, Long frequence, Long wcet)
	{
		// TODO Auto-generated method stub
		
	}




	@Override
	public void periodicEvent(long timeSinceLast, ArrayList<CapteurValue> capteursValues)
	{
		// TODO Auto-generated method stub
		try
		{
			Thread.sleep((long) (Math.random()*90), 0);
			Message msg = mHandler.obtainMessage();
			msg.obj = new String("periode : " +timeSinceLast);
            mHandler.sendMessage(msg);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (capteursValues == null)
		{
			try
			{
				Thread.sleep((long) (Math.random()*15), 0);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			Log.d("DADU", "VIVE LES POMMES");
			Log.d("DADUtilisateur", "" +capteursValues.get(0).getValues()[0]);
		}
		
	}

}
