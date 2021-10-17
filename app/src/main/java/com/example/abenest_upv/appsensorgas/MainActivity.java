/**
 * MainActivity.java
 * @fecha: 07/10/2021
 * @autor: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero abre la primera actividad de la aplicación.
 */

package com.example.abenest_upv.appsensorgas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private String[] nombres = new String[]{"Buscar dispositivo BLE","Listado Últimas Mediciones"};
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    /**
     * Método onCreate se ejecuta antes de iniciar la actividad MainActivity
     *
     * savedInstanceState:Bundle -> onCreate() ->
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Comprobamos si la app tiene los permisos para utilizar el bluetooth
        permisosBluetooth();

        //Referenciar el objeto TabLayout com la vista y añadir los tabs
        //Funcionalidad para crear los TabLayouts
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setText(nombres[position]);
                    }
                }
        ).attach();
    }

    /**
     * Método permisosBluetooth que comprueba la app tiene ya los permisos para utilizar
     * el bluetooth del dispositivo y en caso de no tenerlos los pedirá.
     *
     * permisosBluetooth() ->
     *
     */
    private void permisosBluetooth(){

        //Comprobamos si no tenemos los permisos...
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        }
        else {
            Log.d("PERMISOS BLUETOOTH", " Parece que YA tengo los permisos necesarios !!!!");

        }
    }


    /**
     * Método onRequestPermissionsResult que comprueba el resultado de pedir permiso para utilizar
     * el bluetooth del dispositivo para la aplicación y en caso afirmativo, los concederá.
     *
     *
     *requestCode:Z,
     *permissions:[Texto],
     *grantResults: [Z] -> onRequestPermissionsResult() ->
     *
     * @param requestCode Valor entero
     * @param permissions
     * @param grantResults
     *
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("PERMISOS BLUETOOTH", " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {

                    Log.d("PERMISOS BLUETOOTH", " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
    }
}