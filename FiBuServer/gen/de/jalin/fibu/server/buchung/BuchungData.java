// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchung;

import net.hostsharing.admin.runtime.GenericData;

public class BuchungData extends GenericData {

	private Integer buchid;
	private String belegnr;
	private String buchungstext;
	private Integer jourid;
	private java.util.Date valuta;
	private java.util.Date erfassung;

	public BuchungData() {
		buchid = null;
		belegnr = null;
		buchungstext = null;
		jourid = null;
		valuta = null;
		erfassung = null;
	}

	public Integer getBuchid() {
		return buchid;
	}
	
	public void setBuchid(Integer buchid) {
		this.buchid = buchid;
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
	
	public java.util.Date getErfassung() {
		return erfassung;
	}
	
	public void setErfassung(java.util.Date erfassung) {
		this.erfassung = erfassung;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "buchid"
				,  "belegnr"
				,  "buchungstext"
				,  "jourid"
				,  "valuta"
				,  "erfassung"
			};
	}
	
}
