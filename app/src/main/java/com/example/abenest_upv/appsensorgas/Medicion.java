package com.example.abenest_upv.appsensorgas;

import static java.lang.System.currentTimeMillis;

// -----------------------------------------------------------------------------------
// @author: Aitor Benítez Estruch
// -----------------------------------------------------------------------------------

public class Medicion {

    private enum TipoMedida{CO2, TEMPERATURA};
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

    //S'ha de posar un argument
    public void setFecha() {
        this.fecha = currentTimeMillis();
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo.name();
    }

    //S'ha de canviar l'argument
    public void setTipo(int identificador) {
        switch (identificador) {
            case 11:
                this.tipo = tipo.CO2;
                break;
            case 12:
                this.tipo = tipo.TEMPERATURA;
                break;
            default:
                break;
        }
    }

    public void setTipo(String tipoM) {
        switch (tipoM) {
            case "CO2":
                this.tipo = tipo.CO2;
                break;
            case "TEMPERATURA":
                this.tipo = tipo.TEMPERATURA;
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

    @Override
    public String toString() {
        return "{" +
                " nombreSensor='" + nombreSensor + '\'' +
                ", macSensor='" + macSensor + '\'' +
                ", uuidSensor='" + uuidSensor + '\'' +
                ", tipo=" + tipo.name() +
                ", medida=" + medida +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", fecha=" + fecha +
                '}';
    }
}
