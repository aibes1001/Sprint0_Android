/**
 * Adaptador.java
 * @fecha: 08/10/2021
 * @autor: Aitor Benítez Estruch
 *
 * @Descripcion:
 * Este fichero se encarga de la vista de los datos para mostrarlos en un RecyclerView.
 */

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

/**
 * Clase Adaptador
 * Clase que carga el contenedor donde se mostrará los elementos de un RecyclerView
 */
public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder>{
    private LayoutInflater inflador;
    private List<Medicion> lista;


    /**
     * Clase ViewHolder
     * Clase donde se describe una vista de elementos y asigna a estos unos datos que se mostrarán
     * en la aplicación.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvalor, txtFecha, txtTipo;

        /**
         * Constructor de la clase ViewHolder. Asigna a cada TextView que tiene como campo privado
         * el elemento de la Vista que se le pasa como parámetro.
         *
         * itemView : View -> Constructor() ->
         *
         * @param itemView Se pasa un objeto de tipo View.
         *
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.textFechaRV);
            txtvalor = itemView.findViewById(R.id.textValorRV);
            txtTipo = itemView.findViewById(R.id.textTipoRV);
        }

        /**
         * El método asignarDatos asigna a cada TextView el valor que se quiere mostrar del objeto
         * Medicion que se le pasa.
         *
         * medicion : Medicion -> asignarDatos() ->
         *
         * @param medicion Se pasa un objeto de tipo Medicion.
         *
         */
        public void asignarDatos(Medicion medicion) {
            //Poner en cada textView que tiene el layout elementos_recyclerview.xml
            // el dato que corresponda.
            txtvalor.setText("" + medicion.getMedida());
            txtTipo.setText(medicion.getTipo());

            //Transformar la fecha en milisegundos a formato de fecha-hora
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm");
            Date resultado = new Date(medicion.getFecha());
            txtFecha.setText(resultado.toString());

        }
    }// Class ViewHolder()

    /**
     * Constructor de la clase Adaptador.
     *
     * lista : [Medicion],
     * context : Context -> Constructor() ->
     *
     * @param lista Se pasa un array de objetos de tipo Medicion.
     * @param context Se pasa el contexto de la Activity donde se generará el RecyclerView
     *
     */
    public Adaptador(Context context, List<Medicion> lista) {
        this.lista = lista;
        inflador = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("Nums", ""+this.lista);
    }


    /**
     * El método onCreateViewHolder crea cada elemento del recycler. Se debe poner el layout donde
     * está la vista de cada elemento de la lista.
     *
     * parent : ViewGroup,
     * viewType : Z -> onCreateViewHolder() <-
     * ViewHolder <-
     *
     * @param parent Objeto de tipo ViewGroup.
     * @param viewType Entero
     *
     * @return ViewHolder Crea el viewholder con los elementos (textViews, etc.)
     */
    @NonNull
    @Override
    public Adaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elementos_recyclerview, parent, false);
        return new ViewHolder(v);
    }

    /**
     * El método onBindViewHolder asigna los valores específicos a cada uno de los elementos de la
     * lista del VecyclerView. Este método llama al método asignarDatos del objeto ViewHolder donde
     * pondrá los valores de cada elemento de la lista interna de objetos Medicion en los textViews.
     *
     * holder : ViewHolder,
     * position : N -> onBindViewHolder() ->
     *
     * @param holder Se pasa un objeto de tipo ViewHolder.
     * @param position Se pasa la posición del elemento de la lista de objetos Medicion que se va
     *                 a cargar en el ViewHolder
     *
     */
    @Override
    public void onBindViewHolder(@NonNull Adaptador.ViewHolder holder, int position) {
        holder.asignarDatos(lista.get(position));
    }

    /**
     * El método getItemCount devuelve la longitud de la lista de objetos de tipo Medicion que tiene
     * esta clase
     *
     * N <- getItemCount() <-
     *
     * @return N Devuelve la longitud de la lista de objetos Medicion
     */
    @Override
    public int getItemCount() {
        return lista.size();
    }


}
