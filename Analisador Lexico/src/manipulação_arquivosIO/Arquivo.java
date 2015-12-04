package manipulação_arquivosIO;

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
     * 
     * @return
     * @throws FileNotFoundException 
     */ 
    public ArrayList <String> lerCodigoFonte() throws FileNotFoundException {
        
        String nome = JOptionPane.showInputDialog("Arquivo");
        scanner = new Scanner(new FileReader(nome)).useDelimiter("\n");
      
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
