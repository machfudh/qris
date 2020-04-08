/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.machfudh.qris.dto;

import java.util.ArrayList;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author machfudh
 */
@Data
public class Datachecksum {
    
    public boolean result;
    public String message;
    public ArrayList<String> data;
    public Map mapData;
//    ArrayList<ArrayList<String>> mapData;
    
}
