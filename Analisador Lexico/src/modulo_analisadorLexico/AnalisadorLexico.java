package modulo_analisadorLexico;

//import java.util.regex.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.JOptionPane;
import manipulação_arquivosIO.Arquivo;


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

    private EstruturaLexica estruturaLexica;
    
    private Scanner leitura;
    private Arquivo codigoFonte;
    
    
    
    /**
     * 
     */
    public AnalisadorLexico() {
    
    }
    
    /**
     * 
     * @return
     * @throws FileNotFoundException 
     */
    public ArrayList <String> lerCodigoFonte() throws FileNotFoundException {
        
        String nome = JOptionPane.showInputDialog("Arquivo");
        leitura = new Scanner(new FileReader("code_in/"+nome)).useDelimiter("\n");
      
        ArrayList<String> codigo = new ArrayList();
        while (leitura.hasNext()) {
            codigo.add(leitura.next());
        }
        
        return codigo;
    }
     
          
}
