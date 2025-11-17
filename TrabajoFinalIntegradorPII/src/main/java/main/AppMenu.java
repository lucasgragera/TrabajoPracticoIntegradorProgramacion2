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
            System.out.println("6. Volver al Menú Principal");
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
        int opcion = -1;
        while (opcion != 6) {
            System.out.println("\n--- MENÚ SEGUROS ---");
            System.out.println("1. Crear Nuevo Seguro");
            System.out.println("2. Leer Seguro por ID");
            System.out.println("3. Listar Todos (Activos)");
            System.out.println("4. Actualizar Seguro");
            System.out.println("5. Eliminar (Baja Lógica)");
            System.out.println("6. Volver al Menú Principal");
            System.out.print("Ingrese opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea
                switch (opcion) {
                    case 1:
                        menuCrearSeguro();
                        break;
                    case 2:
                        menuLeerSeguroPorId();
                        break;
                    case 3:
                        menuListarSeguros();
                        break;
                    case 4:
                        menuActualizarSeguro();
                        break;
                    case 5:
                        menuEliminarSeguro();
                        break;
                    case 6:
                        break; // Vuelve al bucle principal
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

    private void menuLeerSeguroPorId() {
        System.out.println("\n*** LECTURA DE SEGURO POR ID ***");
        try {
            System.out.print("Ingrese ID del Seguro: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            // 1. Llamamos al servicio
            SeguroVehicular s = seguroService.getById(id);

            // 2. ¡CORRECCIÓN! Verificamos si es null
            if (s == null) {
                // Usamos el constructor (String, Throwable) como en el error anterior
                throw new ServiceException("No se encontró un Seguro con ID " + id, null);
            }

            // 3. Si llega aquí, 's' no es null
            System.out.println("\n✅ Seguro encontrado:");
            System.out.println(s.toString());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            // 4. El 'throw' de arriba es capturado aquí
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }

    private void menuListarSeguros() {
        System.out.println("\n*** LISTADO DE SEGUROS ACTIVOS ***");
        try {
            List<SeguroVehicular> lista = seguroService.getAll();

            if (lista.isEmpty()) {
                System.out.println("No hay seguros activos registrados.");
                return;
            }

            for (SeguroVehicular s : lista) {
                System.out.println(s.toString());
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERROR al listar: " + e.getMessage());
        }
    }

    private void menuActualizarSeguro() {
        System.out.println("\n*** ACTUALIZAR SEGURO ***");
        try {
            // 1. Pedir el ID y verificar que existe
            System.out.print("Ingrese el ID del seguro a actualizar: ");
            long id = scanner.nextLong();
            scanner.nextLine(); // Consumir el salto de línea

            // 2. Buscar el seguro.
            SeguroVehicular s = seguroService.getById(id);

            // ¡CORRECCIÓN! Verificamos si es null
            if (s == null) {
                throw new ServiceException("No se encontró un Seguro con ID " + id, null);
            }

            System.out.println("Datos actuales: " + s.toString());
            System.out.println("Ingrese los nuevos datos (deje en blanco para no modificar):");

            // 3. Pedir nuevos datos (condicionalmente)
            System.out.print("Nueva Aseguradora (Actual: " + s.getAseguradora() + "): ");
            String aseguradora = scanner.nextLine();
            if (!aseguradora.isEmpty()) {
                s.setAseguradora(aseguradora);
            }

            System.out.print("Nuevo Nro. Póliza (Actual: " + s.getNroPoliza() + "): ");
            String nroPoliza = scanner.nextLine();
            if (!nroPoliza.isEmpty()) {
                s.setNroPoliza(nroPoliza);
            }

            System.out.print("Nueva Cobertura (RC, TERCEROS, TODO_RIESGO) (Actual: " + s.getCobertura() + "): ");
            String coberturaStr = scanner.nextLine().toUpperCase();
            if (!coberturaStr.isEmpty()) {
                s.setCobertura(Cobertura.valueOf(coberturaStr)); // Puede lanzar IllegalArgumentException
            }

            System.out.print("Nueva Fecha Vencimiento (AAAA-MM-DD) (Actual: " + s.getVencimiento() + "): ");
            String fechaStr = scanner.nextLine();
            if (!fechaStr.isEmpty()) {
                s.setVencimiento(LocalDate.parse(fechaStr)); // Puede lanzar DateTimeParseException
            }

            // 4. Llamar al Service para que aplique los cambios
            seguroService.actualizar(s);
            System.out.println("✅ ÉXITO: Seguro ID " + id + " actualizado.");

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine(); // Limpiar buffer
        } catch (IllegalArgumentException e) {
            System.err.println("❌ ERROR: Cobertura inválida. Valores permitidos: RC, TERCEROS, TODO_RIESGO.");
        } catch (DateTimeParseException e) {
            System.err.println("❌ ERROR: Formato de fecha inválido. Use AAAA-MM-DD.");
        } catch (ServiceException | ValidationException e) {
            // Captura "ID no encontrado" (del getById) o 
            // "Póliza duplicada" (del actualizar)
            System.err.println("❌ ERROR al actualizar: " + e.getMessage());
        }
    }

    private void menuCrearSeguro() {
        System.out.println("\n*** CREAR NUEVO SEGURO ***");
        System.out.println("(Nota: Este seguro no estará asociado a ningún vehículo aún)");
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

            SeguroVehicular seguro = new SeguroVehicular(null, false, aseguradora, nroPoliza, coberturaEnum, vencimiento);

            // Llamar al service de seguro
            seguroService.insertar(seguro);

            System.out.println("✅ ÉXITO: Seguro creado con ID: " + seguro.getId());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un número entero válido.");
            scanner.nextLine();
        } catch (DateTimeParseException e) {
            System.err.println("❌ ERROR: Formato de fecha inválido. Use AAAA-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ ERROR: Cobertura inválida. Valores permitidos: RC, TERCEROS, TODO_RIESGO.");
        } catch (ServiceException | ValidationException e) {
            System.err.println("❌ ERROR AL CREAR: " + e.getMessage());
        }
    }

    private void menuEliminarSeguro() {
        System.out.println("\n*** ELIMINACIÓN LÓGICA DE SEGURO ***");
        System.out.println("¡Atención! Eliminar un seguro puede dejar un vehículo sin cobertura.");
        try {
            System.out.print("Ingrese ID del Seguro para dar de baja lógica: ");
            long id = scanner.nextLong();
            scanner.nextLine();

            // (Opcional: Deberías verificar si este seguro está en uso por un vehículo)
            seguroService.eliminar(id); // Implementado en el Service
            System.out.println("✅ ÉXITO: Seguro ID " + id + " marcado como eliminado.");

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            System.err.println("❌ ERROR al eliminar: " + e.getMessage());
        }
    }

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

            // ==========================================================
            // INICIO DE LA CORRECCIÓN
            // ==========================================================
            System.out.print("Cobertura (RC, TERCEROS, TODO_RIESGO): ");
            String coberturaStr = scanner.nextLine().toUpperCase(); // 1. Leer el String

            // 2. Convertir el String al Enum correspondiente
            // Esto lanzará IllegalArgumentException si el string no es válido
            Cobertura coberturaEnum = Cobertura.valueOf(coberturaStr);
            // ==========================================================
            // FIN DE LA CORRECCIÓN
            // ==========================================================

            System.out.print("Fecha de Vencimiento (AAAA-MM-DD): ");
            LocalDate vencimiento = LocalDate.parse(scanner.nextLine());

            // 3. Pasar el objeto Enum (coberturaEnum) al constructor
            SeguroVehicular seguro = new SeguroVehicular(null, false, aseguradora, nroPoliza, coberturaEnum, vencimiento);

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
            vehiculoService.insertar(vehiculo);

            System.out.println("✅ ÉXITO: Vehículo y Seguro creados transaccionalmente.");
            System.out.println("Vehículo ID: " + vehiculo.getId() + " / Póliza ID: " + vehiculo.getSeguro().getId());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un número entero válido (ID o Año).");
            scanner.nextLine();
        } catch (DateTimeParseException e) {
            System.err.println("❌ ERROR: Formato de fecha inválido. Use AAAA-MM-DD.");

            // ==========================================================
            // CORRECCIÓN: Añadir este bloque catch
            // ==========================================================
        } catch (IllegalArgumentException e) {
            System.err.println("❌ ERROR: Cobertura inválida. Valores permitidos: RC, TERCEROS, TODO_RIESGO.");
            // ==========================================================

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

            // ==========================================================
            // ¡CORRECCIÓN AQUÍ!
            // ==========================================================
            // 1. Verificamos si el servicio devolvió 'null'
            if (v == null) {
                // 2. Si es null, lanzamos la excepción que el 'catch' ya espera
                throw new ServiceException("No se encontró un Vehículo con ID " + id, null);
            }
            // ==========================================================

            // Si el código llega aquí, 'v' NO es null.
            System.out.println("\n✅ Vehículo encontrado:");
            System.out.println(v.toString());

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine();
        } catch (ServiceException e) {
            // 3. El 'throw' de arriba ahora será capturado aquí.
            System.err.println("❌ ERROR: " + e.getMessage());
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
     * Requisito: Actualización de Vehículo
     */
    private void menuActualizarVehiculo() {
        System.out.println("\n*** ACTUALIZAR VEHÍCULO ***");
        try {
            // 1. Pedir el ID y verificar que existe
            System.out.print("Ingrese el ID del vehículo a actualizar: ");
            long id = scanner.nextLong();
            scanner.nextLine(); // Consumir el salto de línea

            // 2. Buscar el vehículo. Si no existe, getById lanzará una excepción
            Vehiculo v = vehiculoService.getById(id);
            System.out.println("Datos actuales: " + v.toString());
            System.out.println("Ingrese los nuevos datos (deje en blanco para no modificar):");

            // 3. Pedir nuevos datos (condicionalmente)
            System.out.print("Nuevo Dominio (Actual: " + v.getDominio() + "): ");
            String dominio = scanner.nextLine().toUpperCase();
            if (!dominio.isEmpty()) {
                v.setDominio(dominio);
            }

            System.out.print("Nueva Marca (Actual: " + v.getMarca() + "): ");
            String marca = scanner.nextLine();
            if (!marca.isEmpty()) {
                v.setMarca(marca);
            }

            System.out.print("Nuevo Modelo (Actual: " + v.getModelo() + "): ");
            String modelo = scanner.nextLine();
            if (!modelo.isEmpty()) {
                v.setModelo(modelo);
            }

            System.out.print("Nuevo Nro. Chasis (Actual: " + v.getNroChasis() + "): ");
            String nroChasis = scanner.nextLine();
            if (!nroChasis.isEmpty()) {
                v.setNroChasis(nroChasis);
            }

            System.out.print("Nuevo Año (Actual: " + v.getAnio() + "): ");
            String anioStr = scanner.nextLine();
            if (!anioStr.isEmpty()) {
                v.setAnio(Integer.parseInt(anioStr)); // Puede lanzar NumberFormatException
            }

            // Nota: La lógica para actualizar el SEGURO es más compleja 
            // (requeriría buscar seguros) y se omite en este menú simple.
            // Este método actualiza los campos directos del vehículo.
            // 4. Llamar al Service para que aplique los cambios
            vehiculoService.actualizar(v);
            System.out.println("✅ ÉXITO: Vehículo ID " + id + " actualizado.");

        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Ingrese un ID numérico.");
            scanner.nextLine(); // Limpiar buffer
        } catch (NumberFormatException e) {
            System.err.println("❌ ERROR: El año ingresado no es un número válido.");
        } catch (ServiceException | ValidationException e) {
            // Captura "ID no encontrado" (de getById) o 
            // "Dominio duplicado" (de actualizar)
            System.err.println("❌ ERROR al actualizar: " + e.getMessage());
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
}
