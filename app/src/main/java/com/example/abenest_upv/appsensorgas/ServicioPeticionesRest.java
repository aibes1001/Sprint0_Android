package com.example.abenest_upv.appsensorgas;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class ServicioPeticionesRest extends IntentService {
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private int tiempoDeEsperaHilo = 30000;

    private int tiempoDeEsperaEnvioPost = 60000;
    private long tiempo;

    private boolean seguir = true;

    private List<String> medicionesString = new ArrayList<>();

    IntentFilter filterStopServicio, filterRecepcionMedicion, filterIniciarGetMediciones;
    StopServicioREST receiverStop;
    ReceptorMediciones receptorMedicion;
    InicializadorGetMediciones inicializadorGetMediciones;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public ServicioPeticionesRest(  ) {
        super("ServicioPeticionesRest");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        filterStopServicio = new IntentFilter(StopServicioREST.ACTION_STOP);
        filterStopServicio.addCategory(Intent.CATEGORY_DEFAULT);
        receiverStop = new StopServicioREST();
        registerReceiver(receiverStop, filterStopServicio);

        filterRecepcionMedicion  = new IntentFilter();
        filterRecepcionMedicion.addAction("Nueva_Medicion");
        receptorMedicion = new ReceptorMediciones();
        registerReceiver(receptorMedicion, filterRecepcionMedicion);

        filterIniciarGetMediciones = new IntentFilter();
        filterIniciarGetMediciones.addAction("Iniciar_GET_Mediciones");
        inicializadorGetMediciones = new InicializadorGetMediciones();
        registerReceiver(inicializadorGetMediciones, filterIniciarGetMediciones);

        tiempo = currentTimeMillis();
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void parar () {
        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.parar() " );
        if ( this.seguir == false ) {
            return;
        }
        this.seguir = false;
        this.stopSelf();
        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.parar() : acaba " );

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void onDestroy() {
        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onDestroy() " );
        this.parar(); // posiblemente no haga falta, si stopService() ya se carga el servicio y su worker thread
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        this.seguir = true;

        // esto lo ejecuta un WORKER THREAD !

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: empieza : thread=" + Thread.currentThread().getId() );

        try {

            while ( this.seguir ) {
                Log.d(ETIQUETA_LOG, "LONGITUD: " + medicionesString.size());

                //Enviar mediciones cada 60s al servidor
                if(currentTimeMillis() > tiempo + tiempoDeEsperaEnvioPost){
                    enviarMedidasPost();
                    tiempo = currentTimeMillis();
                }

                recibirMedidasGET();

                Thread.sleep(tiempoDeEsperaHilo);
            }

            Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent : tarea terminada ( tras while(true) )" );


        } catch (InterruptedException e) {
            // Restore interrupt status.
            Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleItent: problema con el thread");

            Thread.currentThread().interrupt();
        }

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleItent: termina");

        unregisterReceiver(receiverStop);
        unregisterReceiver(receptorMedicion);
        this.stopSelf();

    }

    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    private void enviarMedidasPost() {

        if(medicionesString.size() > 0){
            PeticionarioREST elPeticionario = new PeticionarioREST();

            JSONArray jsArray = new JSONArray(medicionesString);
            Log.d(ETIQUETA_LOG, "" + jsArray);
            medicionesString.clear();
            elPeticionario.hacerPeticionREST("POST",  "http://192.168.0.107:8080/medicion",
                    String.valueOf(jsArray),
                    new PeticionarioREST.RespuestaREST () {
                        @Override
                        public void callback(int codigo, String cuerpo) {
                            Log.d(ETIQUETA_LOG, "codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                        }
                    }
            );
        }

    }

    private void recibirMedidasGET(){
        PeticionarioREST elPeticionario = new PeticionarioREST();

        elPeticionario.hacerPeticionREST("GET",  "http://192.168.0.107:8080/ultimasMediciones/10", null,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d(ETIQUETA_LOG, "codigo respuesta= " + codigo + " <-> \n" + cuerpo);

                        Intent i = new Intent();
                        i.setAction("GetMediciones");
                        i.putExtra("Mediciones", cuerpo);
                        sendBroadcast(i);

                    }
                }
        );
    }

    public class StopServicioREST extends BroadcastReceiver {

        public static final String ACTION_STOP = "stop";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ETIQUETA_LOG", " ServicioEscucharBeacons.onHandleItent: termina");
            seguir = false;
        }
    }

    private class ReceptorMediciones extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String m = intent.getStringExtra("Medicion");
            Gson g = new Gson();
            Medicion medicion = g.fromJson(m, Medicion.class);
            String str =  g.toJson(medicion);
            Log.d(ETIQUETA_LOG, "" + str);
            medicionesString.add(str);

            Log.d(ETIQUETA_LOG, "" + medicionesString.size());

        }
    }

    private class InicializadorGetMediciones extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ETIQUETA_LOG, "INICIALIZADOR OBTENCIÓN DE MEDIDASSSSSSSSSSSSSSSSSSSSS");

            recibirMedidasGET();

        }
    }
}
