package main;

import entities.Cobertura;
import entities.SeguroVehicular;
import entities.Vehiculo;
import service.SeguroVehicularService;
import exception.ServiceException;
import exception.ValidationException;
import service.VehiculoService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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
    // BUCLE PRINCIPAL
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
                scanner.nextLine(); // Consumir salto de línea
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
                        menuBuscarPorId();
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
        System.out.println("\n--- MENÚ SEGUROS ---");
        System.out.println("Funcionalidad no implementada en este ejemplo.");
        System.out.println("Volviendo al Menú Principal...");
    }

    // =========================================================================
    // IMPLEMENTACIONES CRUD
    // =========================================================================
    private void menuCrearVehiculo() {
        System.out.println("\n*** CREAR VEHÍCULO Y ASOCIAR SEGURO ***");
        try {
            System.out.print("Aseguradora (NOT NULL): ");
            String aseguradora = scanner.nextLine();

            System.out.print("Nro. Póliza (UNIQUE): ");
            String nroPoliza = scanner.nextLine();

            System.out.print("Cobertura (RC, TERCEROS, TODO_RIESGO): ");
            String coberturaStr = scanner.nextLine().toUpperCase();
            Cobertura coberturaEnum = Cobertura.valueOf(coberturaStr);

            System.out.print("Fecha de Vencimiento (AAAA-MM-DD): ");
            LocalDate vencimiento = LocalDate.parse(scanner.nextLine());

            SeguroVehicular seguro = new SeguroVehicular(
                    null,
                    false,
                    aseguradora,
                    nroPoliza,
                    coberturaEnum,
                    vencimiento
            );

            // Datos vehículo
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

            Vehiculo vehiculo = new Vehiculo(
                    null,
                    false,
                    dominio,
                    marca,
                    modelo,
                    anio,
                    nroChasis,
                    seguro
            );

            vehiculoService.insertar(vehiculo);

            System.out.println("✅ ÉXITO: Vehículo y Seguro creados transaccionalmente.");
            System.out.println("Vehículo ID: " + vehiculo.getId() + " / Póliza ID: " + vehiculo.getSeguro().getId());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un número entero válido.");
            scanner.nextLine();
        } catch (DateTimeParseException e) {
            System.err.println("❌ ERROR: Formato de fecha inválido. Use AAAA-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ ERROR: Cobertura inválida. Valores permitidos: RC, TERCEROS, TODO_RIESGO.");
        } catch (ServiceException e) {
            System.err.println("❌ ERROR TRANSACCIONAL: " + e.getMessage());
        }
    }

    private void menuLeerVehiculo() {
        System.out.println("\n*** LECTURA POR ID ***");
        try {
            System.out.print("Ingrese ID del Vehículo: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            Vehiculo v = vehiculoService.getById(id);

            System.out.println("\n✅ Vehículo encontrado:");
            System.out.println(v);

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }

    private void menuListarVehiculos() {
        System.out.println("\n*** LISTADO DE VEHÍCULOS ACTIVOS ***");
        try {
            List<Vehiculo> lista = vehiculoService.getAll();

            if (lista.isEmpty()) {
                System.out.println("No hay vehículos activos registrados.");
                return;
            }

            for (Vehiculo v : lista) {
                System.out.println(v);
            }

        } catch (ServiceException e) {
            System.err.println("❌ ERROR al listar: " + e.getMessage());
        }
    }

    private void menuActualizarVehiculo() {
        System.out.println("\n*** ACTUALIZAR VEHÍCULO ***");
        try {
            System.out.print("Ingrese el ID del vehículo a actualizar: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            Vehiculo v = vehiculoService.getById(id);

            System.out.println("Datos actuales: " + v);
            System.out.println("Ingrese los nuevos datos (deje en blanco para no modificar):");

            System.out.print("Nuevo Dominio (Actual: " + v.getDominio() + "): ");
            String dominio = scanner.nextLine().toUpperCase();
            if (!dominio.isEmpty()) v.setDominio(dominio);

            System.out.print("Nueva Marca (Actual: " + v.getMarca() + "): ");
            String marca = scanner.nextLine();
            if (!marca.isEmpty()) v.setMarca(marca);

            System.out.print("Nuevo Modelo (Actual: " + v.getModelo() + "): ");
            String modelo = scanner.nextLine();
            if (!modelo.isEmpty()) v.setModelo(modelo);

            System.out.print("Nuevo Nro. Chasis (Actual: " + v.getNroChasis() + "): ");
            String nroChasis = scanner.nextLine();
            if (!nroChasis.isEmpty()) v.setNroChasis(nroChasis);

            System.out.print("Nuevo Año (Actual: " + v.getAnio() + "): ");
            String anioStr = scanner.nextLine();
            if (!anioStr.isEmpty()) v.setAnio(Integer.parseInt(anioStr));

            vehiculoService.actualizar(v);
            System.out.println("✅ ÉXITO: Vehículo ID " + id + " actualizado.");

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un número válido.");
            scanner.nextLine();
        } catch (NumberFormatException e) {
            System.err.println("❌ ERROR: El año ingresado no es válido.");
        } catch (ServiceException | ValidationException e) {
            System.err.println("❌ ERROR al actualizar: " + e.getMessage());
        }
    }

    private void menuEliminarVehiculo() {
        System.out.println("\n*** ELIMINACIÓN LÓGICA DE VEHÍCULO ***");
        try {
            System.out.print("Ingrese ID del Vehículo: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            vehiculoService.eliminar(id);
            System.out.println("✅ ÉXITO: Vehículo ID " + id + " marcado como eliminado.");

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            System.err.println("❌ ERROR al eliminar: " + e.getMessage());
        }
    }

    private void menuBuscarPorId() {
        System.out.println("\n*** BÚSQUEDA POR ID ***");

        try {
            System.out.print("Ingrese ID del Vehículo: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            Vehiculo v = vehiculoService.getById(id);

            System.out.println("\n✅ Resultados:");
            System.out.println(v);

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }
}
