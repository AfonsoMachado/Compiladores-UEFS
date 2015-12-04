package modulo_completo;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import modulo_analisadorLexico.AnalisadorLexico;
import manipulação_arquivosIO.Arquivo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jkdrangel
 */
public class Compilador {
    
    /**
     * 
     */
    private AnalisadorLexico analisadorLexico;
    
    /**
     * 
     */
    public Compilador() {
        
    }
    
    public static void main(String[] args){
        
        //
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
