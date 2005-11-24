package de.jalin.fibu.server.buchungsmaschine;

import net.hostsharing.admin.runtime.GenericData;

public class BuchungsmaschineData extends GenericData {

	private Integer buzlid;
	private String sollkontonr;
	private String habenkontonr;
	private Integer sollmwstid;
	private Integer habenmwstid;
	private Integer brutto;
	private String belegnr;
	private String buchungstext;
	private Integer jourid;
	private java.util.Date valuta;

	public BuchungsmaschineData() {
		buzlid = null;
		sollkontonr = null;
		habenkontonr = null;
		sollmwstid = null;
		habenmwstid = null;
		brutto = null;
		belegnr = null;
		buchungstext = null;
		jourid = null;
		valuta = null;
	}

	public Integer getBuzlid() {
		return buzlid;
	}
	
	public void setBuzlid(Integer buzlid) {
		this.buzlid = buzlid;
	}
	
	public String getSollkontonr() {
		return sollkontonr;
	}
	
	public void setSollkontonr(String sollkontonr) {
		this.sollkontonr = sollkontonr;
	}
	
	public String getHabenkontonr() {
		return habenkontonr;
	}
	
	public void setHabenkontonr(String habenkontonr) {
		this.habenkontonr = habenkontonr;
	}
	
	public Integer getSollmwstid() {
		return sollmwstid;
	}
	
	public void setSollmwstid(Integer sollmwstid) {
		this.sollmwstid = sollmwstid;
	}
	
	public Integer getHabenmwstid() {
		return habenmwstid;
	}
	
	public void setHabenmwstid(Integer habenmwstid) {
		this.habenmwstid = habenmwstid;
	}
	
	public Integer getBrutto() {
		return brutto;
	}
	
	public void setBrutto(Integer brutto) {
		this.brutto = brutto;
	}
	
	public String getBelegnr() {
		return belegnr;
	}
	
	public void setBelegnr(String belegnr) {
		this.belegnr = belegnr;
	}
	
	public String getBuchungstext() {
		return buchungstext;
	}
	
	public void setBuchungstext(String buchungstext) {
		this.buchungstext = buchungstext;
	}
	
	public Integer getJourid() {
		return jourid;
	}
	
	public void setJourid(Integer jourid) {
		this.jourid = jourid;
	}
	
	public java.util.Date getValuta() {
		return valuta;
	}
	
	public void setValuta(java.util.Date valuta) {
		this.valuta = valuta;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "buzlid"
				,  "sollkontonr"
				,  "habenkontonr"
				,  "sollmwstid"
				,  "habenmwstid"
				,  "brutto"
				,  "belegnr"
				,  "buchungstext"
				,  "jourid"
				,  "valuta"
			};
	}
	
}
