package com.example.abenest_upv.appsensorgas;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class Medicion {

    private enum TipoMedida{CO2, TEMPERATURA, RUIDO};
    private TipoMedida tipo;
    private String nombreSensor, macSensor, uuidSensor;
    private int medida; //de moment serà un nº enter
    private double latitud, longitud;
    private long fecha;

    public Medicion() {
    }

    public long getFecha() {
        /*SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultado = new Date(this.fecha);
        return resultado.toString();*/
        return this.fecha;
    }

    public void setFecha() {
        this.fecha = currentTimeMillis();
    }

    public String getTipo() {
        return tipo.name();
    }

    public void setTipo(int identificador) {
        switch (identificador) {
            case 11:
                this.tipo = tipo.CO2;
                break;
            case 12:
                this.tipo = tipo.TEMPERATURA;
                break;
            case 13:
                this.tipo = tipo.RUIDO;
                break;
            default:
                break;
        }
    }

    public int getMedida() {
        return medida;
    }

    public String getNombreSensor() {
        return nombreSensor;
    }

    public void setNombreSensor(String nombreSensor) {
        this.nombreSensor = nombreSensor;
    }

    public String getMacSensor() {
        return macSensor;
    }

    public void setMedida(int medida) {
        this.medida = medida;
    }

    public void setMacSensor(String macSensor) {
        this.macSensor = macSensor;
    }

    public String getUuidSensor() {
        return uuidSensor;
    }

    public void setUuidSensor(String uuidSensor) {
        this.uuidSensor = uuidSensor;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    // Filtre per a no afegir la mateixa mesura enviada (quasi) al mateix temps.
    // Primer, comprovar que el tipus de mesura és la mateixa.
    // Segon, comprovar si les mesures s'han enviat fa menys de 10 segons entre elles (marge establert
    // actualment a la placa d'arduino per a enviar noves mesures). El marge es posa per les xicotetes
    // variacions de milisegons entre les mesures que s'envien amb els beacons.
    public boolean equals(Medicion otraMedicion) {
        if(this.tipo.name().equals(otraMedicion.getTipo()) ){
            //Tornar true si les dates es lleven menys de 5 segons
            return  (this.fecha > otraMedicion.getFecha()+5000 );
        }
        return false;
    }

}
