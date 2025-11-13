/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.trabajofinalintegradorpii;

import config.DatabaseConnection;
import dao.SeguroVehicularDao;
import dao.VehiculoDao;
import entities.Cobertura;
import entities.SeguroVehicular;
import entities.Vehiculo;
import exception.ServiceException;
import exception.ValidationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import service.SeguroVehicularService;
import service.VehiculoService;

/**
 *
 * @author lucasgragera
 */
public class TrabajoFinalIntegradorPII {

    public static void main(String[] args) {
        System.out.println("Como andan muchachas");
        
        System.out.println("Prueba de conexion a la base de datos con la IA xd");
        
        // Usamos try-with-resources:
        // 1. Llama a getConnection()
        // 2. Si tiene éxito, mete la conexión en la variable 'conn'
        // 3. Al final, CIERRA la conexión automáticamente (¡súper útil!)
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Si llegas a esta línea, ¡la conexión fue exitosa!
            // (Tu método 'getConnection' ya imprimió "Conexion establecida...")
            
            // Opcional: puedes hacer una validación extra
            boolean esValida = conn.isValid(5); // Revisa la conexión (timeout de 5 seg)
            System.out.println("¿La conexión es válida? = " + esValida);
            
        } catch (SQLException e) {
            
            // Si algo falla (puerto, pass, URL, etc.), caerá aquí
            System.err.println("--- ❌ ¡ERROR DE CONEXIÓN! ---");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Mensaje: " + e.getMessage());
            // e.printStackTrace(); // Descomenta si quieres ver el rastro completo
            
        }
    }
}




