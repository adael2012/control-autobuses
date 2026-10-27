package com.itla.autobuses.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class Seguridad {

    private static final int ITERACIONES = 120_000;
    private static final int BITS = 256;

    private Seguridad() { }

    public static String generarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String clave, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(),
                    Base64.getDecoder().decode(salt), ITERACIONES, BITS);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(h);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }

    public static boolean verificar(String clave, String salt, String hashGuardado) {
        return MessageDigest.isEqual(
                hash(clave, salt).getBytes(), hashGuardado.getBytes());
    }
}
