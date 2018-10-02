/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.io.File;

/**
 *
 * @author OgarXVI
 */
public interface IReader {
    public String[][] GetData();
    public void ReadFile(File  file);
}
