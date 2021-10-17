/**
 * Tab1.java
 * @fecha: 08/10/2021
 * @autor: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero ejecuta la clase Tab1 que ofrece la vista, los campos de relleno y las
 * funcionalidades de los botones
 */

package com.example.abenest_upv.appsensorgas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tab1 extends Fragment {

    private EditText nombreDispositivo;
    private EditText macDispositivo;

    private TextView textNombreDispositivo;
    private TextView textNMacDispositivo;
    private TextView textUuidDispositivo;
    private TextView textFechaDispositivo;

    private Button buscarDispositivo;
    private Button detenerDispositivo;

    private Context context;
    private Intent intentServicioBLE = null;
    private  Intent intentServicioREST = null;

    private IntentFilter intentFilter;
    private Receptor receptor;

    private static final String ETIQUETA_LOG = ">>>>";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();

        intentFilter = new IntentFilter();
        intentFilter.addAction("Nueva_Medicion");
        receptor = new Receptor();
    }


    /**
     * onCreateView() carga la vista y los elementos que hay en tab1.xml
     *
     * onCreateView() ->
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);

        //Textviews donde se mostrará la información del dispositivo una vez se ha conectado
        textNombreDispositivo = v.findViewById(R.id.textNombreDelDispositivo);
        textNMacDispositivo = v.findViewById(R.id.textMacDelDispositivo);
        textUuidDispositivo = v.findViewById(R.id.textUuidDelDispositivo);
        textFechaDispositivo = v.findViewById(R.id.textFechaUltimoBeacon);

        //EditText donde poner los filtros para buscar nuestro dispositivo
        nombreDispositivo = v.findViewById(R.id.editNombre);
        macDispositivo = v.findViewById(R.id.editMAC);

        //Botones
        buscarDispositivo = v.findViewById(R.id.BotonBuscar);
        detenerDispositivo = v.findViewById(R.id.BotonFinalizar);

        intentServicioBLE = new Intent(context, ServicioEscuharBeacons.class);

        //Creamos el intent que permitirá lanzar el servicio ServicioLogicaFake
        intentServicioREST = new Intent(context, ServicioLogicaFake.class);
        context.startService(intentServicioREST);

        botonBuscarNuestroDispositivoBTLEPulsado();
        botonDetenerBusquedaDispositivosBTLEPulsado();

        return v;
    }

    /**
     * onResume() se ejecuta cuando este Fragment está en primer plano
     *
     * onResume() ->
     */
    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(receptor, intentFilter);
    }

    /**
     * botonBuscarNuestroDispositivoBTLEPulsado() se encarga de lanzar el servicio
     * ServicioEscucharBeacons pasando con un Intent el contenido de los EditText nombreDispositivo
     * y macDispositivo
     *
     * botonBuscarNuestroDispositivoBTLEPulsado() ->
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado() {

        //this.buscarEsteDispositivoBTLE( Utilidades.stringToUUID( "EPSG-GTI-PROY-3A" ) ); GTI-3A-ABENEST C5:BC:C9:2D:5C:D0


        buscarDispositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado" );

                //Asegurarnos que almenos tenga un campo para filtrar nuestro dispositivo:
                if(nombreDispositivo.getText().toString().isEmpty()  && macDispositivo.getText().toString().isEmpty() ){
                    Toast.makeText(context, "Rellene almenos uno de los dos campos" , Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(ETIQUETA_LOG, " Inicio del servicio" );
                //ServicioEscucharBeacons
                intentServicioBLE.putExtra("nombreDispositivo", nombreDispositivo.getText().toString());
                intentServicioBLE.putExtra("macDispositivo", macDispositivo.getText().toString());
                context.startService(intentServicioBLE);

            }
        });

        //this.buscarEsteDispositivoBTLE( "GTI-3A-ABENEST" , "C5:BC:C9:2D:5C:D0" );
    } // ()

    /**
     * botonDetenerBusquedaDispositivosBTLEPulsado() se encarga de parar el servicio
     * ServicioEscucharBeacons dejando de recibir beacons
     *
     * botonBuscarNuestroDispositivoBTLEPulsado() ->
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado() {
        detenerDispositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado" );

                try {
                    //Parar servicio escucha BLE
                    context.stopService(intentServicioBLE);

                    /*//Parar servicio rest
                    Intent stopIntent = new Intent();
                    stopIntent.setAction(ServicioLogicaFake.StopServicioREST.ACTION_STOP);
                    context.sendBroadcast(stopIntent);*/

                    Toast.makeText(context,"Servicio bluetooth detenido",
                            Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.d(ETIQUETA_LOG, "" + e );
                }

            }
        });
    } // ()


    /**
     * Clase Receptor
     * Receptor de mensajes broadcast de tipo "Nueva_Medicion"
     */
    private class Receptor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String m = intent.getStringExtra("Medicion");
            Gson gson = new Gson();
            Medicion medicion = gson.fromJson(m, Medicion.class);

            Log.d("INTENT", "" + medicion);

            textNombreDispositivo.setText(medicion.getNombreSensor());
            textNMacDispositivo.setText(medicion.getMacSensor());
            textUuidDispositivo.setText(medicion.getUuidSensor());

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm");
            Date resultado = new Date(medicion.getFecha());
            textFechaDispositivo.setText(resultado.toString());
        }
    }

}
