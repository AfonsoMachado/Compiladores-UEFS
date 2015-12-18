/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_completo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import manipulação_arquivosIO.Arquivo;
import modulo_analisadorLexico.AnalisadorLexico;

/**
 *
 * @author lucas
 */
public class Compilador {
    
    private File file;
    private final Arquivo arquivo;
    private AnalisadorLexico analisadorLexico;
      /**
     * Creates new form Compiler
     */
    public Compilador() {
        
        arquivo = new Arquivo();
    }
    
    
    public void compilar() {
        try {
            ArrayList<String> localFiles = arquivo.lerCodigos();
            for (String lF : localFiles) {
                ArrayList<String> codigoFonte = arquivo.lerCodigoFonte(lF);
                analisadorLexico = new AnalisadorLexico();
                analisadorLexico.analise(codigoFonte);
                arquivo.escreverSaidaLexico(analisadorLexico.getTokens(), analisadorLexico.getErros());
            }

        } catch (FileNotFoundException error1) {
            JOptionPane.showMessageDialog(null, "Arquivo Não Encontrado");
            System.exit(0);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Compilador compilador = new Compilador();
        compilador.compilar();
    }
}
