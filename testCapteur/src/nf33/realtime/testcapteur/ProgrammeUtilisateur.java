package nf33.realtime.testcapteur;

import java.util.ArrayList;

import android.app.Activity;
import android.hardware.SensorManager;
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
	
	//vitesse minimun pour détecter un shake
	private static final int FORCE_THRESHOLD = 350;
	// temps minimun pour commencer a détecter la position du téléphone
	private static final int TIME_THRESHOLD = 100;
	// temps minimun entre 2 position pour être considérer comme un shaker
	private static final int SHAKE_TIMEOUT = 500;
	// temps entre un 2 shake pour incrementer le nombre de shake
	private static final int SHAKE_DURATION = 1000;
	// nombre de shake pour lancer une action
	private static final int SHAKE_COUNT = 3;
	
	private SensorManager mSensorMgr;
	private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
	private long mLastTime;

	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	
	
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
		/*
		try
		{
			Thread.sleep((long) (Math.random()*90), 0);
			Message msg = mHandler.obtainMessage();
			msg.obj = new String("période : ");
			msg.arg1 = (int)(timeSinceLast);
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
		*/
		
		/***************Programme utilisateur***********************/
		Message msg = mHandler.obtainMessage();
		msg.obj = new String("période : ");
		msg.arg1 = (int)(timeSinceLast);
        mHandler.sendMessage(msg);
		if (capteursValues != null)
		{
			for (int i = 0; i < capteursValues.size(); ++i)
			{
				//si le l'un des capteur est un accelerometre
				if (capteursValues.get(i).getType() == SensorManager.SENSOR_ACCELEROMETER)
				{
					// le temps date de la capture
					long now = capteursValues.get(i).getTimestampCaptureApi();


					// si ce temps et le temps de la dernière position est trop
					// long, supérieur à SHAKE_TIMEOUT on recommence avec mShakeCount = 0;
					if ((now - mLastForce) > SHAKE_TIMEOUT)
					{
						mShakeCount = 0;
					}

					//difference entre la dernier calcul et maintenant
					long diff = now - mLastTime;
					if (diff > TIME_THRESHOLD)
					{
						
						// calcule de la vitesse de shake
						float speed = Math.abs(capteursValues.get(i).getValues()[SensorManager.DATA_X] 
								+ capteursValues.get(i).getValues()[SensorManager.DATA_Y] 
								+ capteursValues.get(i).getValues()[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
						// on passe cette étape si notre vitesse est supérieure à notre vitesse minimum FORCE_THRESHOLD
						if (speed > FORCE_THRESHOLD)
						{
							// si nous avons un nombre de shake supérieur à notre nombre minimun SHAKE_COUNT
							if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION))
							{
								
								// si toutes les conditions sont réunies je previens l'interface
								msg = mHandler.obtainMessage();
								msg.obj = new String("!! Grosse Secousse !!\n");
								msg.arg1 = (int)(speed*1000);
					            mHandler.sendMessage(msg);
					               
								mLastShake = now;
								mShakeCount = 0;			
							}
							mLastForce = now;
						}
						mLastTime = now;
						mLastX = capteursValues.get(i).getValues()[SensorManager.DATA_X];
						mLastY = capteursValues.get(i).getValues()[SensorManager.DATA_Y];
						mLastZ = capteursValues.get(i).getValues()[SensorManager.DATA_Z];

					}
				}
			}

		}

	}

}
