/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iescavanilles.ejercicio1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Eric
 */
public class LogFilterEngine {
    public static void main (String[] args){
        //Envolvemos System.in (la entrada estandar) en un bufferedReader para poder leer linea a linea
        //A diferencia del programa principal, aquí SI usamos System.in porque este programa recibira
        //los datos que le envíe el programa padre
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            
            String linea;
            
            //Leemos continuamente de la entrada estandar hasta que el padre cierre la comunicación
            while ((linea = reader.readLine()) != null) {
              
                //Convertimos la linea recibida a mayusculas
                String lineaMayus = linea.toUpperCase();
                
                if(lineaMayus.contains("CRITICAL") || lineaMayus.contains("WARNING") || lineaMayus.contains("JAVA")){
                    
                    //Si cumple la condicion se imporime por system.out
                    //Esto lo cpaturara el programa padre 
                    System.out.println("Filtrado" + lineaMayus);
                }
            }
            
        }catch(IOException e){
            System.out.println("Error en LogFilterEngine:" + e.getMessage());
        }
    }
}
