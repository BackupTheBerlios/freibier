/* Erzeugt am 25.03.2005 von tbayen
 * $Id: HBCICallbackConstant.java,v 1.1 2006/01/24 00:26:00 tbayen Exp $
 */
package de.bayen.banking.hbci;

import org.kapott.hbci.callback.HBCICallback;
import de.bayen.util.Constant;

/**
 * Diese Klasse stellt Konstanten-Objekte zur Verfügung, die verschiedene
 * HBCI-Callback-Gründe darstellen. Sie "wrappt" die int-Typen, die 
 * HBCICallback dafür benutzt.
 * 
 * @author tbayen
 */
public class HBCICallbackConstant extends Constant{
	protected HBCICallbackConstant(int value, String description) {
		super(value, description);}
	static staticMaps maps=new staticMaps();
	protected staticMaps getMaps(){return maps;}
	public static Constant fromInt(int val){
		return (Constant)(maps.valMap.get(new Integer(val)));}
	public static Constant fromString(String desc){
		return (Constant)maps.descMap.get(desc);}

	public static HBCICallbackConstant NEED_CHIPCARD=new HBCICallbackConstant(
			HBCICallback.NEED_CHIPCARD,"NEED_CHIPCARD");
	public static HBCICallbackConstant NEED_HARDPIN=new HBCICallbackConstant(
			HBCICallback.NEED_HARDPIN,"NEED_HARDPIN");
	public static HBCICallbackConstant NEED_SOFTPIN=new HBCICallbackConstant(
			HBCICallback.NEED_SOFTPIN,"NEED_SOFTPIN");
	public static HBCICallbackConstant HAVE_HARDPIN=new HBCICallbackConstant(
			HBCICallback.HAVE_HARDPIN,"HAVE_HARDPIN");
	public static HBCICallbackConstant HAVE_CHIPCARD=new HBCICallbackConstant(
			HBCICallback.HAVE_CHIPCARD,"HAVE_CHIPCARD");
	public static HBCICallbackConstant NEED_BLZ=new HBCICallbackConstant(
			HBCICallback.NEED_BLZ,"NEED_BLZ");
	public static HBCICallbackConstant NEED_HOST=new HBCICallbackConstant(
			HBCICallback.NEED_HOST,"NEED_HOST");
	public static HBCICallbackConstant NEED_PORT=new HBCICallbackConstant(
			HBCICallback.NEED_PORT,"NEED_PORT");
	public static HBCICallbackConstant NEED_USERID=new HBCICallbackConstant(
			HBCICallback.NEED_USERID,"NEED_USERID");
	public static HBCICallbackConstant NEED_NEW_INST_KEYS_ACK=new HBCICallbackConstant(
			HBCICallback.NEED_NEW_INST_KEYS_ACK,"NEED_NEW_INST_KEYS_ACK");
	public static HBCICallbackConstant HAVE_NEW_MY_KEYS=new HBCICallbackConstant(
			HBCICallback.HAVE_NEW_MY_KEYS,"HAVE_NEW_MY_KEYS");
	public static HBCICallbackConstant HAVE_INST_MSG=new HBCICallbackConstant(
			HBCICallback.HAVE_INST_MSG,"HAVE_INST_MSG");
	public static HBCICallbackConstant NEED_REMOVE_CHIPCARD=new HBCICallbackConstant(
			HBCICallback.NEED_REMOVE_CHIPCARD,"NEED_REMOVE_CHIPCARD");
	public static HBCICallbackConstant NEED_PT_PIN=new HBCICallbackConstant(
			HBCICallback.NEED_PT_PIN,"NEED_PT_PIN");
	public static HBCICallbackConstant NEED_PT_TAN=new HBCICallbackConstant(
			HBCICallback.NEED_PT_TAN,"NEED_PT_TAN");
	public static HBCICallbackConstant NEED_CUSTOMERID=new HBCICallbackConstant(
			HBCICallback.NEED_CUSTOMERID,"NEED_CUSTOMERID");
	public static HBCICallbackConstant HAVE_CRC_ERROR=new HBCICallbackConstant(
			HBCICallback.HAVE_CRC_ERROR,"HAVE_CRC_ERROR");
	public static HBCICallbackConstant HAVE_ERROR=new HBCICallbackConstant(
			HBCICallback.HAVE_ERROR,"HAVE_ERROR");
	public static HBCICallbackConstant NEED_PASSPHRASE_LOAD=new HBCICallbackConstant(
			HBCICallback.NEED_PASSPHRASE_LOAD,"NEED_PASSPHRASE_LOAD");
	public static HBCICallbackConstant NEED_PASSPHRASE_SAVE=new HBCICallbackConstant(
			HBCICallback.NEED_PASSPHRASE_SAVE,"NEED_PASSPHRASE_SAVE");
	public static HBCICallbackConstant NEED_SIZENTRY_SELECT=new HBCICallbackConstant(
			HBCICallback.NEED_SIZENTRY_SELECT,"NEED_SIZENTRY_SELECT");
	public static HBCICallbackConstant NEED_CONNECTION=new HBCICallbackConstant(
			HBCICallback.NEED_CONNECTION,"NEED_CONNECTION");
	public static HBCICallbackConstant CLOSE_CONNECTION=new HBCICallbackConstant(
			HBCICallback.CLOSE_CONNECTION,"CLOSE_CONNECTION");
	public static HBCICallbackConstant NEED_FILTER=new HBCICallbackConstant(
			HBCICallback.NEED_FILTER,"NEED_FILTER");
}

/*
 * $Log: HBCICallbackConstant.java,v $
 * Revision 1.1  2006/01/24 00:26:00  tbayen
 * Erste eigenständige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/26 03:10:44  tbayen
 * Banking-Applikation kann per Chipkarte
 * Auszüge abholen und anzeigen
 *
 */