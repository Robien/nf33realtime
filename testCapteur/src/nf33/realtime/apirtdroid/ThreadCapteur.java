package nf33.realtime.apirtdroid;

public class ThreadCapteur extends Thread
{

	private CapteurManager capteurManager;
	private RTRunnable programmeUtilisateur;

	public ThreadCapteur(CapteurManager mgr, RTRunnable programmeUtilisateur)
	{
		capteurManager = mgr;
		this.programmeUtilisateur = programmeUtilisateur;

	}

	public void run()
	{

		capteurManager.startMesure();
		while (capteurManager.nextCapteur())
		{
			try
			{
				sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		programmeUtilisateur.endConfiguration(true, 0);
	}
}
