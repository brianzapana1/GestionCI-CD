package com.inventario.utils;

import com.inventario.models.Usuario;

public class SessionManager {
    private static Usuario usuarioActual;
    private static String rolActual; // 🔹 Guardamos el nombre del rol

    public static void iniciarSesion(Usuario usuario, String nombreRol) {
        usuarioActual = usuario;
        rolActual = nombreRol; // 🔹 Guardamos el nombre del rol
        System.out.println("✅ Sesión iniciada con el usuario: " + usuario.getNombre() + " | Rol: " + rolActual);
    }

    public static void cerrarSesion() {
        System.out.println("🔴 Sesión cerrada.");
        usuarioActual = null;
        rolActual = null;
    }

    public static boolean estaAutenticado() {
        return usuarioActual != null;
    }

    public static String getRol() {
        System.out.println("🔍 Consultando rol de usuario: " + rolActual);
        return rolActual; // 🔹 Devuelve el nombre del rol correctamente
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
