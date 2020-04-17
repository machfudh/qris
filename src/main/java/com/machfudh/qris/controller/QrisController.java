/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.machfudh.qris.controller;

import com.machfudh.qris.dto.DataResponse;
import com.machfudh.qris.dto.Datachecksum;
import com.machfudh.qris.dto.Datatoxml;
import com.machfudh.qris.utilities.Utilities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author machfudh
 */
@RestController
public class QrisController {

    private static final Logger log = Logger.getLogger(QrisController.class);
    static Utilities utilities = new Utilities();

    @PostMapping("/api/qris")
    @ResponseStatus(HttpStatus.OK)
    public String qrisTest(@RequestParam("qriscode") String qriscode) {

//        String[] mercInfo = utilities.getKey("qris.merchant.information").split(",");
        String mercInfo = "";
        Datachecksum datachecksum = new Datachecksum();
        Datatoxml datatoxml = new Datatoxml();
        ArrayList<String> dataMerchent = new ArrayList<>();
        String pesan;
        boolean xmlfilerstatus = false;
        
        ObjectMapper mapperObj = new ObjectMapper();
        Map<String, String> returtnMap = new HashMap<String, String>();
        DataResponse dataResponse = new DataResponse();

        datachecksum = utilities.checkCheksum(qriscode);
        dataResponse.result = datachecksum.result;
        dataResponse.setMessage(datachecksum.getMessage());
        if (datachecksum.result == true && datachecksum.data.size() > 0) {
            pesan = new String();
            String[] xmlfilter = utilities.getKey("qris.xml.filter").split(",");
            // cek ulang data dengan xml 
            for (int x = 0; x < datachecksum.data.size(); x++) {
                pesan = datachecksum.data.get(x);
//                System.out.println("======================== Controller " + x + "  :" + pesan);
                
                mercInfo = "999";
                xmlfilerstatus = false;
                String idIntPesan = pesan.substring(0, 2).trim();
                int potongPesan = Integer.valueOf(pesan.substring(2, 4).trim());
//                System.out.println("======================= idIntPEssan " + x + "  :" + idIntPesan);

                for (String lcfilter : xmlfilter) {
                    if (idIntPesan.equalsIgnoreCase(lcfilter)) {
//                    System.out.println(" masuk  idintpesan : "+idIntPesan+"  lcfilter : "+lcfilter);
                        mercInfo = lcfilter;
                        xmlfilerstatus = true;
                        break;

                    } else {
                        xmlfilerstatus = false;
                    }
//           
                }
                
                if (idIntPesan.equalsIgnoreCase(mercInfo) && xmlfilerstatus == true ) {
//                    System.out.println("======================= ["+ mercInfo +"] ============== " + x + "  :" + pesan);
                    String idMerchanInfo = pesan.substring(0, 4).trim();
                    dataMerchent = new ArrayList<>();
                    dataMerchent.addAll((Collection<? extends String>) datachecksum.mapData.get(idMerchanInfo));
                    if (dataMerchent.size() > 0) {
                        Map<String, String> subMap = new HashMap<String, String>();
                        for (String data : dataMerchent) {
                            datatoxml = utilities.cektoqrisxml(data, idIntPesan);
                            if (datatoxml.getResult() == true && !datatoxml.getXmlName().isEmpty()) {
                                log.info(" Sub Information  ==>" + datatoxml.getXmlName() + " : " + datatoxml.getXmlValue());
                                subMap.put(datatoxml.getXmlName(), datatoxml.getXmlValue());
                            }
                        }
//                        try {
                            System.out.println(subMap.toString());
                            returtnMap.put(idIntPesan, subMap.toString());
//                        } catch (IOException ex) {
//                            java.util.logging.Logger.getLogger(QrisController.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                    }
                } else {
                    datatoxml = utilities.cektoqrisxml(pesan, idIntPesan);
                    if (datatoxml.getResult() == true && !datatoxml.getXmlName().isEmpty()) {
                        log.info("Root ==>" + datatoxml.getXmlName() + " : " + datatoxml.getXmlValue());
                        returtnMap.put(datatoxml.getXmlName(), datatoxml.getXmlValue());
                    }

                }

            }


        }
        
        String jsonResp = null;
        try {
            dataResponse.setData(returtnMap);
            jsonResp = mapperObj.writeValueAsString(dataResponse);
            log.info("response ==> : "+jsonResp);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(QrisController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
        
        return jsonResp; //.replaceAll("\\\\", "");

    }

}
