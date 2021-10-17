/**
 * ServicioEscucharBeacons.java
 * @fecha: 07/10/2021
 * @autor: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero contiene la clase ServicioEscuharBeacons, para ejecutar un servicio que permitirá
 * recibir información vía bluetooth y gestionar los datos recibidos.
 */


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

    /**
     * El método onStartCommand se ejecuta al iniciar el servicio
     *
     * elIntent:Intent,
     * losFlags: Z,
     * startId:Z -> onStartCommand() ->
     * Z <-
     *
     * @param elIntent
     * @param losFlags
     * @param startId
     *
     * @return N
     */

    @Override
    public int onStartCommand( Intent elIntent, int losFlags, int startId) {

        super.onStartCommand( elIntent, losFlags, startId );

        nombreBLE = elIntent.getStringExtra("nombreDispositivo");
        macBLE = elIntent.getStringExtra("macDispositivo");

        Log.d(ETIQUETA_LOG, "Que retorna: "+ nombreBLE + "     " + macBLE);
        crearNotificaciónSegundoPlano();

        try{
            //Inicializamos el bluetooth
            inicializarBlueTooth();

            //Buscamos el dispositivo que deseamos a través de su nombre y/o bien su MAC
            buscarEsteDispositivoBTLE(nombreBLE, macBLE);
        }catch (Exception e){
            //En caso de haber algún error como que no esté aún activado el bluetooth mostramos un toast
            Toast.makeText(this, "Activado bluetooth del dispositivo",
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this,"Servicio Bluetooth arrancado ",
                Toast.LENGTH_SHORT).show();

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onStartCommand : empieza: thread=" + Thread.currentThread().getId() );//*/

        return START_STICKY;
    } // ()

    /**
     * El método crearNotificaciónSegundoPlano crea una notificación que indica que el servicio
     * está en marcha.
     *
     * crearNotificacionSegundoPlano() <-
     *
     */
    private void crearNotificaciónSegundoPlano(){
        //Crear la notificación
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
        /*PendingIntent intencionPendiente = PendingIntent.getActivity(
                this, 0, new Intent(this, Tab1.class), 0);
        notificacion.setContentIntent(intencionPendiente);*/

        //Servici en primer pla (DECLARAR EN EL MANIFEST)
        //startForeground(NOTIFICACION_ID, notificacion.build());

        //Servici en segon pla
        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
    }


    /**
     * El método onDestroy ejecuta las acciones para dar por finalizado el servicio
     * está en marcha.
     *
     * onDestroy() ->
     *
     */
    @Override public void onDestroy() {
        this.detenerBusquedaDispositivosBTLE();
    }

    /**
     * Este método aquí no hace nada pero es necesario implementarlo en la clase
     *
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * El método inicializarBlueTooth habilita el bluetooth y prepara para poder recibir beacons
     *
     * inicializarBluetooth() ->
     *
     */
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

    /**
     * El método detenerBusquedaDispositivosBTLE para la escucha de beacons de los dispositivos
     *
     * detenerBusquedaDispositivosBTLE() ->
     *
     */
    private void detenerBusquedaDispositivosBTLE() {

        if ( this.callbackDelEscaneo == null ) {
            return;
        }

        try{
            this.elEscanner.stopScan( this.callbackDelEscaneo );
            this.callbackDelEscaneo = null;

            notificationManager.cancel(NOTIFICACION_ID);

        }catch (Exception e){}

    } // ()

    /**
     * El método buscarEsteDispositivoBTLE inicia la búsqueda de un dispositivo concreto filtrando
     * por su nombre y/o MAC
     *
     * dispositivoBuscado: Texto,
     * dispositivoMac: Texto -> buscarEsteDispositivoBTLE() ->
     *
     * @param dispositivoBuscado Texto con el nombre del dispositivo que se desea buscar
     * @param dispositivoMAC Texto con la MAC del dispositivo que se desea buscar
     *
     */
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

        //Configuramos los filtros de búsqueda del dispositivo
        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters = (ArrayList<ScanFilter>) filtrarDispositivos(dispositivoBuscado, dispositivoMAC);

        //Configuramos el tipo de búsqueda
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();

        //Iniciamos el escaneo de dispositivos
        this.elEscanner.startScan(filters, settings, this.callbackDelEscaneo );
    } // ()



    /**
     * El método filtrarDispositivos incluimos los tipos de filtros que se quieren incluir para
     * buscar nuestro dispositivo bluetooth
     *
     * dispositivoBuscado: Texto,
     * dispositivoMac: Texto -> filtrarDispositivos() ->
     * [ScanFilter] <-
     *
     * @param nombreDispositivo Texto con el nombre del dispositivo que se desea buscar
     * @param macDispositivo Texto con la MAC del dispositivo que se desea buscar
     *
     * @return filtos Lista de ScanFilter con los filtros que queremos para buscar
     */
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


    /**
     * El método mostrarInformacionDispositivoBTLE muestra en el LogCat la información recibida
     * de cada beacon
     *
     * resultado: ScanResult -> mostrarInformacionDispositivo BTLE() <-
     *
     * @param resultado ScanResult con la información de cada beacon recibido
     *
     */
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

    /**
     * El método guardarNuevaMedida crea un objeto de tipo Medicion a partir de la información
     * otenida en cada beacon. Posteriormente incluye la medición en un array de mediciones
     * si esta aún no ha sido incluída ya que se pueden llegar a recibir varios beacons de la
     * misma medición.
     *
     * result: ScanResult -> guardarNuevaMedida() ->
     *
     * @param result ScanResult con la información de cada beacon recibido
     *
     */
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

        //Dentro de Major coger el byte más significativo para determinar el tipo de medida
        byte[] tipoMedida =  Arrays.copyOfRange(tramaIBeacon.getMajor(), 0, 1 );
        medicion.setTipo(Utilidades.bytesToInt(tipoMedida));

        //De momento poner lat y log del Campus de Gandia
        medicion.setLatitud(38.995860);
        medicion.setLongitud(-0.166152);

        //Añadimos al array si la medición no esta ya, y la enviamos de forma broadcast
        if(!comprobarSiYaEstaLaMedicion(medicion)){
            mediciones.add(medicion);
            pasarMedicion(medicion);
        }

    }


    /**
     * El método comprobarSiYaEstaLaMedicion se encarga de comprobar si una medición ya está dentro
     * de la lista de mediciones. Lo comprueba a través del tipo de medición y de la fecha de medición.
     *
     * m: Medicion -> comprobarSiYaEstaLaMedida() <-
     *     V/F <-
     *
     * @param m Objeto Medición que se quiere comprobar
     *
     * @return V/F
     *
     */
    private boolean comprobarSiYaEstaLaMedicion(Medicion m){
        if(this.mediciones.size() == 0){
            return false;
        }

        for (Medicion medicion : this.mediciones){
            //Si el tipo de medición coincide y la fecha se diferencia por menos de 5 segundos, la
            // medición ya está incluida y devuelve True
            if(m.getTipo().equals(medicion.getTipo()) && (m.getFecha() < medicion.getFecha()+5000)){
                return true;
            }
        }

        return false;
    }

    /**
     * El método verArrayMedidas muestra en el LogCat las mediciones dentro de la lista
     *
     * verArrayMedidas() <-
     *
     */
    public void verArrayMedidas(){
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


    /**
     * El método pasarMedicion envía un intent de forma broadcast dentro de la aplicación
     * con la última medición
     *
     *  medicion: Medicion -> pasarMedicion() <-
     *
     */
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