/**
 * PagerAdapter.java
 * @fecha: 08/10/2021
 * @autor: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero permite manejar los Fragments de la vista de la app. En este caso se crearán dos Fragments:
 * - El primero servirá para iniciar la búsqueda del dispositivo BLE de donde deseemos recibir
 *   los Beacons, así como mostrar la información del dispositivo encontrado.
 * - El segundo servirá para mostrar una lista de las 10 últimas mediciones obtenidas del servidor.
 */

package com.example.abenest_upv.appsensorgas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Clase PagerAdapter
 * Clase para manejar varias pestañas (Tabs) para mostrar diferentes Fragments
 */
public class PagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor de la clase PagerAdapter.
     *
     * activity:FragmentActivity -> Constructor() ->
     *
     * @param activity Se pasa un objeto de FragmentActivity.
     *
     */
    public PagerAdapter(FragmentActivity activity){
        super(activity);
    }


    /**
     * El método getItemCount devuelve el número de fragments que deseamos crear en la app
     *
     * N <- getItemCount()
     *
     * @return N Número de Fragments que se crearán.
     *
     */
    @Override
    public int getItemCount() {
        return 2;
    }


    /**
     * El método createFragment devuelve el Fragment que deseamos a partir de la posición
     *
     * position: N -> createFragment() <-
     * Fragment <-
     *
     * @param position Número entero positivo que pasa la posición
     * @return Fragment Devuelve el fragment 1 o 2 según la posición.
     *
     */
    @Override @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Tab1();
            case 1: return new Tab2();
        }
        return null;
    }
}

