/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

public class ServiceException extends RuntimeException {
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}