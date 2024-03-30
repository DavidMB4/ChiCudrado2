package com.example.chicuadrado;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;


public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML private TextField Datos;
    @FXML private TextField NcText;
    @FXML private TextField KField;
    @FXML private TextField AmpField;
    @FXML private TextField ValorX2;
    @FXML private TextField x2Prueba;
    @FXML private TextField pruebaHip;

    @FXML private TableView<Clase> table;
    @FXML private TableColumn<Clase , Integer> clases1;
    @FXML private TableColumn<Clase , String> clases2;
    @FXML private TableColumn<Clase , Integer> frecAbs;
    @FXML private TableColumn<Clase , Integer> frecAcum;

    @FXML private TableView<KDatos> kTable;
    @FXML private TableColumn<KDatos , Double> valorKColumn;
    @FXML private TableColumn<KDatos , String> kColumn;

    public void initialize(){
        valorKColumn.setCellValueFactory(new PropertyValueFactory<KDatos, Double>("ValorKColumn"));
        kColumn.setCellValueFactory(new PropertyValueFactory<KDatos, String>("kColumn"));

        clases1.setCellValueFactory(new PropertyValueFactory<Clase, Integer>("clases1"));
        clases2.setCellValueFactory(new PropertyValueFactory<Clase, String>("clases2"));
        frecAbs.setCellValueFactory(new PropertyValueFactory<Clase, Integer>("frecAbs"));
        frecAcum.setCellValueFactory(new PropertyValueFactory<Clase, Integer>("frecAcum"));
    }

    public void start(javafx.stage.Stage primaryStage) throws Exception {
        // Inicialización de la escena, etc.
    }

    public void button(){
        this.kTable.getItems().clear();
        this.table.getItems().clear();

        List<Double> lista = parseNumbers(Datos.getText().substring(1));

        int n = lista.size();
        String kString = KField.getText();
        String ampliString = AmpField.getText();
        double k = DatoK();
        double Ampli = DatoAmpli();

        if(k == 0){
            k = 1/Ampli;
        } else if(Ampli == 0){
            Ampli = 1/k;
        }

        System.out.println();
        double min = lista.get(0);
        double max = lista.get(0);
        double rango;
        double Xmedia = 0;
        double desvEstand;
        double varianza;
        double Ai = lista.size()*Ampli;
        System.out.println(lista);
        System.out.println(lista.size());
        double chiCuad;

        double Nc = DatoNc();

        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i) < min) {
                min = lista.get(i);
            }
        }

        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i) > max) {
                max = lista.get(i);
            }
        }

        rango = max-min;

        for(int i=0; i < lista.get(i); i++){
            Xmedia += lista.get(i);
        }
        Xmedia = Xmedia/lista.size();

        double sumaCuadrados = 0;
        for(int i=0; i < lista.size(); i++){
            sumaCuadrados = sumaCuadrados + Math.pow(lista.get(i)-Xmedia, 2);
        }

        varianza = VarianzaMetod(lista, Xmedia, sumaCuadrados);
        desvEstand = Math.sqrt(varianza);
        chiCuad = FrecuenciasK(k, Ampli, lista, Ai);
        String chiDecimal = String.format("%.4f", chiCuad);
        chiCuad = Double.parseDouble(chiDecimal);
        System.out.println(chiCuad);

        double alfa = 1-(1 - Nc / 100);
        System.out.println("Alfa: "+alfa);
        ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(k-1);
        double inverseChiSquare = chiSquaredDistribution.inverseCumulativeProbability(alfa);
        String StringInverseChiSquare = String.format("%.4f", inverseChiSquare);
        double inverseChiSquareMostrar = Double.parseDouble(StringInverseChiSquare);
        System.out.println(inverseChiSquare);

        String condicion = pruebaDeHip(inverseChiSquare, chiCuad);
        System.out.println(condicion);

        ValorX2.setText(String.valueOf(inverseChiSquareMostrar));
        pruebaHip.setText(condicion);

        int frecuenciaAcumulada = 0;
        double contadorAmpli = Ampli;
        chiCuad = 0;
        double chiCuadTotal = 0;
        String KdatosString = "k"+1;
        System.out.println(KdatosString);
        String StringContadorAmpliMin = String.format("%.2f", contadorAmpli-Ampli);
        double AmpliMin = Double.parseDouble(StringContadorAmpliMin);
        String StringContadorAmpliMax = String.format("%.2f", contadorAmpli);
        double AmpliMax = Double.parseDouble(StringContadorAmpliMax);


        for(int i=0; i<k; i++){
            int frecuencia = 0;
            for(int u = 0; u< lista.size(); u++){
                if(AmpliMin == 0){
                    if(lista.get(u)<AmpliMax){
                        frecuencia++;
                    }
                } else if(AmpliMax == 1){
                    if(lista.get(u)>=AmpliMin){
                        frecuencia ++;
                    }
                } else {
                    if(lista.get(u) >= (AmpliMin) && lista.get(u) < AmpliMax){
                        frecuencia ++;
                    }
                }

            }

            String intervalos = "("+AmpliMin+", "+AmpliMax+")";
            System.out.println(frecuencia);
            chiCuad = Math.pow((frecuencia-Ai),2)/Ai;
            chiCuadTotal += chiCuad;
            System.out.println(chiCuadTotal);
            contadorAmpli += Ampli;

            StringContadorAmpliMin = String.format("%.2f", contadorAmpli-Ampli);
            AmpliMin = Double.parseDouble(StringContadorAmpliMin);
            StringContadorAmpliMax = String.format("%.2f", contadorAmpli);
            AmpliMax = Double.parseDouble(StringContadorAmpliMax);

            String StringChiCuad = String.format("%.4f", chiCuad);
            double chiCuadMostrar = Double.parseDouble(StringChiCuad);

            frecuenciaAcumulada += frecuencia;
            KdatosString = "k"+(i+1);

           KDatos numero = new KDatos (chiCuadMostrar, KdatosString);
            this.kTable.getItems().add(numero);
            Clase numero2 = new Clase((i+1),intervalos, frecuencia, frecuenciaAcumulada);
            this.table.getItems().add(numero2);
        }
        String StringChiCuadTotal = String.format("%.4f", chiCuadTotal);
        double chiCuadTotalMostrar = Double.parseDouble(StringChiCuadTotal);
        x2Prueba.setText(String.valueOf(chiCuadTotalMostrar));

    }

    public double DatoAmpli(){
        if(AmpField.getText() != ""){
            double Dato1 = Double.parseDouble(AmpField.getText());
            return Dato1;
        } else {
            throw new IllegalArgumentException("Faltan datos en Amplitud.");
        }
    }

    public double DatoK(){
        if(KField.getText() != ""){
            double Dato2 = Double.parseDouble(KField.getText());
            return Dato2;
        } else {
            throw new IllegalArgumentException("Faltan datos en K.");
        }
    }

    public double DatoNc(){
        if(NcText.getText() != ""){
           double Dato3 = Double.parseDouble(NcText.getText());
           return Dato3;
        } else {
            throw new IllegalArgumentException("Faltan datos en Nivel de confianza.");
        }
    }

    public static List<Double> parseNumbers(String data) {
        List<Double> numbers = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

        // Recorre cada caracter en la cadena de datos
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);

            // Agrega el caracter al número actual
            currentNumber.append(c);

            // Si el siguiente caracter es un punto, el actual es un dígito, y hay más caracteres por delante,
            // eso significa que hemos llegado al final de un número.
            if (i + 1 < data.length() && data.charAt(i + 1) == '.' && Character.isDigit(c)) {
                try {
                    double number = Double.parseDouble(currentNumber.toString());
                    numbers.add(number);
                    // Reinicia el StringBuilder para el siguiente número
                    currentNumber.setLength(0);
                } catch (NumberFormatException e) {
                    // Manejar la excepción según sea necesario
                    System.out.println("Error al parsear: " + currentNumber.toString());
                }
            }
        }

        // Añade el último número si es que queda alguno
        if (currentNumber.length() > 0) {
            try {
                double number = Double.parseDouble(currentNumber.toString());
                numbers.add(number);
            } catch (NumberFormatException e) {
                System.out.println("Error al parsear: " + currentNumber.toString());
            }
        }

        return numbers;
    }

    public static String pruebaDeHip(double inverseChiSquare, double chiCuad){
        if(chiCuad>inverseChiSquare){
            return("Ho se rechaza y los datos no son de una serie U(0, 1)");
        } else{
            return ("Ho se acepta y los datos son de una serie U(0, 1)");
        }
    }

    public static double VarianzaMetod(List<Double> lista, double Xmedia, double sumaCuadrados){
        double standarDerivation = Math.sqrt(sumaCuadrados/lista.size());
        return standarDerivation;
    }

    public static double FrecuenciasK(double k, double Ampli, List<Double> lista, double Ai){
        int frecuenciaAcumulada = 0;
        double contadorAmpli = Ampli;
        double chiCuad = 0;
        double chiCuadTotal = 0;

        for(int i=0; i<k; i++){
            int frecuencia = 0;
            for(int u = 0; u<lista.size(); u++){
                if(lista.get(u)<contadorAmpli && (contadorAmpli-Ampli)<= lista.get(u)){
                    frecuencia ++;
                }
            }

            chiCuad = Math.pow((frecuencia-Ai),2)/Ai;
            chiCuadTotal += chiCuad;
            contadorAmpli += Ampli;
            frecuenciaAcumulada += frecuencia;
        }
        return chiCuadTotal;
    }
}