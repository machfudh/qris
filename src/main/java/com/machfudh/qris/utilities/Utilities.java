/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.machfudh.qris.utilities;

import com.machfudh.qris.dto.Datachecksum;
import com.machfudh.qris.dto.Datatoxml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author machfudh
 */
public class Utilities {

    private static final Logger log = Logger.getLogger(Utilities.class);

    public Utilities() {
        /**
         * To test development local
         */
//		PropertyConfigurator.configure(dir + "/CFG/log4j.properties");
        // End To Test Development
        /**
         * To development server Or Production
         */
        PropertyConfigurator.configure("/home/machfudh/Documents/qris/qris/config/log4j.properties");
        // End To development server Or Production
    }

    public String getKey(String key) {
        Properties p2 = new Properties();
        /**
         * To development server Or Production
         */
        p2 = loadProperties("/home/machfudh/Documents/qris/qris/config/config.properties");
        // End To development server Or Production
        String val = p2.getProperty(key);
        return val;
    }

    public static Properties loadProperties(String sFile) {
        Properties p = new Properties();
        try {
            FileInputStream in = new FileInputStream(sFile);
            p.load(in);
            in.close();
        } catch (Exception e) {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            log.error(e.getMessage());
        }
        return p;
    }

    public Datachecksum checkCheksum(String message) {
        Boolean result = false;
        Datachecksum datachecksum = new Datachecksum();
        datachecksum.data = new ArrayList<>();
        datachecksum.mapData = new HashMap<String, ArrayList>();
        ArrayList<String> merchentData = new ArrayList<>();
        datachecksum.result = true;

        log.info("read message <" + message + " >");

        boolean xmlfilerstatus = false;
        String merchentInfo = "";
        String merchentKey = "";
        String pesan = message;
        String mercInfo = "999";
//        String[] mercInfo = getKey("qris.merchant.information").split(",");
        String[] xmlfilter = getKey("qris.xml.filter").split(",");

        String idPesan = pesan.substring(0, 2).trim();
        int x = 1;
        while (pesan.length() > 0) {

            String idStrPesan = pesan.substring(0, 2).trim();
            int idIntPesan = Integer.valueOf(pesan.substring(0, 2).trim());
            int potongPesan = Integer.valueOf(pesan.substring(2, 4).trim());
           
            for (String lcfilter : xmlfilter) {
                if( idIntPesan == Integer.valueOf(lcfilter)){
                    mercInfo = lcfilter;
                    xmlfilerstatus = true;
                    break;
                    
                }else{
                    xmlfilerstatus = false;
                }
            }
            
            if (idIntPesan == Integer.valueOf(mercInfo) && xmlfilerstatus == true ) {
                merchentData = new ArrayList<>();
                datachecksum.data.add(pesan.substring(0, potongPesan + 4));
                merchentKey = pesan.substring(0, 4);
                merchentInfo = pesan.substring(4, potongPesan + 4);
                pesan = pesan.substring(potongPesan + 4, pesan.length());
                while (merchentInfo.length() > 0) {
                    int potongInfo = Integer.valueOf(merchentInfo.substring(2, 4).trim());
//                    log.info(" Merhcent Info :" + merchentInfo.substring(0, potongInfo + 4));
                    merchentData.add(merchentInfo.substring(0, potongInfo + 4));
                    merchentInfo = merchentInfo.substring(potongInfo + 4, merchentInfo.length());

                }
                if (merchentInfo.length() <= 0 && merchentData.size() != 0) {
                    datachecksum.mapData.put(merchentKey, merchentData);
                }

            } else {
                if (pesan.length() >= potongPesan + 4) {
//                    log.info(" Root   : " + pesan.substring(0, potongPesan + 4));
                    if (idStrPesan.equalsIgnoreCase(getKey("qris.checksum"))) {
                        log.info(" checksum ==> " + pesan.substring(0, potongPesan + 4));
                        //isi data list
                        datachecksum.result = true;
                        datachecksum.message = pesan.substring(0, potongPesan + 4);
                        datachecksum.data.add(pesan.substring(0, potongPesan + 4));

                    } else {
                        datachecksum.message = pesan.substring(0, potongPesan + 4);
                        datachecksum.data.add(pesan.substring(0, potongPesan + 4));
                    }
                    pesan = pesan.substring(potongPesan + 4, pesan.length());
                } else {
                    pesan = "";
                }
            }

            x++;
        }

        return datachecksum;
    }

    public String checkIDdataxxx(String message) {
        log.info("read message <" + message + " >");

        String pesan = message;
        String[] mercInfo = getKey("qris.merchant.information").split(",");

        String idPesan = pesan.substring(0, 2).trim();
        while (pesan.length() == 0) {

            int idIntPesan = Integer.valueOf(pesan.substring(0, 2).trim());
            int potongPesan = Integer.valueOf(pesan.substring(2, 4).trim());

            if (idIntPesan >= Integer.valueOf(mercInfo[0]) || idIntPesan <= Integer.valueOf(mercInfo[1])) {
                pesan = pesan.substring(potongPesan + 2, pesan.length());
            } else {

                pesan = pesan.substring(potongPesan + 2, pesan.length());
            }

        }

        return "";
    }

    public Datatoxml cektoqrisxml(String message, int idField) {

        Datatoxml datatoxml = new Datatoxml();
        String elementId = "";
        String elementFormat = "";
        String elementPresence = "";
        String elementName = "";
        int elementLengthin = 0;
        int elementLengthout = 0;
        
        String mercInfo = "999";
        boolean xmlfilerstatus = false;

        String idMessage = message.substring(0, 2).trim();

        File fXmlFile;  //= new File(getKey("qris.xml.root")); // default xml
        
        String[] xmlfilter = getKey("qris.xml.filter").split(",");
        String[] xmlfile = getKey("qris.xml.file").split(",");
        String xmlurl = getKey("qris.xml.url");
        String xmllink = "";
        
         for (int xx=0; xx<xmlfilter.length; xx++) {
                if( idField == Integer.valueOf(xmlfilter[xx].trim())){
                    mercInfo = xmlfilter[xx].trim();
                    xmllink = xmlurl+xmlfile[xx];
                    xmlfilerstatus = true;
//                    System.out.println("xxxCek sub ...:"+ mercInfo+" status :"+ xmlfilerstatus+ "link : "+xmllink);
                    break;
                }else{
                    xmlfilerstatus = false;
                    xmllink = xmlurl+getKey("qris.xml.root");
                }
//           
            }

            fXmlFile = new File(xmllink);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();

            Document doc;
            try {
                doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("field");

                elementId = "";
                elementFormat = "";
                elementPresence = "";
                elementName = "";
                elementLengthin = 0;
                elementLengthout = 0;
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        elementId = eElement.getAttribute("fieldid").toString().trim();
//                        System.out.println("Element ID :" + elementId);
                        if (idMessage.equalsIgnoreCase(elementId)) {
                            elementFormat = eElement.getAttribute("format").toString().trim();
                            elementPresence = eElement.getAttribute("presence").toString().trim();
                            elementName = eElement.getAttribute("name").toString().trim();
                            elementLengthin = Integer.valueOf(eElement.getAttribute("lengthin").toString().trim());
                            elementLengthout = Integer.valueOf(eElement.getAttribute("lengthout").toString().trim());
                            datatoxml = checklenghtdata(message, elementFormat, elementLengthin, elementLengthout, elementPresence);
//                            System.out.println("di xml " + datatoxml.getResult());
                            datatoxml.setXmlName(elementName.toString().trim());
                            break;
                        }
                    }

                }

            } catch (SAXException ex) {
                java.util.logging.Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (ParserConfigurationException ex) {
            java.util.logging.Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return datatoxml;

    }

    public Datatoxml checklenghtdata(String message, String format, int lengthin, int lengthout, String presence) {

        Datatoxml datatoxml = new Datatoxml();
        String idMessage = message.substring(0, 2).trim();
        int sizeMessage = Integer.valueOf(message.substring(2, 4).trim());
        String pesanMessage = message.substring(4, message.length()).trim();

        datatoxml.setResult(Boolean.TRUE);
        datatoxml.setMessage("success parsing ");
        datatoxml.setXmlValue(pesanMessage);

        if (presence.equalsIgnoreCase("M")) {
            if (pesanMessage.length() == 0) {
                datatoxml.setResult(Boolean.FALSE);
                datatoxml.setMessage("salah di size message ... ! ");
                datatoxml.setXmlValue(pesanMessage);
                return datatoxml;
            }
        }

        if (lengthin == lengthout) {
            if (sizeMessage != lengthin) {
                datatoxml.setResult(Boolean.FALSE);
                datatoxml.setMessage("salah di size message ... ! ");
                datatoxml.setXmlValue(pesanMessage);
                return datatoxml;
            }
        }

        if (pesanMessage.length() != sizeMessage) {
            datatoxml.setResult(false);
            datatoxml.setMessage("salah di size message ... ! ");
            datatoxml.setXmlValue(pesanMessage);
            return datatoxml;
        }

        if (format.equalsIgnoreCase("N")) {
            if (!pesanMessage.matches("\\d+")) {
                datatoxml.setResult(Boolean.FALSE);
                datatoxml.setMessage("salah di size message ... ! ");
                datatoxml.setXmlValue(pesanMessage);
                return datatoxml;
            }
        }

        return datatoxml;
    }

}
