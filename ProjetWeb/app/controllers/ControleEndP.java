package controllers;



import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class ControleEndP extends Controller {
	
   //Traitement de requêtes
	
    public static Result endpoint(){
    	
		return ok(endpoint.render(""));
        	
    }


}
