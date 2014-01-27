package models;

import play.db.ebean.Model;

public class Comment extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String nom;
	public String date;
	public String contenu;
	
	public Comment(String nom,String date,String contenu){
		this.nom=nom;
		this.date=date;
		this.contenu=contenu;
	}

}
