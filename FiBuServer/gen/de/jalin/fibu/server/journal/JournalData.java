// Generiert mit xmlrpcgen

package de.jalin.fibu.server.journal;

import net.hostsharing.admin.runtime.GenericData;

public class JournalData extends GenericData {

	private Integer jourid;
	private String journr;
	private String jahr;
	private String periode;
	private java.util.Date since;
	private java.util.Date lastupdate;
	private Boolean absummiert;

	public JournalData() {
		jourid = null;
		journr = null;
		jahr = null;
		periode = null;
		since = null;
		lastupdate = null;
		absummiert = null;
	}

	public Integer getJourid() {
		return jourid;
	}
	
	public void setJourid(Integer jourid) {
		this.jourid = jourid;
	}
	
	public String getJournr() {
		return journr;
	}
	
	public void setJournr(String journr) {
		this.journr = journr;
	}
	
	public String getJahr() {
		return jahr;
	}
	
	public void setJahr(String jahr) {
		this.jahr = jahr;
	}
	
	public String getPeriode() {
		return periode;
	}
	
	public void setPeriode(String periode) {
		this.periode = periode;
	}
	
	public java.util.Date getSince() {
		return since;
	}
	
	public void setSince(java.util.Date since) {
		this.since = since;
	}
	
	public java.util.Date getLastupdate() {
		return lastupdate;
	}
	
	public void setLastupdate(java.util.Date lastupdate) {
		this.lastupdate = lastupdate;
	}
	
	public Boolean getAbsummiert() {
		return absummiert;
	}
	
	public void setAbsummiert(Boolean absummiert) {
		this.absummiert = absummiert;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "jourid"
				,  "journr"
				,  "jahr"
				,  "periode"
				,  "since"
				,  "lastupdate"
				,  "absummiert"
			};
	}
	
}
