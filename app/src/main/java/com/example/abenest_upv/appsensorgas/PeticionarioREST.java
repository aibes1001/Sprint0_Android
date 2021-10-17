/**
 * PeticionarioREST.java
 * @fecha: 07/10/2021
 * @autor: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero contiene la clase PeticionarioREST que permitirá ejecutar peticiones REST para enviar
 * y recibir información del servidor.
 */

package com.example.abenest_upv.appsensorgas;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase PeticionarioREST
 * Clase para establecer una comunicacion REST con el servidor de forma asíncrona
 */
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    /**
     *
     * Interfaz RespuestaREST
     *
     */
    public interface RespuestaREST {
        void callback(int codigo, String cuerpo);
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    private String elMetodo;
    private String urlDestino;
    private String elCuerpo = null;
    private RespuestaREST laRespuesta;

    private int codigoRespuesta;
    private String cuerpoRespuesta = "";


    /**
     * Método hacerPeticionREST donde se pasa los parámetros necesarios para la comunicación REST
     * y ejecuta en un nuevo hilo (con el método doInBackground) dicha comunicación.
     *
     * metodo: Texto,
     * urlDestino: Texto,
     * cuerpo: Texto,
     * laRespuesta: RespuestaREST -> hacerPeticionREST() ->
     *
     * @param metodo Texto que indica el tipo de petición REST que se va a realizar
     * @param urlDestino Texto con la dirección del servidor web
     * @param cuerpo Texto que se enviará con la petición (sólo con el método POST; con GET cuerpo = null)
     * @param laRespuesta Objeto de tipo RespuestaREST que debe implementar esta clase
     *
     */
    public void hacerPeticionREST (String metodo, String urlDestino, String cuerpo, RespuestaREST  laRespuesta) {
        this.elMetodo = metodo;
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        this.laRespuesta = laRespuesta;

        this.execute(); // otro thread ejecutará doInBackground()
    }

    /**
     * Constructor por defecto.
     */
    public PeticionarioREST() {
        Log.d("clienterestandroid", "constructor()");
    }


    /**
     * Método doInBackground ejecuta en un hilo nuevo en segundo plano la petición REST a partir
     * de los parámetros guardados en esta clase, y la respuesta que recibe
     *
     * VF <- doInBackground() <-
     *
     * @Return V/F Devuelve un booleano dependiendo si la petición se ha ejecutado bien o ha habido
     *         alguna excepción.
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("clienterestandroid", "doInBackground()");

        try {
            Log.d("clienterestandroid", "doInBackground() me conecto a >" + urlDestino + "< " + elCuerpo);

            URL url = new URL(this.urlDestino);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestMethod(this.elMetodo);
            // connection.setRequestProperty("Accept", "*/*);

            // connection.setUseCaches(false);
            connection.setDoInput(true);

            //En caso de ser una petición POST y que tenga cuerpo...
            if ( ! this.elMetodo.equals("GET") && this.elCuerpo != null ) {
                Log.d("clienterestandroid", "doInBackground(): no es get, pongo cuerpo");
                connection.setDoOutput(true);
                // si no es GET, pongo el cuerpo que me den en la petición
                OutputStream dos = new DataOutputStream (connection.getOutputStream());
                dos.write(this.elCuerpo.toString().getBytes());
                dos.flush();
                dos.close();
            }

            // Ya he enviado la petición
            Log.d("clienterestandroid", "doInBackground(): peticin enviada ");

            // Ahora obtengo la respuesta
            int rc = connection.getResponseCode();
            String rm = connection.getResponseMessage();
            String respuesta = "" + rc + " : " + rm;
            Log.d("clienterestandroid", "doInBackground() recibo respuesta = " + respuesta);
            this.codigoRespuesta = rc;

            try {

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                Log.d("clienterestandroid", "leyendo cuerpo");
                StringBuilder acumulador = new StringBuilder ();
                String linea;
                while ( (linea = br.readLine()) != null) {
                    Log.d("clienterestandroid", linea);
                    acumulador.append(linea);
                }
                Log.d("clienterestandroid", "FIN leyendo cuerpo");

                this.cuerpoRespuesta = acumulador.toString();
                Log.d("clienterestandroid", "cuerpo recibido=" + this.cuerpoRespuesta);

                connection.disconnect();

            } catch (IOException ex) {
                // dispara excepcin cuando la respuesta REST no tiene cuerpo y yo intento getInputStream()
                Log.d("clienterestandroid", "doInBackground() : parece que no hay cuerpo en la respuesta");
            }

            return true; // doInBackground() termina bien

        } catch (Exception ex) {
            Log.d("clienterestandroid", "doInBackground(): ocurrio alguna otra excepcion: " + ex.getMessage());
        }

        return false; // doInBackground() NO termina bien
    } // ()

    /**
     * Método onPostExecute
     *
     * V/F -> onPostExecute() ->
     *
     * @param comoFue Booleano resultante del método doInBackground.
     *
     */
    protected void onPostExecute(Boolean comoFue) {
        // llamado tras doInBackground()
        Log.d("clienterestandroid", "onPostExecute() comoFue = " + comoFue);
        this.laRespuesta.callback(this.codigoRespuesta, this.cuerpoRespuesta);
    }



} // class