package com.example.abenest_upv.appsensorgas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class ServicioEscuharBeacons extends Service {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    private List<Medicion> mediciones = new ArrayList<>();

    private String nombreBLE, macBLE = "";

    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @Override
    public int onStartCommand( Intent elIntent, int losFlags, int startId) {
        // creo que este método no es necesario usarlo. Lo ejecuta el thread principal !!!
        super.onStartCommand( elIntent, losFlags, startId );

        nombreBLE = elIntent.getStringExtra("nombreDispositivo");
        macBLE = elIntent.getStringExtra("macDispositivo");

        Log.d(ETIQUETA_LOG, "Que retorna: "+ nombreBLE + "     " + macBLE);
        crearNotificaciónSegundoPlano();

        try{
            inicializarBlueTooth();
            buscarEsteDispositivoBTLE(nombreBLE, macBLE);
        }catch (Exception e){
            Toast.makeText(this, "Compruebe que tiene el bluetooth activado",
                    Toast.LENGTH_SHORT).show();
        }


        Toast.makeText(this,"Servicio Bluetooth arrancado ",
                Toast.LENGTH_SHORT).show();

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onStartCommand : empieza: thread=" + Thread.currentThread().getId() );//*/

        return START_STICKY;
    } // ()

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
    private void crearNotificaciónSegundoPlano(){
        //Crear la notificació
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(this, CANAL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Sensor Bluetooth escuchando")
                        .setContentText("Notificación de sensor de bluetooth activo");
        //Llançar l'aplicació des de la notificació
        PendingIntent intencionPendiente = PendingIntent.getActivity(
                this, 0, new Intent(this, Tab1.class), 0);
        notificacion.setContentIntent(intencionPendiente);


        //Servici en primer pla (DECLARAR EN EL MANIFEST)
        //startForeground(NOTIFICACION_ID, notificacion.build());

        //Servici en segon pla
        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
    }


    //------------------------------------------------------------
    //Accions per a donar per acabat el servici
    //-----------------------------------------------------------
    @Override public void onDestroy() {
        this.detenerBusquedaDispositivosBTLE();
        Toast.makeText(this,"Servicio bluetooth detenido",
                Toast.LENGTH_SHORT).show();
        notificationManager.cancel(NOTIFICACION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // --------------------------------------------------------------
    //
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if ( this.callbackDelEscaneo == null ) {
            return;
        }

        this.elEscanner.stopScan( this.callbackDelEscaneo );
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado, final String dispositivoMAC ) {

        this.detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");
                mostrarInformacionDispositivoBTLE( resultado );
                guardarNuevaMedida(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {

                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");
            }
        };

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado );
        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): también escanear buscando: " + dispositivoMAC );
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters = (ArrayList<ScanFilter>) filtrarDispositivos(dispositivoBuscado, dispositivoMAC);
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
        this.elEscanner.startScan(filters, settings, this.callbackDelEscaneo );
    } // ()



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private List<ScanFilter> filtrarDispositivos(String nombreDispositivo, String macDispositivo){
        ArrayList<ScanFilter> filtros = new ArrayList<ScanFilter>();

        if(nombreDispositivo.length() > 0){
            ScanFilter scanFilterName = new ScanFilter.Builder().setDeviceName(nombreDispositivo).build();
            filtros.add(scanFilterName);
        }
        if(macDispositivo.length() > 0){
            ScanFilter scanFilterMAC = new ScanFilter.Builder().setDeviceAddress(macDispositivo).build();
            filtros.add(scanFilterMAC);
        }

        return filtros;
    }//()


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void mostrarInformacionDispositivoBTLE( ScanResult resultado ) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()

    private void guardarNuevaMedida(ScanResult result){

        Medicion medicion = new Medicion();
        byte[] bytes = result.getScanRecord().getBytes();
        TramaIBeacon tramaIBeacon = new TramaIBeacon(bytes);

        BluetoothDevice bluetoothDevice = result.getDevice();

        medicion.setNombreSensor(bluetoothDevice.getName());
        medicion.setMacSensor(bluetoothDevice.getAddress());
        medicion.setFecha();
        medicion.setUuidSensor(Utilidades.bytesToString(tramaIBeacon.getUUID()));
        medicion.setMedida(Utilidades.bytesToInt(tramaIBeacon.getMinor()));

        //Dins de Major agafar el primer byte i transformar-lo a int per a determinar el tipus de mesura
        byte[] tipoMedida =  Arrays.copyOfRange(tramaIBeacon.getMajor(), 0, 1 );
        medicion.setTipo(Utilidades.bytesToInt(tipoMedida));

        //De moment es posen lat i log del Campus de Gandia
        medicion.setLatitud(38.995860);
        medicion.setLongitud(-0.166152);

        //Afegim a l'array si  la mesura no està incorporada
        if(!comprobarSiYaEstaLaMedicion(medicion)){
            mediciones.add(medicion);
            pasarMedicion(medicion);
            enviarUltimaMedicion();
        }

    }

    private boolean comprobarSiYaEstaLaMedicion(Medicion m){
        if(this.mediciones.size() == 0){
            return false;
        }

        for (Medicion medicion : this.mediciones){
            if(m.getTipo().equals(medicion.getTipo()) && (m.getFecha() < medicion.getFecha()+5000)){
                return true;
            }
        }

        return false;
    }

    public void verArrayMedidas(View v){
        Log.d(ETIQUETA_LOG, " Longitud de la lista  = " + this.mediciones.size());
        for (Medicion m : this.mediciones){
            Log.d(ETIQUETA_LOG, " ****************************************************");
            Log.d(ETIQUETA_LOG, " nombre  = " + m.getNombreSensor());
            Log.d(ETIQUETA_LOG, " mac  = " + m.getMacSensor());
            Log.d(ETIQUETA_LOG, " uuid  = " + m.getUuidSensor());
            Log.d(ETIQUETA_LOG, " fecha  = " + m.getFecha());
            Log.d(ETIQUETA_LOG, " tipo  = " + m.getTipo());
            Log.d(ETIQUETA_LOG, " medida  = " + m.getMedida());
            Log.d(ETIQUETA_LOG, " lat  = " + m.getLatitud());
            Log.d(ETIQUETA_LOG, " long  = " + m.getLongitud());
            Log.d(ETIQUETA_LOG, " ****************************************************");
        }
    }


    //Mètode per a actualitzar les dades agafades
    public void enviarUltimaMedicion(){
        Log.d(ETIQUETA_LOG, "LONGITUD ARRAY: " + this.mediciones.get(this.mediciones.size() - 1));
        Intent i = new Intent();
        i.setAction("Datos_Ultimo_Beacon");
        i.putExtra("Medicion", this.mediciones.get(this.mediciones.size() - 1).toString());
        sendBroadcast(i);
    }

    public void pasarMedicion(Medicion m){
        Intent i = new Intent();
        i.setAction("Nueva_Medicion");
        i.putExtra("Medicion", m.toString());
        sendBroadcast(i);
    }



} // class
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------