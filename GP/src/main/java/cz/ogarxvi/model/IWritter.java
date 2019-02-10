/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.List;

/**
 *
 * @author OgarXVI
 */
public interface IWritter {
    boolean update(List<String> replaces, List<Integer> colums, List<Integer> rows);
    boolean copy(List<String> replaces, List<Integer> colums, List<Integer> rows);
}
