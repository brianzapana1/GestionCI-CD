package com.inventario.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hashear la contraseña
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Verificar si una contraseña coincide con su hash
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
