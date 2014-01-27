package controllers;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.atlas.json.JsonObject;

import models.Comment;
import models.Graphe;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.comp1;
import views.html.comp2;
import views.html.comp3;

public class ControleComment extends Controller {
	private static final GraphCreation gc = new GraphCreation("crim_gen");
	private static ArrayList<String> listeComments ;
	
	public static Result comment(){
		
		String comp = Form.form().bindFromRequest().get("comp");
		String nom = Form.form().bindFromRequest().get("nom");
		String commentaire = Form.form().bindFromRequest().get("commentaire");
		String graphe = Form.form().bindFromRequest().get("graphe");
		String ipAdress = Form.form().bindFromRequest().get("ipAdress");
		String uri = Form.form().bindFromRequest().get("uri");
		String covariance = Form.form().bindFromRequest().get("covariance");
		String correlation = Form.form().bindFromRequest().get("correlation");
		String moyenne = Form.form().bindFromRequest().get("moyenne");
		String ecartType = Form.form().bindFromRequest().get("covariance");
		JsonObject graph1 = new JsonObject();
		String ip = request().remoteAddress();
		
		/**
		 * création des commentaires
		 */
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		gc.postComment(uri, nom, commentaire,false);
		gc.save();
		
		listeComments = gc.getComments(uri, false);
		System.out.println("%%%%%%"+listeComments.size());
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
		
		System.out.println(listeCom.size());
		
		
		
		Graphe g = new Graphe(graphe,Double.parseDouble(covariance),Double.parseDouble(correlation),Double.parseDouble(moyenne),
				              Double.parseDouble(ecartType),listeCom,ipAdress,null,null);
		//Redirection automatique
		
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		
		map.put("comp1",1);
		map.put("comp2", 2);
		map.put("comp3", 3);
		int c = map.get(comp);
	
		
	    if(c==1){
		return ok(comp1.render(g)); 
	    }else{
	    	 if(c==2){
	    		 return ok(comp2.render(g)); 
	    	 }else{
	    		 return ok(comp3.render(g)); 
	    	 }
		
	    }
	
		
	
		
	    	
		
		
		
	}

}
