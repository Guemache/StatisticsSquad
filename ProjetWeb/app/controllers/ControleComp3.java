package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import models.Comment;
import models.Graphe;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.comp1;
import views.html.comp3;

public class ControleComp3 extends Controller {
	
	public static final String rdf_file0 = "input/crim_gen.rdf";
	public static final String rdf_file1 = "input/crim_gen.ttl";
	private static String anneeDebut;
	private static String anneeFin;
	
	public static Result comp3(){
		
		//Requête pour récupèrer le nombre de viols en espagne
   	    //requete pour recuperer les valeurs , les dates et les pays avec filtre date et pays 
    	// creattion d modele 
		Model m = ModelFactory.createDefaultModel();
		 // j'int�gre mon modele dans un autre modele inf�r� 
		InfModel infm = ModelFactory.createRDFSModel(m);
		 // je lis les deus fichier .RDF et .ttl  pour le sujet crime 		
		String ns = "http://www.StatisticSquade.fr#";
	 	infm.setNsPrefix("StatisticSquade", ns);
	 	/// name space de eurostat
	 	String nsEuro = "http://eurostat.linked-statistics.org/data/";
		infm.setNsPrefix("Eurostat", nsEuro);
		FileManager.get().readModel(infm, rdf_file0 );
		FileManager.get().readModel(infm, rdf_file1 );   
		
		//Construction dynamique des requêtes
				/**
				 * Récupération des pays
				 */
			
				/**
				 * 
				 * récupération autes
				 */
		String pays1 = Form.form().bindFromRequest().get("pays1");
		String pays2 = Form.form().bindFromRequest().get("pays2");
		String valeurs = Form.form().bindFromRequest().get("valeurs");
		anneeDebut = Form.form().bindFromRequest().get("anneeDebut");
		anneeFin = Form.form().bindFromRequest().get("anneeFin");
		String sujet = Form.form().bindFromRequest().get("sujet");
		String [] tabValeurs = valeurs.split(",");
		String [] tabAnneeDebut = anneeDebut.split("-");
        String anneeD = tabAnneeDebut[0];
        int aDebut = Integer.parseInt(anneeD);
        String [] tabAnneeFin = anneeFin.split("-");
        String anneeF = tabAnneeFin[0];
        int aFin = Integer.parseInt(anneeF);
        
        if(aFin < aDebut){
        	String anneeTemp = anneeDebut;
        	anneeDebut = anneeFin;
        	anneeFin = anneeTemp;
        }
		
        //Requête pour récupération des valeurs du premier pays existant dans l'ontologie EuroStat
        String rdq = 
	  			 
		 		"PREFIX sdmx-dimension:  <http://purl.org/linked-data/sdmx/2009/dimension#>" +
				"PREFIX property:  <http://eurostat.linked-statistics.org/property#>" +
		        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			    "PREFIX sdmx-measure:  <http://purl.org/linked-data/sdmx/2009/measure#>" +
			    "PREFIX skos:    <http://www.w3.org/2004/02/skos/core#>" +
			    "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>" +
			    "PREFIX qb:      <http://purl.org/linked-data/cube#>" +
			    "PREFIX  rdfs:    <http://www.w3.org/2000/01/rdf-schema#>" +
			    "PREFIX StatisticSquade: <http://www.StatisticSquade.fr#>"  +
			   
		 		"SELECT  " +
		 	" ?Pays  ?Date ?Valeur   " +
				 		"FROM <http://eurostat.linked-statistics.org/data/crim_gen.rdf>" +
				 		"FROM <http://eurostat.linked-statistics.org/dsd/crim_gen.ttl>" +
		 		"WHERE {" +
		 					" ?x sdmx-dimension:timePeriod ?Date  . " +
					 		" ?x sdmx-measure:obsValue ?Valeur ." +
				 		     "?x property:geo ?y ." +
				 		     "?y skos:prefLabel  ?Pays  ." +
					 		 " ?x property:crim ?z . " +
					 	  	 "?z skos:notation ?l . " +
					 		 "FILTER regex( ?l ,\""+sujet+"\" ) . " +

					 		"  FILTER ( ?Date >= \""+anneeDebut+"\"^^xsd:date && ?Date <= \""+anneeFin+"\"^^xsd:date ) ." +
					 		"FILTER regex (?Pays , \""+pays1+"\" ) . " +
		 		" }  "; 
		
      //mapping entre sujets et leurs codes
        HashMap<String,String> codeToSujet = new HashMap<String,String>();
        codeToSujet.put("DBURG", "Cambriolages dans un lieu d'habitation");
        codeToSujet.put("DRUGT", "Trafic de stupéfiants");
        codeToSujet.put("HCIDE", "Homicides");
        codeToSujet.put("VTHFT", "Vols de véhicules à moteur");
        codeToSujet.put("VIOLT", "Crimes et délits violents");
        codeToSujet.put("ROBBR", "Vols avec violences");
        
        //Execution des requêtes
        /**
         * Requête 1
         */
        Query query1 = QueryFactory.create(rdq); 
	    QueryExecution qexec1 = QueryExecutionFactory.create(query1,m);
	      /////////
	      ResultSet rs1 = qexec1.execSelect() ;
	      //Transformation en List de querySolution
	      List<QuerySolution> liste1 = ResultSetFormatter.toList(rs1);
	      Iterator<QuerySolution> it1 = liste1.iterator();
         
         ////////// Mise des résultats dans une hashmap
        HashMap<Integer,String> map1 = new HashMap<Integer,String>(); 
        //////////////
        ArrayList<String> donneesString1 = new ArrayList<String>();

        while(it1.hasNext()){
       	  
       	  QuerySolution elt1 = it1.next();
             String anneeElt1 = elt1.get("Date").toString();
             String valeur1 = elt1.get("Valeur").toString();
             String [] tabAnnee1 = anneeElt1.split("-");
             String annee1 = tabAnnee1[0];
             map1.put(Integer.parseInt(annee1), valeur1);
             donneesString1.add(valeur1);
             
       	  
         }
        //Pour ordonner la HashMap en se basant sur la clé
        TreeMap<Integer, String> mapOrd1 = new TreeMap<Integer,String>(map1);
        
        /**
         * 
         * traitement pour le pays entré par l'utilisateur
         */
        
        List<String> valeursTab =  Arrays.asList(tabValeurs);
        Iterator<String> itValeurs = valeursTab.iterator();
        HashMap<Integer,String> map2 = new HashMap<Integer,String>();
        int intervalAnnee = aFin - aDebut;
        ArrayList<Integer> tabAnnees = new ArrayList<Integer>();
        Iterator<Integer> itAnnees = tabAnnees.iterator();
        ArrayList<String> donneesString2 = new ArrayList<String>();
        Iterator<String> it2 = valeursTab.iterator();
        while(it2.hasNext()){
        	String val = it2.next();
        	donneesString2.add(val);
        }
        
        	
        for(int i=0;i<=intervalAnnee;i++){
        	tabAnnees.add(aDebut+i);
        }
        System.out.println( valeursTab.toString());
        System.out.println(tabAnnees.toString());
        
       if(valeursTab.size() <= tabAnnees.size()){
       int k =0;
       while(k<valeursTab.size()){
    	   map2.put(tabAnnees.get(k), valeursTab.get(k));
    	   k++;
       }
	}
       int j = 0;
       if(valeursTab.size() > tabAnnees.size()){
    	   while(j<tabAnnees.size()){
        	   map2.put(tabAnnees.get(j), valeursTab.get(j));
        	   j++;
           }	
    	   
       }
        
        TreeMap<Integer, String> mapOrd2 = new TreeMap<Integer,String>(map2);
        
        //////////
        JsonObject title = new JsonObject();
        title.put("text", "Nombre de "+codeToSujet.get(sujet));
        
        JsonObject subtitle = new JsonObject();
        subtitle.put("text", pays1+" et "+pays2);
        
        JsonObject xAxis = new JsonObject();
        xAxis.put("type", "value");
        
        JsonObject titleY = new JsonObject();
        titleY.put("text", "valeurs :");
        JsonObject yAxis = new JsonObject();
        yAxis.put("title", titleY);
        yAxis.put("min", 0);
        
        JsonArray series = new JsonArray();
        
        JsonObject objSerie1 = new JsonObject();
        objSerie1.put("name", pays1);
        JsonArray data1 = new JsonArray();
        for(Entry<Integer, String> entry1 : mapOrd1.entrySet()){
      	  
      	  JsonArray eltData1 = new JsonArray();
            eltData1.add(entry1.getKey());
            eltData1.add(Integer.parseInt(entry1.getValue()));
            data1.add(eltData1);
            
        }
        objSerie1.put("data",data1);
        series.add(objSerie1);
        
    
        
        JsonObject objSerie2 = new JsonObject();
        objSerie2.put("name", pays2);
        JsonArray data2 = new JsonArray(); 
        for(Entry<Integer, String> entry2 : mapOrd2.entrySet()){
      	  
      	  JsonArray eltData2 = new JsonArray();
            eltData2.add(entry2.getKey());
            eltData2.add(Integer.parseInt(entry2.getValue()));
            data2.add(eltData2);
        }	 
       objSerie2.put("data",data2); 
       series.add(objSerie2);
           
      
      //Ajouter au graphe
      JsonObject graphe = new JsonObject();
      graphe.put("title", title);
      graphe.put("subtitle", subtitle);
      graphe.put("xAxis", xAxis);
      graphe.put("yAxis", yAxis);
      graphe.put("series", series);
      
      /**
       * 
       * données statistiques
       */
      
      
      StatisticsComputation myStats = new StatisticsComputation(donneesString1, donneesString2);
      double covariance = myStats.covariance();
		double pearsonsCorrelation = myStats.pearsonsCorrelation();
		System.out.println("Ma covariance " + covariance);
		System.out.println("Ma correlation " + pearsonsCorrelation);
		double mean = myStats.mean();
		double standardDeviation  = myStats.standardDeviation();
		System.out.println("Ma moyenne " + mean);
		System.out.println("Mon écart-type " + standardDeviation);

      /**
       * 
       * Données statistiques fin
       * 
       * 
       */
		/**
		 * Ajout au graphe
		 */
		GraphCreation gc = new GraphCreation("crim_gen");
		gc.postGraph(sujet+"-"+pays1+"-"+pays2, sujet+pays1, sujet+pays2, anneeDebut, anneeFin, 
				     Double.toString(pearsonsCorrelation), Double.toString(mean), Double.toString(standardDeviation), 
				     Double.toString(covariance));
		gc.save();
		
        String ip = request().remoteAddress();
        ArrayList<String> listeComments = gc.getComments(sujet+"-"+pays1+"-"+pays2, false);   
        if(listeComments !=null){
		Iterator<String> it = listeComments.iterator();
		List<Comment> listeCom = new ArrayList<Comment>();
		while(it.hasNext()){
			String elt = it.next();
			String [] tab = elt.split(";");
			String nomRecup = tab[0];
			String dateRecup = tab[1];
			String contenuRecup = tab[2];
			Comment comment1 = new Comment(nomRecup,dateRecup,contenuRecup);
			listeCom.add(comment1);
		}
        Graphe g = new Graphe(graphe.toString(),covariance,pearsonsCorrelation,mean,standardDeviation,listeCom,ip,sujet+"-"+pays1+"-"+pays2,null);		
        return ok(comp1.render(g));
        }else{
        	Graphe g = new Graphe(graphe.toString(),covariance,pearsonsCorrelation,mean,standardDeviation,null,ip,sujet+"-"+pays1+"-"+pays2,null);		
            return ok(comp1.render(g));
        	
        }
	}

}
