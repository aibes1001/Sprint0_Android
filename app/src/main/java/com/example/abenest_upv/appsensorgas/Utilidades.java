/**
 * Utilidades.java
 * @fecha: 07/10/2021
 * @autor: Jordi Bataller i Mascarell
 * @Modificado: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero contiene la clase Utilidades que son una serie de métodos destinados a la conversión
 * de un tipo de datos a otro. Servirán sobretodo para poder manejar los datos resultantes de la
 * información recibida vía bluetooth.
 */

package com.example.abenest_upv.appsensorgas;

import java.math.BigInteger;

public class Utilidades {

    /**
     * El método bytesToString se encarga de convertir una lista de bytes a una cadena de caracteres.
     *
     * [bytes] -> bytesToString()
     * Texto <-
     *
     * @param bytes Se pasa un array de bytes.
     *
     * @returns Devuelve la cadena de texto resultado de la conversión de cada byte a un
     *          valor ASCII.
     */
    public static String bytesToString( byte[] bytes ) {
        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append( (char) b );
        }
        return sb.toString();
    }

    /**
     * El método bytesToInt se encarga de convertir una lista de bytes a un número entero.
     *
     * [bytes] -> bytesToInt()
     * Z <-
     *
     * @param bytes Se pasa un array de bytes.
     *
     * @returns Devuelve un número entero resultado de la conversión de los bytes a número decimal.
     */
    public static int bytesToInt( byte[] bytes ) {
        return new BigInteger(bytes).intValue();
    }


    /**
     * El método bytesToHexString se encarga de convertir una lista de bytes a un número hexadecimal
     * en formato de cadena de caracteres.
     *
     * [bytes] -> bytesToHexString()
     * Texto <-
     *
     * @param bytes Se pasa un array de bytes.
     *
     * @returns Devuelve una cadena de texto con un número hexadecimal resultado de la conversión
     *          de los bytes a formato hexadecimal y después convertirlo a texto.
     */
    public static String bytesToHexString( byte[] bytes ) {

        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------


