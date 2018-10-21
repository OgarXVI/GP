/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.io.File;

/**
 * Rozhraní pro načítání souborů
 * @author OgarXVI
 */
public interface IReader {
    /**
     * Vrátí načtená data ve formě dvojrozměrného ple
     * @return Načtená data
     */
    public String[][] GetData();
    /**
     * Otevře a načte vybraný soubor
     * @param file Vybraný soubor na načtení
     */
    public void ReadFile(File  file);
}
