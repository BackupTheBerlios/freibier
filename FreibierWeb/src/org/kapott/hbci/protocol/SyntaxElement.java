
/*  $Id: SyntaxElement.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2004  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.protocol;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.NoSuchPathException;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.protocol.factory.MultipleDEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleDEsFactory;
import org.kapott.hbci.protocol.factory.MultipleSEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleSFsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* ein syntaxelement ist ein strukturelement einer hbci-nachricht (die nachricht
    selbst, eine segmentfolge, ein einzelnes segment, eine deg oder 
    ein einzelnes de) 

    @bug die xml-angaben fuer validvalues gelten bei mehrmaligem auftreten eines
    syntaxelements natuerlich fuer *alle* auftreten dieses de und nicht nur fuer
    eines: 
    <validvalues path="path.to.element">..</>
    <DE name="path.to.element" maxnum="3"/>
    die valids werden nur fuer path.to.element gescheckt, nicht aber fuer
    path.to.element_2 und path.to.element_3
*/
public abstract class SyntaxElement
{
    private List childContainers;  /**< @internal @brief alle in diesem element enthaltenen unterelemente */
    private String name;   /**< @internal @brief bezeichner fuer dieses element */
    private String type;
    private String path;   /**< @internal @brief pfadname dieses elementes innerhalb einer MSG */
    private char predelim;  /**< @internal @brief nur beim parsen: zeichen, das vor diesem element stehen muesste */
    private boolean valid; /**< @internal @brief indicates if this element is really valid, i.e. will appear in 
                                an outgoing hbci message resp. in returned results from an incoming message*/
    private MultipleSyntaxElements parent;
    private int posInMsg;
    
    public final static boolean TRY_TO_CREATE=true;
    public final static boolean DONT_TRY_TO_CREATE=false;
    public final static boolean ALLOW_OVERWRITE=true;
    public final static boolean DONT_ALLOW_OVERWRITE=false;

    /** wird fuer datenelemente benoetigt, die sonst unbeabsichtigt generiert werden koennten.
        das problem ist, dass es datenelemente (bisher nur bei segmenten bekannt) gibt,
        die aus einigen "required" unterelementen bestehen und aus einigen optionalen
        unterelementen. wenn *alle* "required" elemente bereits durch predefined values
        bzw. durch automatisch generierte werte vorgegeben sind, dann wird das entsprechende
        element erzeugt, da es auch ohne angabe der optionalen unterelemente gueltig ist.
        
        es ist aber u.U. gar nicht beabsichtigt, dass dieses element erzeugt wird (beispiel
        segment "KIOffer", wo nur die DEG SegHead required ist, alle anderen elemente sind
        optional). es kann also vorkommen, dass ein element *unbeabsichtigt* nur aus den
        vorgabedaten erzeugt wird.

        bei den elementen, bei denen das passieren kann, wird in der xml-spezifikation
        deshalb zusaetzlich das attribut "needsRequestTag" angegeben. der wert dieses
        attributes wird hier in der variablen @p needsRequestTag gespeichert.

        beim ueberpruefen, ob das aktuelle element gueltig ist (mittels @c validate() ),
        wird neben der gueltigkeit aller unterelemente zusaetzlich ueberprueft, ob dieses
        element ein request-tag benoetigt, und wenn ja, ob es vorhanden ist. wenn die
        @p needsRequestTag -bedingung nicht erfuellt ist, ist auch das element ungueltig,
        und es wird nicht erzeugt.

        das vorhandensein eines request-tags wird in der variablen @haveRequestTag
        gespeichert. dieses flag kann fuer ein bestimmtes element gesetzt werden, indem
        ihm der wert "requested" zugewiesen wird. normalerweise kann nur DE-elementen
        ein wert zugewiesen werden, diese benoetigen aber kein request-tag. wird also einem
        gruppierenden element der wert "requested" zugewiesen, dann wird das durch die
        methode @c propagateValue() als explizites setzen des @p haveRequestTag
        interpretiert.

        alle klassen und methoden, die also daten fuer die erzeugung von nachrichten
        generieren, muessen u.U. fuer bestimmte syntaxelemente diesen "requested"-wert
        setzen. 

        needsRequestTag kann komplett weg, oder? -- nein. Für GV-Segmente
        gilt das schon. Die Überprüfung des requested-Werted findet aber
        in der *allgemeinen* SyntaxElement-Klasse statt, wo auch andere
        Segmente (z.b. MsgHead) erzeugt werden. Wenn als "allgemeiner"
        Check der Check "if SEG.isRequested" eingeführt werden würde, dann
        würde der nur bei tatsächlich gewünschten GV-Segmenten true ergeben.
        Bei MsgHead-Segmenten z.B. würde er false ergeben (weil diese
        Segmente niemals auf "requested" gesetzt werden). Deshalb darf diese
        "requested"-Überprüfung nur bei den Syntaxelementen stattfinden,
        bei denen das explizit gewünscht ist (needsRequestTag). */
    private boolean needsRequestTag;
    private boolean haveRequestTag;
    
    private void initData(String type, String name, String path, int idx, Document syntax)
    {
        this.type = type;
        this.name = name;
        this.parent=null;
        this.needsRequestTag=false;
        this.haveRequestTag=false;
        this.childContainers = new ArrayList();
        this.predelim=0;
        
        /* der pfad wird gebildet aus bisherigem pfad
         plus name des elementes
         plus indexnummer, falls diese groesser 0 ist */
        StringBuffer temppath=new StringBuffer(128);
        if (path!=null && path.length()!=0)
            temppath.append(path).append(".");
        temppath.append(HBCIUtilsInternal.withCounter(name,idx));
        this.path=temppath.toString();

        setValid(false);

        if (syntax != null) {
            Node def=getSyntaxDef(type,syntax);
            // erzeugen der child-elemente

            String requestTag=((Element)def).getAttribute("needsRequestTag");
            if (requestTag!=null && requestTag.equals("1"))
                needsRequestTag=true;

            try {
                for (Node ref=def.getFirstChild(); ref!=null; ref=ref.getNextSibling()) {
                    if (ref.getNodeType()==Node.ELEMENT_NODE) {
                        MultipleSyntaxElements child=createNewChildContainer(ref, syntax);
                        if (child!=null)
                            child.setParent(this);
                    }
                }

                /* durchlaufen aller "value"-knoten und setzen der
                 werte der entsprechenden de */
                NodeList valueNodes = ((Element)def).getElementsByTagName("value");
                int len=valueNodes.getLength();
                for (int i=0; i<len; i++) {
                    Node valueNode = valueNodes.item(i);
                    String valuePath = ((Element)valueNode).getAttribute("path");
                    String value = (valueNode.getFirstChild()).getNodeValue();
                    String destpath=this.path+"."+valuePath;
                    
                    if (!propagateValue(destpath,value,TRY_TO_CREATE,DONT_ALLOW_OVERWRITE))
                        throw new NoSuchPathException(destpath);
                }

                /* durchlaufen aller "valids"-knoten und speichern der valid-values */
                NodeList validNodes=((Element)def).getElementsByTagName("valids");
                len=validNodes.getLength();
                for (int i=0;i<len;i++) {
                    Node validNode=validNodes.item(i);
                    String valuePath=((Element)(validNode)).getAttribute("path");
                    String absPath=getPath()+"."+valuePath;

                    NodeList validvalueNodes=((Element)(validNode)).getElementsByTagName("validvalue");
                    int len2=validvalueNodes.getLength();
                    for (int j=0;j<len2;j++) {
                        Node validvalue=validvalueNodes.item(j);
                        String value=(validvalue.getFirstChild()).getNodeValue();

                        storeValidValueInDE(absPath,value);
                    }
                }
            } catch (RuntimeException e) {
                for (Iterator i=getChildContainers().iterator();i.hasNext();) {
                    Object o=i.next();
                    if (o instanceof MultipleSFs) {
                        MultipleSFsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleSEGs) {
                        MultipleSEGsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleDEGs) {
                        MultipleDEGsFactory.getInstance().unuseObject(o);
                    } else {
                        MultipleDEsFactory.getInstance().unuseObject(o);
                    }
                }
                throw e;
            }
        }
    }

    /** es wird ein syntaxelement mit der id 'name' initialisiert; der pfad bis zu
        diesem element wird in 'path' uebergeben; 'idx' ist die nummer dieses
        elementes innerhalb der syntaxelementliste fuer dieses element (falls ein
        bestimmtes syntaxelement mehr als einmal auftreten kann) */
    protected SyntaxElement(String type, String name, String path, int idx, Document syntax)
    {
        initData(type,name,path,idx,syntax);
    }

    protected void init(String type, String name, String path, int idx, Document syntax)
    {
        initData(type,name,path,idx,syntax);
    }

    protected boolean storeValidValueInDE(String path,String value)
    {
        boolean ret=false;

        for (Iterator i=childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());
            if (l.storeValidValueInDE(path, value))
                ret=true;
        }

        return ret;
    }

    public void setParent(MultipleSyntaxElements parent)
    {
        this.parent=parent;
    }

    public MultipleSyntaxElements getParent()
    {
        return parent;
    }
    
    public int getPosInMsg()
    {
        return posInMsg;
    }

    /** loop through all child-elements; the segments found there
        will be sequentially enumerated starting with num startValue;
        if startValue is zero, the segments will not be enumerated,
        but all given the number 0

        @param startValue value to be used for the first segment found
        @return next sequence number usable for enumeration */
    public int enumerateSegs(int startValue,boolean allowOverwrite)
    {
        int idx = startValue;

        for (Iterator i = getChildContainers().iterator(); i.hasNext(); ) {
            MultipleSyntaxElements s = (MultipleSyntaxElements)(i.next());
            if (s != null)
                idx = s.enumerateSegs(idx,allowOverwrite);
        }

        return idx;
    }

    // -------------------------------------------------------------------------------------------
    
    private void initData(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable predefs,Hashtable valids)
    {
        this.type=type;
        this.name=name;
        this.parent=null;
        this.childContainers = new ArrayList();
        this.predelim = predelim;
        this.needsRequestTag=false;
        this.haveRequestTag=false;
        /* position des aktuellen datenelementes berechnet sich aus der
         * gesamtlänge des ursprünglichen msg-strings minus der länge des
         * reststrings, der jetzt zu parsen ist, und der mit dem aktuellen
         * datenelement beginnt */
        this.posInMsg=fullResLen-res.length(); 

        StringBuffer temppath=new StringBuffer(128);
        if (path!=null && path.length()!=0)
            temppath.append(path).append(".");
        temppath.append(HBCIUtilsInternal.withCounter(name,idx));
        this.path=temppath.toString();

        setValid(false);

        if (syntax != null) {
            Node def=getSyntaxDef(type,syntax);
            
            /* fuellen der 'predefs'-tabelle mit den in der
             syntaxbeschreibung vorgegebenen werten */
            NodeList valueNodes = ((Element)def).getElementsByTagName("value");
            int len=valueNodes.getLength();
            for (int i = 0; i < len; i++) {
                Node valueNode = valueNodes.item(i);
                String valuePath = ((Element)valueNode).getAttribute("path");
                String value = (valueNode.getFirstChild()).getNodeValue();

                predefs.put(getPath() + "." + valuePath, value);
            }

            if (valids!=null) {
                /* durchlaufen aller "valids"-knoten und speichern der valid-values */
                NodeList validNodes=((Element)def).getElementsByTagName("valids");
                len=validNodes.getLength();
                for (int i=0;i<len;i++) {
                    Node validNode=validNodes.item(i);
                    String valuePath=((Element)(validNode)).getAttribute("path");
                    String absPath=getPath()+"."+valuePath;
                    
                    NodeList validvalueNodes=((Element)(validNode)).getElementsByTagName("validvalue");
                    int len2=validvalueNodes.getLength();
                    for (int j=0;j<len2;j++) {
                        Node validvalue=validvalueNodes.item(j);
                        String value=(validvalue.getFirstChild()).getNodeValue();
                        valids.put(HBCIUtilsInternal.withCounter(absPath+".value",j),value);
                    }
                }
            }

            try {
                // anlegen der child-elemente
                int counter=0;
                for (Node ref=def.getFirstChild();ref!=null;ref=ref.getNextSibling()) {
                    if (ref.getNodeType()==Node.ELEMENT_NODE) {
                        MultipleSyntaxElements child=parseNewChildContainer(ref,
                                ((counter++)==0)?predelim:getInDelim(),
                                getInDelim(),
                                res,fullResLen,syntax,predefs,valids);
                        if (child!=null) {
                            child.setParent(this);
                            
                            // *** this is a very very dirty hack to fix the problem with the params-template;
                            // bei der SF "Params", die mit <SF type="Params" maxnum="0"/> referenziert wird, 
                            // soll nach jedem erfolgreich in die SF aufgenommenen Param-Segment eine neue
                            // SF begonnen werden, damit das Problem mit dem am Ende der SF stehenden Template-
                            // Param-Segment nicht mehr auftritt
                            // dazu wird beim hinzufuegen von segmenten zur sf ueberprueft, ob diese evtl. bereits
                            // segmente enthaelt (haveFilledChildContainers()). falls das der fall ist, so wird
                            // kein neues segment hinzugefuegt
                            if ((this instanceof SF) && 
                                    getName().equals("Params") &&
                                    ((MultipleSEGs)child).hasValidChilds()) {
                                break;
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                for (Iterator i=getChildContainers().iterator();i.hasNext();) {
                    Object o=i.next();
                    if (o instanceof MultipleSFs) {
                        MultipleSFsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleSEGs) {
                        MultipleSEGsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleDEGs) {
                        MultipleDEGsFactory.getInstance().unuseObject(o);
                    } else {
                        MultipleDEsFactory.getInstance().unuseObject(o);
                    }
                }
                throw e;
            }
        }

        // if there was no error until here, this syntaxelement is valid
        setValid(true);
    }

    /** beim parsen: initialisiert ein neues syntaxelement mit der id 'name'; in
        'path' wird der pfad bis zu dieser stelle uebergeben 'predelim' gibt das
        delimiter-zeichen an, das beim parsen vor diesem syntax- element stehen
        muesste 'idx' ist die nummer des syntaxelementes innerhalb der
        uebergeordneten liste (die liste repraesentiert das evtl. mehrmalige
        auftreten eines syntaxelementes, siehe class syntaxelementlist) 'res' ist
        der zu parsende String 'predefs' soll eine menge von pfad-wert-paaren
        enthalten, die fuer einige syntaxelemente den wert angeben, den diese
        elemente zwingend haben muessen (z.b. ein bestimmter segmentcode o.ae.) */
    protected SyntaxElement(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable predefs,Hashtable valids)
    {
        initData(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }
    
    protected void init(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable predefs,Hashtable valids)
    {
        initData(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    /** @return the ArrayList containing all child-elements (the elements
        of the ArrayList are instances of the SyntaxElementArray class */
    public List getChildContainers()
    {
        return childContainers;
    }

    /** @return den wert eines bestimmten DE; 
        funktioniert analog zu 'propagateValue' */
    public String getValueOfDE(String path)
    {
        String ret = null;

        for (Iterator i = childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());

            String temp = l.getValueOfDE(path);
            if (temp != null)
                ret = temp;
        }

        return ret;
    }

    public String getValueOfDE(String path, int zero)
    {
        String ret = null;

        for (Iterator i = childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());

            String temp = l.getValueOfDE(path, 0);
            if (temp != null)
                ret = temp;
        }

        return ret;
    }

    /** @param path path to the element to be returned
        @return the element identified by path */
    public SyntaxElement getElement(String path)
    {        
        SyntaxElement ret = null;

        if (getPath().equals(path)) {
            ret = this;
        } else {
            for (Iterator i = childContainers.listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());

                SyntaxElement temp = l.getElement(path);
                if (temp != null)
                    ret = temp;
            }
        }
        
        return ret;
    }

    /** @return the path to this element */
    public final String getPath()
    {
        return path;
    }

    /** @return the name of this element (i.e. the last component of path) */
    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    /** @return the delimiter that must be in front of this element */
    protected char getPreDelim()
    {
        return predelim;
    }
    
    /** @param type the name of the syntaxelement to be returned
        @param syntax the structure containing the current syntaxdefinition
        @return a XML-node with the definition of the requested syntaxelement */
    public final Node getSyntaxDef(String type, Document syntax)
    {
        Node ret = syntax.getElementById(type);
        if (ret == null)
            throw new org.kapott.hbci.exceptions.NoSuchElementException(getElementTypeName(), type);
        return ret;
    }

    /** setzt den wert eines de; in allen syntaxelementen ausser DE wird dazu die
        liste der child-elemente durchlaufen; jedem dieser child-elemente wird der
        wert zum setzen uebergeben; genau _eines_ dieser elemente wird sich dafuer
        zustaendig fuehlen (das DE mit 'path'='destPath') und den wert uebernehmen */
    public boolean propagateValue(String destPath, String value, boolean tryToCreate,boolean allowOverwrite)
    {
        boolean ret = false;

        if (destPath.equals(getPath())) {
            if (value!=null && value.equals("requested"))
                haveRequestTag=true;
            else
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_INVVALUE",new Object[] {destPath,value}));
            ret=true;
        } else {
            for (Iterator i = childContainers.listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());
                if (l.propagateValue(destPath,value,tryToCreate,allowOverwrite)) {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    /** fuellt die hashtable 'values' mit den werten der de-syntaxelemente; dazu
        wird in allen anderen typen von syntaxelementen die liste der
        child-elemente durchlaufen und deren 'fillValues' methode aufgerufen */
    public void extractValues(Hashtable values)
    {
        for (Iterator i = childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());
            l.extractValues(values);
        }
    }

    /** diese toString() methode wird benutzt, um den wert eines
        de-syntaxelementes in human-readable-form zurueckzugeben. innerhalb eines
        de-elementes wird der wert in der hbci-form gespeichert */
    public String toString(int zero)
    {
        return toString();
    }

    protected void setPath(String path)
    {
        this.path = path;
    }

    protected void setName(String name)
    {
        this.name = name;
    }

    protected void setType(String type)
    {
        this.type = type;
    }

    protected final void setValid(boolean valid)
    {
        this.valid = valid;
    }

    /** gibt einen string mit den typnamen (msg,seg,deg,de,...) des 
        elementes zurueck */
    protected abstract String getElementTypeName();

    /** liefert das delimiter-zeichen zurueck, dass innerhalb dieses
        syntaxelementes benutzt wird, um die einzelnen child-elemente voneinander
        zu trennen */
    protected abstract char getInDelim();

    public boolean isValid()
    {
        return valid;
    }

    /** haengt an die 'childElements' ein neues element an, welches durch den
        xml-knoten 'ref' identifiziert wird; wird beim erzeugen von elementen
        benutzt */
    protected abstract MultipleSyntaxElements createNewChildContainer(Node ref, Document syntax);

    /** beim parsen: haengt an die 'childElements' ein neues Element an. der
        xml-knoten 'ref' gibt an, um welches element es sich dabei handelt; aus
        'res' (der zu parsende String) wird der wert fuer das element ermittelt
        (falls es sich um ein de handelt); in 'predefined' ist der wert des
        elementes zu finden, der laut syntaxdefinition ('syntax') an dieser stelle
        auftauchen mueste (optional; z.b. fuer segmentcodes); 'predelim*' geben
        die delimiter an, die direkt vor dem zu erzeugenden syntaxelement
        auftauchen muessten */
    protected abstract MultipleSyntaxElements parseNewChildContainer(Node ref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable predefs,Hashtable valids);

    public int checkSegSeq(int value)
    {
        for (Iterator i=childContainers.iterator();i.hasNext();) {
            MultipleSyntaxElements a=(MultipleSyntaxElements)(i.next());
            value=a.checkSegSeq(value);
        }

        return value;
    }

    protected void addChildContainer(MultipleSyntaxElements x)
    {
        childContainers.add(x);
    }

    /** ueberpreuft, ob das syntaxelement alle restriktionen einhaelt; ist das
        nicht der fall, so wird eine Exception ausgeloest. die meisten
        syntaxelemente koennen sich nicht selbst ueberpruefen, sondern rufen statt
        dessen die validate-funktion der child-elemente auf */
    public void validate()
    {
        if (!needsRequestTag || haveRequestTag) {
            for (Iterator i = childContainers.listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements l = (MultipleSyntaxElements)(i.next());
                l.validate();
            }

            /* wenn keine exception geworfen wurde, dann ist das aktuelle element
               offensichtlich valid */
            setValid(true);
        }
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
    }
    
    protected void destroy()
    {
        childContainers.clear();
        childContainers=null;
        name=null;
        parent=null;
        path=null;
        type=null;
    }
}

