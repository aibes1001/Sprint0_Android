package com.example.abenest_upv.appsensorgas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder>{
    private LayoutInflater inflador;
    private List<Medicion> lista;


    //Class ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvalor, txtFecha, txtTipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.textFechaRV);
            txtvalor = itemView.findViewById(R.id.textValorRV);
            txtTipo = itemView.findViewById(R.id.textTipoRV);
        }

        public void asignarDatos(Medicion medicion) {
            //posar en cada textview les dades de cada medició
            txtvalor.setText("" + medicion.getMedida());
            txtTipo.setText(medicion.getTipo());

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm");
            Date resultado = new Date(medicion.getFecha());
            txtFecha.setText(resultado.toString());

        }
    }// Class()

    //Constructor de la classe adaptador
    public Adaptador(Context context, List<Medicion> lista) {
        this.lista = lista;
        inflador = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("Nums", ""+this.lista);
    }


    //Crear cada element del recycler. S'ha de posar el layout on està la vista de cada element de la llista
    @NonNull
    @Override
    public Adaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elementos_recyclerview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptador.ViewHolder holder, int position) {
        holder.asignarDatos(lista.get(position));
    }

    //Posar el número d'elements de la llista
    @Override
    public int getItemCount() {
        return lista.size();
    }


}
