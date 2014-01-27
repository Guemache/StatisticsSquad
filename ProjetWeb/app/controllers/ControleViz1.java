package controllers;

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
import models.Graphe;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class ControleViz1 extends Controller {
	
	public static final String rdf_file0 = "input/crim_gen.rdf";
	public static final String rdf_file1 = "input/crim_gen.ttl";
	private static String annee;
	
	//visualisation des résulats du formuaire Viz1
	
    public static Result viz1(){
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
		FileManager.get().readModel( infm, rdf_file0 );
		FileManager.get().readModel( infm, rdf_file1 );   
		//construction de la requête
		String paysForm = Form.form().bindFromRequest().get("pays");
		annee = Form.form().bindFromRequest().get("annee");
		String sujet = Form.form().bindFromRequest().get("sujet");
		
		String rdq1 = 
	  			 
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

					 		"  FILTER ( ?Date = \""+annee+"\"^^xsd:date ) ." +
					 		"FILTER regex (?Pays , \""+paysForm+"\" ) . " +
		 		" }  ";
		
		Query query = QueryFactory.create(rdq1); 
	     QueryExecution qexec = QueryExecutionFactory.create(query,m);
	      /////////
	      ResultSet rs = qexec.execSelect() ;
	      //Transformation en List de querySolution
	      List<QuerySolution> liste = ResultSetFormatter.toList(rs);
	      Iterator<QuerySolution> it = liste.iterator();
         String pays1 = paysForm;
         ////////// Mise des résultats dans une hashmap
        HashMap<Integer,String> map = new HashMap<Integer,String>(); 
        
        while(it.hasNext()){
       	  
       	  QuerySolution elt = it.next();
       	  System.out.println(elt);
             String anneeElt = elt.get("Date").toString();
             String valeur = elt.get("Valeur").toString();
             String [] tabAnnee = anneeElt.split("-");
             String annee = tabAnnee[0];
             map.put(Integer.parseInt(annee), valeur);	  
       	  
         }
        //Pour ordonner la HashMap en se basant sur la clé
        TreeMap<Integer, String> mapOrd = new TreeMap<Integer,String>(map);
      //Création de l'objet JSON
        HashMap<String,String> codeToSujet = new HashMap<String,String>();
        codeToSujet.put("DBURG", "Cambriolages dans un lieu d'habitation");
        codeToSujet.put("DRUGT", "Trafic de stupéfiants");
        codeToSujet.put("HCIDE", "Homicides");
        codeToSujet.put("VTHFT", "Vols de véhicules à moteur");
        codeToSujet.put("VIOLT", "Crimes et délits violents");
        codeToSujet.put("ROBBR", "Vols avec violences");
        //title
        JsonObject title = new JsonObject();
        title.put("text", "nombre de "+codeToSujet.get(sujet));
        title.put("x", new Integer(-20));
        //subtitle
        JsonObject subtitle = new JsonObject();
        subtitle.put("text", "en "+pays1);
        subtitle.put("x", new Integer(-20));
        //xAxis
        JsonObject xAxis = new JsonObject();
        JsonArray listeAnnee = new JsonArray(); 
        for(Entry<Integer, String> entry : mapOrd.entrySet()){
      	 listeAnnee.add(entry.getKey());
        }
        //création de yAxis
        JsonObject yAxis = new JsonObject();
        //Ajout d'un title à yAxis
        JsonObject titleY = new JsonObject();
        titleY.put("text", "Valeurs");
        yAxis.put("title", titleY);
        //Ajout de plotlines à y Axis
        JsonArray plotlines = new JsonArray();
        yAxis.put("plotLines", plotlines);
        xAxis.put("categories", listeAnnee);
        //création d'une légende
        JsonObject legend = new JsonObject();
        legend.put("layout", "vertical");
        legend.put("align", "right");
        legend.put("verticalAlign", "middle");
        legend.put("boderWidth", new Integer(0));
        //Ajout d'une série
        JsonObject pays = new JsonObject();
        JsonArray data = new JsonArray();
        for(Entry<Integer, String> entry : mapOrd.entrySet()){
       	 data.add(Integer.parseInt(entry.getValue()));
         }
        pays.put("name", pays1);
        pays.put("data", data);
        JsonArray series = new JsonArray();
        series.add(pays);
     
        JsonObject graphe = new JsonObject();
        graphe.put("series", series);
        graphe.put("legend", legend);
        graphe.put("yAxis", yAxis);
    	graphe.put("xAxis", xAxis);
        graphe.put("title",title);
        graphe.put("subtitle", subtitle);
        Graphe g = new Graphe(graphe.toString(),0.0,0.0,0.0,0.0,null,null,null,null);		
       return ok(viz2.render(g));
        	
    }

}
