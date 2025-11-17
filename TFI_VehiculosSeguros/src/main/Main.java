package main;

import dao.SeguroVehicularDAO;
import dao.VehiculoDAO;
import service.SeguroVehicularService;
import service.VehiculoService;

public class Main {
    public static void main(String[] args) {
        
        try {
            // Inicializar las capas (asumiendo que tus DAOs tienen constructor por defecto)
            VehiculoDAO vDao = new VehiculoDAO(); 
            SeguroVehicularDAO sDao = new SeguroVehicularDAO(); 
            
            // Inyectar dependencias en la capa Service
            VehiculoService vService = new VehiculoService(vDao, sDao);
            SeguroVehicularService sService = new SeguroVehicularService(sDao);
            
            // Crear el menú e iniciar la aplicación
            AppMenu menu = new AppMenu(vService, sService);
            menu.iniciar();
            
        } catch (Exception e) {
            // Este try-catch es para atrapar errores de inicialización, como si faltan constructores
            System.err.println("❌ ERROR FATAL AL INICIALIZAR LA APLICACIÓN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}