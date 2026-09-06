package usuario.back.usuarios_service.model;

/**
 * Roles del sistema, según la especificación del caso Sonido Vivo:
 * - ADMINISTRADOR: acceso total.
 * - VENDEDOR: solo ve listado/detalle de productos y listado/detalle de órdenes.
 * - CLIENTE: solo accede a la tienda pública.
 */
public enum Tipousuario {
    ADMINISTRADOR,
    VENDEDOR,
    CLIENTE
}