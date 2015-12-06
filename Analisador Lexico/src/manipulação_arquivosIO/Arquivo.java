package manipulação_arquivosIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MUDAR DEPOIS ESSES DADOS DO JAVADOC. 
 * na verdade é bom começar com o javadoc tbm
 */
public class Arquivo {
    
    /**
     * 
     */
    private Scanner scanner;
    
    /**
     * C:\Users\Magally\Documents\NetBeansProjects\Compiladores-UEFS\Analisador Lexico\src\manipulação_arquivosIO\Arquivo.java
     * @param file
     * @return
     * @throws FileNotFoundException 
     */ 
    public ArrayList <String> lerCodigoFonte(File file) throws FileNotFoundException {
        
        scanner = new Scanner(new FileReader(file)).useDelimiter("\n");
      
        ArrayList<String> codigo = new ArrayList();
        while (scanner.hasNext()) {
            codigo.add(scanner.next());
        }
       
        return codigo; 
    }
    
    /**
     * 
     * @param saida 
     */
    public void escreverSaidaLexico(ArrayList <String> saida){
        
        
    }
    
}
