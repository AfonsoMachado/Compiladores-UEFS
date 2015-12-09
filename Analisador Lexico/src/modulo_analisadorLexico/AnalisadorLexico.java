package modulo_analisadorLexico;

//import java.util.regex.*;

import java.util.ArrayList;
import javax.swing.JOptionPane;


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
    private ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<String> erros = new ArrayList<>();

    public ArrayList<Token> getTokens() {
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
            while(indice<caracteres.length){
                if(Character.isSpaceChar(caracteres[indice])){
                    indice++;
                }
                //Verifica se eh um identificador
                else if(Character.isLetter(caracteres[indice])){
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
                        Token tk;                        
                        //verifica se eh uma palavra reservada
                        if(estruturaLexica.ehPalavraReservada(token)){
                             JOptionPane.showMessageDialog(null, token + " Palavra reservada");
                             tk = new Token(token, "Palavra Reservada", numero);
                        }
                        else{
                             tk= new Token(token, "Identificador", numero);
                        }
                        tokens.add(tk);
                        token ="";
                    }
                    //indentificador com erro
                    else{
                        JOptionPane.showMessageDialog(null, token+ " Identificador mal formado");
                        erros.add("Código com erro, identificador mal formado na linha "+ numero + "\n");
                    }
                }
                else if(estruturaLexica.ehDelimitador(caracteres[indice])){
                    token = token + caracteres[indice];
                    indice++;
                }
            }
            System.out.println("Acabou");
        }
    
    }      
}
