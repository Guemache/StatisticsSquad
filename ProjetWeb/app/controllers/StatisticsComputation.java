/*********
 * Cette classe permet d'effectuer des calculs statistiques sur deux jeux de données
 * On l'instancie en appelant le constructeur auquel on passe en parametre deux ArrayList<String>
 * qui contient les données chiffrées de statistiques.
 * Les méthodes à appeler sont
 * double covariance = myStats.covariance();    => Covariance
 * 	double pearsonsCorrelation = myStats.pearsonsCorrelation();  => Correlation
 * 	double mean = myStats.mean();   => Moyenne
 * 	double standardDeviation  = myStats.standardDeviation();   => Ecart-type
 */


package controllers;

import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class StatisticsComputation{
	private ArrayList<Double> donnees1;
	private ArrayList<Double> donnees2;
	private double[] tabDonnees1;
	private double[] tabDonnees2;
	private DescriptiveStatistics standardDeviation;
	
	
	
	
	public StatisticsComputation(ArrayList<String> donneesFournies1, ArrayList<String> donneesFournies2)
	{
		
		tabDonnees1 = new double[donneesFournies1.size()];
		tabDonnees2 = new double[donneesFournies1.size()];
		standardDeviation = new DescriptiveStatistics();
		
		donnees1 = checkingGap(donneesFournies1, Math.max(donneesFournies1.size(), donneesFournies2.size()));
		
		
		tabDonnees1 = convertArrayListToTab(donnees1);

		donnees2 = checkingGap(donneesFournies2, Math.max(donneesFournies1.size(), donneesFournies2.size()));
		tabDonnees2 = convertArrayListToTab(donnees2);

	}
	
	
	private double[] convertArrayListToTab(ArrayList<Double> donnees)
	{
		 //On convertit les deux arrayLists en tableau
		double[] temp = new double[donnees.size()];
		temp = toPrimitive(donnees.toArray(new Double[donnees.size()]));
		
//		System.out.println("Tableau Converti "+ tabDonnees[1]);
		return temp;
	}
	
	
	public ArrayList<Double> checkingGap(ArrayList<String> arrayString, int tailleMax)
	{
		ArrayList <Double> arrayDouble = new ArrayList<Double>();
		double myLastData=0;
		double myDouble;
		//On convertit le tableau de données récupérées (sous forme de strings en Double.
		//Si une des valeurs n'est pas fournie, on met la valeur de la case précédente.
		for (String s : arrayString)
		{
			
			myDouble = Double.parseDouble(s);
			
			if (!(myDouble== 0))
			{
				arrayDouble.add(myDouble);
				myLastData = myDouble;
			}
			else
			{
				arrayDouble.add(myLastData);
			}
		}
		//On agrandi la taille du tableau à la taille du plus grand echantillon de données
		//à cause des définitions des méthodes de math3 
		while (arrayDouble.size()< tailleMax)
		{
			
			arrayDouble.add(myLastData);
		}
		return arrayDouble;
	}
	
	
	public double covariance()
	{
		Covariance myCovariance = new Covariance();		
		return myCovariance.covariance(tabDonnees1, tabDonnees2);		
	}
	
	public double pearsonsCorrelation()
	{
		PearsonsCorrelation myCorrelation = new PearsonsCorrelation();
		return myCorrelation.correlation(tabDonnees1, tabDonnees2);
	}
	
	
	public double mean()
	{
		standardDeviation=descriptiveStatistics();
		return standardDeviation.getMean();
	}
	
	public double standardDeviation()
	{
		
		standardDeviation=descriptiveStatistics();
		return standardDeviation.getStandardDeviation();
	}
	
	
	public DescriptiveStatistics descriptiveStatistics()
	{
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// Add the data from the array
		for( int i = 0; i < tabDonnees1.length; i++) {
		        stats.addValue(tabDonnees1[i]);
		}
		
		for( int i = 0; i < tabDonnees2.length; i++) {
	        stats.addValue(tabDonnees2[i]);
		}
		
		return stats;
	}
	
	public static double[] toPrimitive(Double[] array) {
		double[] temp= new double[array.length];

		  for (int i = 0; i < array.length; i++) {
		    temp[i] = array[i].doubleValue();
			  }
		  return temp;
		}
	
}

