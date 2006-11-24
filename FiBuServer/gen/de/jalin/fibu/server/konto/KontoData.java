// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import net.hostsharing.admin.runtime.GenericData;

public class KontoData extends GenericData {

	private Integer kontoid;
	private String kontonr;
	private String bezeichnung;
	private Integer mwstid;
	private Integer oberkonto;
	private Boolean istsoll;
	private Boolean isthaben;
	private Boolean istaktiv;
	private Boolean istpassiv;
	private Boolean istaufwand;
	private Boolean istertrag;

	public KontoData() {
		kontoid = null;
		kontonr = null;
		bezeichnung = null;
		mwstid = null;
		oberkonto = null;
		istsoll = null;
		isthaben = null;
		istaktiv = null;
		istpassiv = null;
		istaufwand = null;
		istertrag = null;
	}

	public Integer getKontoid() {
		return kontoid;
	}
	
	public void setKontoid(Integer kontoid) {
		this.kontoid = kontoid;
	}
	
	public String getKontonr() {
		return kontonr;
	}
	
	public void setKontonr(String kontonr) {
		this.kontonr = kontonr;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}
	
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public Integer getMwstid() {
		return mwstid;
	}
	
	public void setMwstid(Integer mwstid) {
		this.mwstid = mwstid;
	}
	
	public Integer getOberkonto() {
		return oberkonto;
	}
	
	public void setOberkonto(Integer oberkonto) {
		this.oberkonto = oberkonto;
	}
	
	public Boolean getIstsoll() {
		return istsoll;
	}
	
	public void setIstsoll(Boolean istsoll) {
		this.istsoll = istsoll;
	}
	
	public Boolean getIsthaben() {
		return isthaben;
	}
	
	public void setIsthaben(Boolean isthaben) {
		this.isthaben = isthaben;
	}
	
	public Boolean getIstaktiv() {
		return istaktiv;
	}
	
	public void setIstaktiv(Boolean istaktiv) {
		this.istaktiv = istaktiv;
	}
	
	public Boolean getIstpassiv() {
		return istpassiv;
	}
	
	public void setIstpassiv(Boolean istpassiv) {
		this.istpassiv = istpassiv;
	}
	
	public Boolean getIstaufwand() {
		return istaufwand;
	}
	
	public void setIstaufwand(Boolean istaufwand) {
		this.istaufwand = istaufwand;
	}
	
	public Boolean getIstertrag() {
		return istertrag;
	}
	
	public void setIstertrag(Boolean istertrag) {
		this.istertrag = istertrag;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "kontoid"
				,  "kontonr"
				,  "bezeichnung"
				,  "mwstid"
				,  "oberkonto"
				,  "istsoll"
				,  "isthaben"
				,  "istaktiv"
				,  "istpassiv"
				,  "istaufwand"
				,  "istertrag"
			};
	}
	
}
