package main;

import entities.SeguroVehicular;
import entities.Vehiculo;
import service.SeguroVehicularService;
import service.ServiceException;
import service.VehiculoService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona la interacción por consola (AppMenu) para las entidades
 * Vehiculo y SeguroVehicular.
 */
public class AppMenu {

    private final VehiculoService vehiculoService;
    private final SeguroVehicularService seguroService;
    private final Scanner scanner;

    public AppMenu(VehiculoService vService, SeguroVehicularService sService) {
        this.vehiculoService = vService;
        this.seguroService = sService;
        this.scanner = new Scanner(System.in);
    }

    // =========================================================================
    // BU CLE PRINCIPAL
    // =========================================================================

    public void iniciar() {
        int opcion = -1;
        while (opcion != 3) {
            System.out.println("\n==============================================");
            System.out.println("*** GESTIÓN DE VEHÍCULOS Y SEGUROS (TFI) ***");
            System.out.println("==============================================");
            System.out.println("1. Gestión de Vehículos (CRUD)");
            System.out.println("2. Gestión de Seguros (CRUD)");
            System.out.println("3. Salir");
            System.out.print("Ingrese opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea
                switch (opcion) {
                    case 1:
                        menuGestionVehiculos();
                        break;
                    case 2:
                        menuGestionSeguros();
                        break;
                    case 3:
                        System.out.println("Aplicación finalizada. ¡Hasta pronto!");
                        break;
                    default:
                        System.err.println("❌ Opción inválida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.err.println("❌ Error: Ingrese un número para la opción.");
                scanner.nextLine(); // Limpiar buffer
                opcion = -1;
            }
        }
    }

    // =========================================================================
    // SUB-MENÚS DE GESTIÓN
    // =========================================================================

    private void menuGestionVehiculos() {
        int opcion = -1;
        while (opcion != 7) {
            System.out.println("\n--- MENÚ VEHÍCULOS ---");
            System.out.println("1. Crear Vehículo y Asociar Seguro (Transaccional)");
            System.out.println("2. Leer Vehículo por ID");
            System.out.println("3. Listar Todos (Activos)");
            System.out.println("4. Actualizar Vehículo");
            System.out.println("5. Eliminar (Baja Lógica)");
            System.out.println("6. Buscar por Dominio (Búsqueda Relevante)");
            System.out.println("7. Volver al Menú Principal");
            System.out.print("Ingrese opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1:
                        menuCrearVehiculo();
                        break;
                    case 2:
                        menuLeerVehiculo();
                        break;
                    case 3:
                        menuListarVehiculos();
                        break;
                    case 4:
                        menuActualizarVehiculo();
                        break;
                    case 5:
                        menuEliminarVehiculo();
                        break;
                    case 6:
                        menuBuscarPorDominio();
                        break;
                    case 7:
                        break;
                    default:
                        System.err.println("❌ Opción inválida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.err.println("❌ Error: Ingrese un número para la opción.");
                scanner.nextLine();
                opcion = -1;
            }
        }
    }

    private void menuGestionSeguros() {
        // Implementación de CRUD simple para SeguroVehicular
        System.out.println("\n--- MENÚ SEGUROS ---");
        System.out.println("Funcionalidad no implementada en este ejemplo.");
        System.out.println("Volviendo al Menú Principal...");
    }


    // =========================================================================
    // IMPLEMENTACIONES CRUD (Requisito 6)
    // =========================================================================

    /**
     * Requisito: Operación Transaccional y Creación
     */
    private void menuCrearVehiculo() {
        System.out.println("\n*** CREAR VEHÍCULO Y ASOCIAR SEGURO ***");
        try {
            // --- 1. SOLICITAR DATOS DE SEGURO (B) ---
            System.out.print("Aseguradora (NOT NULL): ");
            String aseguradora = scanner.nextLine();
            System.out.print("Nro. Póliza (UNIQUE): ");
            String nroPoliza = scanner.nextLine();
            System.out.print("Cobertura (RC, TERCEROS, TODO_RIESGO): ");
            String cobertura = scanner.nextLine().toUpperCase();
            System.out.print("Fecha de Vencimiento (AAAA-MM-DD): ");
            LocalDate vencimiento = LocalDate.parse(scanner.nextLine());
            
            SeguroVehicular seguro = new SeguroVehicular(null, false, aseguradora, nroPoliza, cobertura, vencimiento);

            // --- 2. SOLICITAR DATOS DE VEHÍCULO (A) ---
            System.out.print("Dominio (UNIQUE, Ej: AB123CD): ");
            String dominio = scanner.nextLine().toUpperCase();
            System.out.print("Marca (NOT NULL): ");
            String marca = scanner.nextLine();
            System.out.print("Modelo (NOT NULL): ");
            String modelo = scanner.nextLine();
            System.out.print("Año: ");
            int anio = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Nro. Chasis (UNIQUE): ");
            String nroChasis = scanner.nextLine();

            Vehiculo vehiculo = new Vehiculo(null, false, dominio, marca, modelo, anio, nroChasis, seguro);

            // --- 3. LLAMAR AL SERVICE TRANSACCIONAL ---
            vehiculoService.crearVehiculoConSeguro(vehiculo);

            System.out.println("✅ ÉXITO: Vehículo y Seguro creados transaccionalmente.");
            System.out.println("Vehículo ID: " + vehiculo.getId() + " / Póliza ID: " + vehiculo.getSeguro().getId());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un número entero válido (ID o Año).");
            scanner.nextLine();
        } catch (DateTimeParseException e) {
             System.err.println("❌ ERROR: Formato de fecha inválido. Use AAAA-MM-DD.");
        } catch (ServiceException e) {
            System.err.println("❌ ERROR TRANSACCIONAL: " + e.getMessage());
        }
    }

    /**
     * Requisito: Búsqueda por ID (y manejo de IDs inexistentes)
     */
    private void menuLeerVehiculo() {
        System.out.println("\n*** LECTURA POR ID ***");
        try {
            System.out.print("Ingrese ID del Vehículo: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            Vehiculo v = vehiculoService.getById(id);
            System.out.println("\n✅ Vehículo encontrado:");
            System.out.println(v.toString());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            System.err.println("❌ ERROR: " + e.getMessage()); // Maneja ID inexistente
        }
    }
    
    /**
     * Requisito: Listar Todos (filtrando baja lógica)
     */
    private void menuListarVehiculos() {
        System.out.println("\n*** LISTADO DE VEHÍCULOS ACTIVOS ***");
        try {
            List<Vehiculo> lista = vehiculoService.getAll();

            if (lista.isEmpty()) {
                System.out.println("No hay vehículos activos registrados.");
                return;
            }

            for (Vehiculo v : lista) {
                System.out.println(v.toString());
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERROR al listar: " + e.getMessage());
        }
    }
    
    /**
     * Requisito: Eliminar (Baja Lógica)
     */
    private void menuEliminarVehiculo() {
        System.out.println("\n*** ELIMINACIÓN LÓGICA DE VEHÍCULO ***");
        try {
            System.out.print("Ingrese ID del Vehículo para dar de baja lógica: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            vehiculoService.eliminar(id); // Implementado en el Service
            System.out.println("✅ ÉXITO: Vehículo ID " + id + " marcado como eliminado.");

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            System.err.println("❌ ERROR al eliminar: " + e.getMessage());
        }
    }
    
    /**
     * Requisito: Búsqueda por campo relevante (Dominio)
     */
    private void menuBuscarPorDominio() {
        System.out.println("\n*** BÚSQUEDA POR DOMINIO ***");
        scanner.nextLine(); // Consumir línea
        try {
            System.out.print("Ingrese Dominio del Vehículo (Ej: AB123CD): ");
            String dominio = scanner.nextLine().toUpperCase();

            Vehiculo v = vehiculoService.getByDominio(dominio); // Este método debe estar en Service
            System.out.println("\n✅ Resultados:");
            System.out.println(v.toString());

        } catch (ServiceException e) {
            System.err.println("❌ ERROR: " + e.getMessage()); // Captura si el dominio no existe
        }
    }
}
