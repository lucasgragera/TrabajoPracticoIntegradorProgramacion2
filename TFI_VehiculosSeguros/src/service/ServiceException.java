package service;

/**
 * Excepción personalizada para la capa de Servicio.
 * Permite encapsular errores de negocio (validación) o de la capa DAO (SQL)
 * y enviarlos de manera controlada al AppMenu.
 */
public class ServiceException extends Exception {
    
    // Constructor que acepta solo un mensaje
    public ServiceException(String message) {
        super(message);
    }
    
    // Constructor que acepta un mensaje y la causa original (ej: SQLException)
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}