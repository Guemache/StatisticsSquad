package controllers;

import models.Graphe;

import org.apache.jena.atlas.json.JsonObject;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.formComp;


public class ControleFormComp extends Controller {
	
public static Result formComp(){
		String salut = "";
    	return ok(formComp.render(salut));
	}

}
