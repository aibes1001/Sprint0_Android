package com.example.abenest_upv.appsensorgas;

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {

    private TextView elTexto;
    private Button elBotonEnviar;
    private EditText input;

    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.elTexto = (TextView) findViewById(R.id.elTexto);
        this.elBotonEnviar = (Button) findViewById(R.id.botonEnviar);
        this.input = (EditText) findViewById(R.id.numero);


        Log.d("clienterestandroid", "fin onCreate()");
    }


    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public void boton_enviar_pulsado (View quien) {
        Log.d("clienterestandroid", "boton_enviar_pulsado");
        this.elTexto.setText("pulsado");

        // ojo: creo que hay que crear uno nuevo cada vez
        PeticionarioREST elPeticionario = new PeticionarioREST();

		/*

		   enviarPeticion( "hola", function (res) {
		   		res
		   })

        elPeticionario.hacerPeticionREST("GET",  "http://158.42.144.126:8080/prueba", null,
			(int codigo, String cuerpo) => { } );

		   */

        String jsontext = "{\"medida\":\""+ input.getText() + "\"}";
        elPeticionario.hacerPeticionREST("POST",  "http://10.236.28.238:8080/medida",
                jsontext,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        elTexto.setText ("codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                    }
                }
        );

        Log.d("clienterestandroid", "HOLA");

        /*elPeticionario.hacerPeticionREST("POST", "http://192.168.0.107:8080/mensaje",
                "{\"dni\": \"A9182342W\", \"nombre\": \"Android\", \"apellidos\": \"De Los Palotes\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        elTexto.setText ("codigo respuesta: " + codigo + " <-> \n" + cuerpo);
                    }
                });*/

        //(int codigo, String cuerpo) -> { elTexto.setText ("lo que sea"=; }

        //String textoJSON = "{ 'medida': '" + 987.12  + "' }";

        //"{ 'dni': '2023423434' }";

        /*


		// otro ejemplo:



		/*
        elPeticionario.hacerPeticionREST("GET",  "https://jsonplaceholder.typicode.com/posts/2", ...

    } // pulsado ()

    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;*/
    }

} // class