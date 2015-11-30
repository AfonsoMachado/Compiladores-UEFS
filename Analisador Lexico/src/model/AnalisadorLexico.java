package model;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import util.Arquivo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author UCHIHA
 */
public class AnalisadorLexico {

    /**
     * 
     */
    public AnalisadorLexico() {
    
    }
    
    public static void main(String[] args){
        //coisas aleatorias 
        String nome = JOptionPane.showInputDialog("Arquivo");
        Arquivo arquivo = null;
        ArrayList<String> codigoFonte = arquivo.lerCodigoFonte();
        
    }
}
