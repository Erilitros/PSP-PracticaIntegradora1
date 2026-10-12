/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iescavanilles.ejercicio1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Eric
 */
public class ProcessMonitorCLI {
    
    public static void main(String[] args){
        System.out.println("Auditoria de sistema");
        hito1();
        hito2();
        hito3();
        hito4();
    }
    
    public static void hito1(){
        //1.1 Detecta automáticamente el sistema operativo
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("Sistema operativo:" + osName);
        
        //1.2.Se construye una instancia de ProcessBuilder
        //Usamos una List de Strings para los argumentos, así no usamos shell
        List<String> command = new ArrayList<>();
        
        if (osName.contains("win")){
            command.add("tasklist");
        } else {
            command.add("ps");
            command.add("aux");
        }
        
        //Iniciamos el constructor de procesos con la lista de comandos
        ProcessBuilder pb = new ProcessBuilder(command);
        
        try{
            //1.3. Inicia el proceso y lee su salida estandar
            Process p = pb.start();
            
            //Leemos el InputStream del proceso (lo que saca el proceso)
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            String linea;
            int totalProcesos = 0;
            boolean javaDetectado = false;
            
            //leemos linea a linea hasta que no haya más
            while((linea = reader.readLine()) != null){
                totalProcesos++;
                
                //Se comprueba si la palaba "java" está en la linea actual
                if(linea.toLowerCase().contains("java")){
                    javaDetectado = true;
                }
            }
            
            int exitCode = p.waitFor();
            
            System.out.println("Total procesos detectados: "+ totalProcesos);
            if(javaDetectado){
                System.out.println("Proceso java detectado");
            }else{
                System.out.println("No se detectaron procesos 'java'");
            }
            System.out.println("Codigo de salida del proceso: " + exitCode);
            
        }catch(IOException | InterruptedException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static void hito2(){
        System.out.println("Hito 2");
        
        //2.2 Lanzar la clase compilada LogFilterEngine ocmo subproceso
        //Para ejecutar un programa Java desde otro Java, usamos el comando "java"
        //necesitamos decirle donde estan los archivos compilador (.class)
        //para eso usamos System.getProperty("java.class.path)
        String classpath = System.getProperty("java.class.path");
        
        List<String> command = new ArrayList<>();
        command.add("java");//Comando para ejecutar
        command.add("-cp"); //Atributo ClassPath (ruta de clases)
        command.add(classpath); //La ruta que hemos obtenido
        command.add("com.iescavanilles.ejercicio1.LogFilterEngine"); 
        
        ProcessBuilder pb = new ProcessBuilder(command);
        
        try {
            //Arrancamos el subproceso hijo
            Process p = pb.start();
            
            //2.3 Obtener flujo de escritura (getOutputStream)
            //Usamos p.getOutputStream() para conectarnos al System.in del hijo
            //Lo envolvemos en un PrintWriter (junto con OutputStreamWriter) porque nos permite usar ".println()"
            //esto nos sirve para enviar texto linea a linea
            java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
            
            //Enviamos un bloque de logs simulador al hijo
            writer.println("Iniciando sistema operativo...");
            writer.println("CRITICAL: El disco duro esta lleno.");
            writer.println("Usuario logueado correctamente.");
            writer.println("WARNING: Memoria RAM alta.");
            writer.println("Ejecutando proceso java en el puerto 8080.");
            
            //flush() obliga a que los datos viajen por la tuberia ahora mismo
            //Si no se pone se quedan atascados en memoria y el programa se bloquea
            writer.flush();
            
            //Se cierra la escritura
            //Esto envia un aviso al hijo de "ya no hay mas texto" (el readLine() del hijo devuelve null)
            //y así el buble while del hijo acaba
            writer.close();
            
            //2.4. Leer respuesta procesada del hijo (getInputStream)
            //Ahora nos conectamos a la salida del hijo para ver que contesta
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineaRespuesta;
            
            System.out.println("Respuesta procesada que nos devuelve el hijo;");
            
            //Leemos todo lo que el hijo haya impreso por su println
            while((lineaRespuesta = reader.readLine()) != null){
                System.out.println(lineaRespuesta);
            }
            
            // Esperamos a que el hijo termine para no dejar procesos sueltos.
            p.waitFor();
           
        } catch (IOException | InterruptedException e) {
            System.out.println("Error en Hito 2: " + e.getMessage());
        }
    }
    
    public static void hito3(){
        System.out.println("Hito 3");
        
        //3.1. Preparamos comando de diagnostico
        //Usamos "systeminfo" para windows
        String osName = System.getProperty("os.name").toLowerCase();
        List<String> command = new ArrayList<>();
        if (osName.contains("win")) {
            command.add("systeminfo");
        } else {
            command.add("uname");
            command.add("-a");
        }
        
        ProcessBuilder pb = new ProcessBuilder(command);
        
        
        //Paso 3.2 redirigir los errores
        //redirectErrorStream(true) hace que si el comando falla, el mensaje de error
        // viaje por la misma tuberia que la salida normal, juntandolo todo
        pb.redirectErrorStream(true);
        
        //Paso 3.3 redirigr la salida a un archivo
        //En lugar de usar getInputStream() y leer con while le decimos que saque todo en un archivo
        java.io.File archivoLog = new java.io.File("reporte_sistema.log");
        pb.redirectOutput(ProcessBuilder.Redirect.to(archivoLog));
        
        try{
            System.out.println("Ejecutando diagnostico");
            
            //Paso 3.4 Se inicia el proceso
            Process p = pb.start();
            
            //Se espera a que termine de escribir todo
            p.waitFor();
            
            // Le confirmamos al usuario dónde se ha guardado el archivo.
            System.out.println("Diagnóstico completado. Archivo generado en: " + archivoLog.getAbsolutePath());
        }catch (IOException | InterruptedException e) {
            System.out.println("Error en Hito 3: " + e.getMessage());
        }
    }
    
    public static void hito4() {
        System.out.println("HITO 4");
        
        // PASO 1: Preparamos un comando que sabemos que va a tardar mucho.
        String osName = System.getProperty("os.name").toLowerCase();
        List<String> command = new ArrayList<>();
        
        if (osName.contains("win")) {
            command.add("ping");
            command.add("-n"); // En Windows se usa -n para el número de pings
            command.add("10");
            command.add("127.0.0.1");
        } else {
            command.add("ping");
            command.add("-c");
            command.add("10");
            command.add("127.0.0.1");
        }
        
        ProcessBuilder pb = new ProcessBuilder(command);
        
        try {
            System.out.println("Iniciando proceso pesado (ping de 10 segundos)...");
            
            // PASO 2: Arrancamos el proceso lento
            Process p = pb.start();
            
            // PASO 3: En lugar del p.waitFor() normal, usamos el que tiene límite de tiempo.
            // Le decimos que espere un máximo de 3 segundos.
            // Este método nos devuelve TRUE si el proceso terminó por sí solo a tiempo, 
            // o FALSE si pasaron los 3 segundos y el proceso seguía vivo.
            boolean terminoATiempo = p.waitFor(3, TimeUnit.SECONDS);
            
            // PASO 4: Comprobamos el resultado de la espera
            if (!terminoATiempo) {
                // Si entra aquí, es que devolvió FALSE (se acabó el tiempo y sigue vivo).
                System.out.println("El proceso supero el tiempo límite de 3 segundos.");
                
                // PASO 5: Destruimos el proceso forzosamente en el sistema operativo.
                p.destroyForcibly();
                
                System.out.println("Proceso cancelado y cerrado de forma limpia.");
            } else {
                // Si entra aquí, es que devolvió TRUE (el proceso era rápido y terminó a tiempo).
                System.out.println("El proceso termino correctamente dentro del tiempo permitido.");
            }
            
        } catch (IOException | InterruptedException e) {
            System.out.println("Error en Hito 4: " + e.getMessage());
        }
    }
}
