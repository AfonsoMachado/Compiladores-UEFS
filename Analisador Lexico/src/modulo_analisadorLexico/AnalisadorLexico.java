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

    private EstruturaLexica estruturaLexica = new EstruturaLexica();
    private ArrayList<String> tokens;
    private ArrayList<String> erros;

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public ArrayList<String> getErros() {
        return erros;
    }
    
    
    /**
     * 
     */
    public AnalisadorLexico() {
    
    }
     
    public void analise(ArrayList<String> codigo){
        int numero=0, indice = 0;
        String token = ""; 
        for (String linha : codigo) {
            numero++;
            indice = 0;
            char[] caracteres = linha.toCharArray();
            while(caracteres[indice]!='\0'){
                while(Character.isSpaceChar(caracteres[indice])){
                    indice++;
                }
                //Verifica se eh um identificador
                if(Character.isLetter(caracteres[indice])){
                    token = token + caracteres[indice];
                    indice++;
                    //percorre enquanto houver letras digitos ou _
                    while(Character.isLetter(caracteres[indice]) || Character.isDigit(caracteres[indice]) || caracteres[indice]== '_'){
                        token = token + caracteres[indice];
                        indice++;
                    }
                    //Apos consumir letras digitos e simbolos verifica se o token esta correto
                    //Falta verificar operadores (Mudar na estrutura lexica pra char)
                    if(Character.isSpaceChar(caracteres[indice]) || estruturaLexica.ehDelimitador(caracteres[indice])){
                        JOptionPane.showMessageDialog(null, token);
                        //verifica se eh uma palavra reservada
                        if(estruturaLexica.ehPalavraReservada(token)){
                             JOptionPane.showMessageDialog(null, token + " Palavra reservada");
                        }
                        token ="";
                    }
                    //indentificador com erro
                    else{
                        JOptionPane.showMessageDialog(null, token+ " Identificador mal formado");
                    }
                }
                if(estruturaLexica.ehDelimitador(caracteres[indice])){
                    token = token + caracteres[indice];
                    indice++;
                }
            }
        }
    
    }      
}
