package util;

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
 * @author lucas
 */
public class Arquivo {
    
    private Scanner scanner;
    
    public ArrayList <String> lerCodigoFonte(){
        try {
            String nome = JOptionPane.showInputDialog("Arquivo");
            scanner = new Scanner(new FileReader(nome)).useDelimiter("\\n");
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Arquivo Não Encontrado");
        }
        ArrayList<String> codigo = new ArrayList();
        while (scanner.hasNext()) {
            codigo.add(scanner.next());
        }
        
        return codigo;
    }
    
    public void escreverSaidaLexico(ArrayList <String> saida){
        
    }
    
}
