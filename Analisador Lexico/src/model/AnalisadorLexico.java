package model;

import java.io.FileNotFoundException;
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
        
        Arquivo arquivo = new Arquivo();
        try {
            ArrayList<String> codigoFonte = arquivo.lerCodigoFonte();
            for (String string : codigoFonte) {
                System.out.println(string);
            }
        } catch (FileNotFoundException error1) {
            JOptionPane.showMessageDialog(null, "Arquivo Não Encontrado");
            System.exit(0);
        }
              
    }
}
