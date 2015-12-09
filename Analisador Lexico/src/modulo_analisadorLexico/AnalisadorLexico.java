package modulo_analisadorLexico;

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
        int numero=0, indice;
        String token; 
        for (String linha : codigo) {
            numero++;
            indice = 0;
            char[] caracteres = linha.toCharArray();
            while(indice<caracteres.length){
                token = "";
                if(Character.isSpaceChar(caracteres[indice])){
                    indice++;
                }
                //Verifica se eh um identificador
                else if(Character.isLetter(caracteres[indice])){
                    token = token + caracteres[indice];
                    indice++;
                    //percorre enquanto houver letras digitos ou _
                    while(indice<caracteres.length && (Character.isLetter(caracteres[indice]) || Character.isDigit(caracteres[indice]) || caracteres[indice]== '_')){
                        token = token + caracteres[indice];
                            indice++;
                    }
                    //Apos consumir letras digitos e simbolos verifica se o token esta correto
                    if(caracteres.length==indice || Character.isSpaceChar(caracteres[indice]) || estruturaLexica.ehDelimitador(caracteres[indice]) || estruturaLexica.ehOperador(caracteres[indice])){
                        Token tk;                        
                        //verifica se eh uma palavra reservada
                        if(estruturaLexica.ehPalavraReservada(token)){
                             tk = new Token(token, "Palavra Reservada", numero);
                        }
                        else{
                             tk= new Token(token, "Identificador", numero);
                        }
                        tokens.add(tk);
                    }
                    //indentificador com erro
                    else{
                        erros.add("Código com erro, identificador mal formado na linha "+ numero + "\n");
                    }
                    System.out.println(token);
                }
                else if(estruturaLexica.ehDelimitador(caracteres[indice])){
                    token = token + caracteres[indice];
                    Token tk = new Token(token, "Delimitador", indice);
                    tokens.add(tk);
                    indice++;
                }
                //outros itens
//                else if(){
//                
//                }
            }
        }
    
    }      
}
