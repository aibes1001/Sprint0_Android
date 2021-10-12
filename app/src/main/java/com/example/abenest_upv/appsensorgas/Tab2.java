package com.example.abenest_upv.appsensorgas;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Tab2 extends Fragment {
    private RecyclerView recyclerView;
    private Adaptador adaptador;
    private Context context;
    private ArrayList<Medicion> mediciones;
    private IntentFilter intentFilter;
    private ReceptorGetMedicion receptor;

    private static ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Per a agafar el context
        context = getActivity();
        intentFilter = new IntentFilter();
        intentFilter.addAction("GetMediciones");
        receptor = new ReceptorGetMedicion();

        mediciones = new ArrayList<>();
        context.registerReceiver(receptor, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Obteniendo las últimas mediciones");
        progressDialog.show();
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adaptador = new Adaptador(context, mediciones);
        recyclerView.setAdapter(adaptador);

        //Forzamos la obtención de los datos del servidor para no esperar el tiempo que esté
        //durmiendo el hilo del intentService ServicioPeticionesREST
        Intent i = new Intent();
        i.setAction("Iniciar_GET_Mediciones");
        context.sendBroadcast(i);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class ReceptorGetMedicion extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mediciones.clear();

            String recibido = intent.getStringExtra("Mediciones");

            if (recibido.length() > 0){
                Gson gson = new Gson();
                Medicion[] mediciones = gson.fromJson(recibido, Medicion[].class);

                for (Medicion m : mediciones) {
                    Log.d("RESULTADO DE MEDICIONES", "OBJETO = "  + m);
                    Tab2.this.mediciones.add(m);
                }

                // notify adapter
                adaptador.notifyDataSetChanged();
            }


            //Quan acaba de carregar
            progressDialog.dismiss();
            Log.d("RESULTADO DE MEDICIONES", "LONGITUD LISTA TAB2 = "  + Tab2.this.mediciones.size());
            Log.d("RESULTADO DE MEDICIONES", "LISTA TAB2 = "  + Tab2.this.mediciones);

        }
    }
}