package com.mycompany.trabajofinalintegradorpii;

import config.DatabaseConnection;
import dao.SeguroVehicularDao;
import dao.VehiculoDao;
import java.sql.Connection;
import java.sql.SQLException;
import main.AppMenu;
import service.SeguroVehicularService;
import service.VehiculoService;

/**
 * Punto de entrada de la aplicación.
 */
public class TrabajoFinalIntegradorPII {

    public static void main(String[] args) {
        
        try (Connection conn = DatabaseConnection.getConnection()) {

            System.out.println("✅ Conexión a la base de datos verificada.");
            System.out.println("Cerrando conexión de prueba...");

            // (La conexión se cierra automáticamente gracias al try-with-resources)
            // ==========================================================
            // INYECCIÓN DE DEPENDENCIAS
            // ==========================================================
            // A. DAOs (Acceso a Datos)
            SeguroVehicularDao seguroDAO = new SeguroVehicularDao();
            VehiculoDao vehiculoDAO = new VehiculoDao();

            // B. Services (Lógica de Negocio)
            SeguroVehicularService seguroService = new SeguroVehicularService();
            VehiculoService vehiculoService = new VehiculoService();

            // C. Menú / UI (Capa de Presentación)
            AppMenu menu = new AppMenu(vehiculoService, seguroService);

            // D. Iniciar la aplicación
            menu.iniciar();

        } catch (SQLException e) {
            // Error crítico: sin DB no se puede iniciar
            System.err.println("\n--- ❌ ERROR CRÍTICO DE CONEXIÓN ---");
            System.err.println("La aplicación no puede iniciarse. Verifique la base de datos.");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            // Otros errores inesperados
            System.err.println("\n--- ❌ ERROR INESPERADO AL INICIAR ---");
            e.printStackTrace();
        }
    }
}
