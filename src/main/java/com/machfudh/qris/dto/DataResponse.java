/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.machfudh.qris.dto;

import java.util.Map;
import lombok.Setter;

/**
 *
 * @author machfudh
 */
@Setter
public class DataResponse {
    
    public boolean result;
    public String message;
    public Map data;
    
}
