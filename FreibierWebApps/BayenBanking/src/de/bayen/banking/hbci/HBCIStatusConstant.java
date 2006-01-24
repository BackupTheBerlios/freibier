/* Erzeugt am 25.03.2005 von tbayen
 * $Id: HBCIStatusConstant.java,v 1.1 2006/01/24 00:26:00 tbayen Exp $
 */
package de.bayen.banking.hbci;

import org.kapott.hbci.callback.HBCICallback;
import de.bayen.util.Constant;

/**
 * Konstanten, die in Callbacks benutzt werden, um den Status des
 * HBCI-Kernels auszudrücken.
 * 
 * @author tbayen
 */
public class HBCIStatusConstant extends Constant {
	protected HBCIStatusConstant(int value, String description) {
		super(value, description);}
	static staticMaps maps=new staticMaps();
	protected staticMaps getMaps(){return maps;}
	public static Constant fromInt(int val){
		return (Constant)(maps.valMap.get(new Integer(val)));}
	public static Constant fromString(String desc){
		return (Constant)maps.descMap.get(desc);}

	public static HBCIStatusConstant STATUS_SEND_TASK=new HBCIStatusConstant(
			HBCICallback.STATUS_SEND_TASK,"STATUS_SEND_TASK");
	public static HBCIStatusConstant STATUS_SEND_TASK_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_SEND_TASK_DONE,"STATUS_SEND_TASK_DONE");
	public static HBCIStatusConstant STATUS_INST_BPD_INIT=new HBCIStatusConstant(
			HBCICallback.STATUS_INST_BPD_INIT,"STATUS_INST_BPD_INIT");
	public static HBCIStatusConstant STATUS_INST_BPD_INIT_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_INST_BPD_INIT_DONE,"STATUS_INST_BPD_INIT_DONE");
	public static HBCIStatusConstant STATUS_INST_GET_KEYS=new HBCIStatusConstant(
			HBCICallback.STATUS_INST_GET_KEYS,"STATUS_INST_GET_KEYS");
	public static HBCIStatusConstant STATUS_INST_GET_KEYS_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_INST_GET_KEYS_DONE,"STATUS_INST_GET_KEYS_DONE");
	public static HBCIStatusConstant STATUS_SEND_KEYS=new HBCIStatusConstant(
			HBCICallback.STATUS_SEND_KEYS,"STATUS_SEND_KEYS");
	public static HBCIStatusConstant STATUS_SEND_KEYS_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_SEND_KEYS_DONE,"STATUS_SEND_KEYS_DONE");
	public static HBCIStatusConstant STATUS_INIT_SYSID=new HBCIStatusConstant(
			HBCICallback.STATUS_INIT_SYSID,"STATUS_INIT_SYSID");
	public static HBCIStatusConstant STATUS_INIT_SYSID_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_INIT_SYSID_DONE,"STATUS_INIT_SYSID_DONE");
	public static HBCIStatusConstant STATUS_INIT_UPD=new HBCIStatusConstant(
			HBCICallback.STATUS_INIT_UPD,"STATUS_INIT_UPD");
	public static HBCIStatusConstant STATUS_INIT_UPD_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_INIT_UPD_DONE,"STATUS_INIT_UPD_DONE");
	public static HBCIStatusConstant STATUS_LOCK_KEYS=new HBCIStatusConstant(
			HBCICallback.STATUS_LOCK_KEYS,"STATUS_LOCK_KEYS");
	public static HBCIStatusConstant STATUS_LOCK_KEYS_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_LOCK_KEYS_DONE,"STATUS_LOCK_KEYS_DONE");
	public static HBCIStatusConstant STATUS_INIT_SIGID=new HBCIStatusConstant(
			HBCICallback.STATUS_INIT_SIGID,"STATUS_INIT_SIGID");
	public static HBCIStatusConstant STATUS_INIT_SIGID_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_INIT_SIGID_DONE,"STATUS_INIT_SIGID_DONE");
	public static HBCIStatusConstant STATUS_DIALOG_INIT=new HBCIStatusConstant(
			HBCICallback.STATUS_DIALOG_INIT,"STATUS_DIALOG_INIT");
	public static HBCIStatusConstant STATUS_DIALOG_INIT_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_DIALOG_INIT_DONE,"STATUS_DIALOG_INIT_DONE");
	public static HBCIStatusConstant STATUS_DIALOG_END=new HBCIStatusConstant(
			HBCICallback.STATUS_DIALOG_END,"STATUS_DIALOG_END");
	public static HBCIStatusConstant STATUS_DIALOG_END_DONE=new HBCIStatusConstant(
			HBCICallback.STATUS_DIALOG_END_DONE,"STATUS_DIALOG_END_DONE");
	public static HBCIStatusConstant STATUS_MSG_CREATE=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_CREATE,"STATUS_MSG_CREATE");
	public static HBCIStatusConstant STATUS_MSG_SIGN=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_SIGN,"STATUS_MSG_SIGN");
	public static HBCIStatusConstant STATUS_MSG_CRYPT=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_CRYPT,"STATUS_MSG_CRYPT");
	public static HBCIStatusConstant STATUS_MSG_SEND=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_SEND,"STATUS_MSG_SEND");
	public static HBCIStatusConstant STATUS_MSG_DECRYPT=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_DECRYPT,"STATUS_MSG_DECRYPT");
	public static HBCIStatusConstant STATUS_MSG_VERIFY=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_VERIFY,"STATUS_MSG_VERIFY");
	public static HBCIStatusConstant STATUS_MSG_RECV=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_RECV,"STATUS_MSG_RECV");
	public static HBCIStatusConstant STATUS_MSG_PARSE=new HBCIStatusConstant(
			HBCICallback.STATUS_MSG_PARSE,"STATUS_MSG_PARSE");
	
}

/*
 * $Log: HBCIStatusConstant.java,v $
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