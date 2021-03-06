/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.genetic.GeneticAlgorithm;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.scene.control.Button;

/**
 *
 * @author OgarXVI
 */
public class GPController extends Thread {

    private DataHandler dh;
    private Messenger m;
    private Button pauseButton;
    private Button stopButton;
    private int numberOfGeneration;
    private int sizeOfInitPopulation;
    private int maximalniInicializacniHloubkaStromu;
    private int maximalniHloubkaStromuPoKrizeni;
    private double krizeni;
    private double reprodukce;
    private double mutace;
    private double krizeniVUzluFunkce;
    private boolean zachovavatNejzdatnejsihoJedince;
    private boolean decimace;
    private boolean editace;
    private int pocetKroku;
    private int selectionMethod;
    
    public GPController(Messenger m, DataHandler dh, Button pauseButton, Button stopButton, int pocetGeneraci, int velikostPocatecniPopulace, int maximalniInicializacniHloubkaStromu, int maximalniHloubkaStromuPoKrizeni, double krizeni, double reprodukce, double mutace, double krizeniVUzluFunkce, boolean zachovavatNejzdatnejsihoJedince, boolean decimace, boolean editace, int pocetKroku, int selectionMethod) {
        this.m = m;
        this.dh = dh;
        this.pauseButton = pauseButton;
        this.stopButton = stopButton;
        this.numberOfGeneration = pocetGeneraci;
        this.sizeOfInitPopulation = velikostPocatecniPopulace;
        this.maximalniInicializacniHloubkaStromu = maximalniInicializacniHloubkaStromu;
        this.maximalniHloubkaStromuPoKrizeni = maximalniHloubkaStromuPoKrizeni;
        this.krizeni = krizeni;
        this.reprodukce = reprodukce;
        this.mutace = mutace;
        this.krizeniVUzluFunkce = krizeniVUzluFunkce;
        this.zachovavatNejzdatnejsihoJedince = zachovavatNejzdatnejsihoJedince;
        this.decimace = decimace;
        this.editace = editace;
        this.pocetKroku = pocetKroku;
        this.selectionMethod = selectionMethod;
    }

    public GPController(Messenger mm) {
        m = mm;
    }

    public void run() {

        try {
            GeneticAlgorithm ga = new GeneticAlgorithm(stopButton, m, dh);
            ga.runGP(numberOfGeneration, sizeOfInitPopulation, maximalniInicializacniHloubkaStromu, maximalniHloubkaStromuPoKrizeni, reprodukce, krizeni, mutace, krizeniVUzluFunkce, zachovavatNejzdatnejsihoJedince, decimace, editace, pocetKroku, selectionMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    GPController(Messenger m, Button PauseButton, Button StopButton) {
        this.m = m;
        this.pauseButton = PauseButton;
        this.stopButton = StopButton;
    }

}
