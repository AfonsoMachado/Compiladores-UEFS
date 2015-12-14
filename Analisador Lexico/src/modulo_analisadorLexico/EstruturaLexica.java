/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorLexico;

import java.util.ArrayList;

/**
 *
 * @author UCHIHA
 */
public class EstruturaLexica {
    
    /**
     * 
     */
    private final ArrayList<String> palavrasReservadas;
    /**
     * 
     */
    private final ArrayList<Character> letra;
    /**
     * 
     */
    private final ArrayList<Character> digito;
    /**
     * 
     */
    private final ArrayList<Character> simbolo;
    /**
     * 
     */
    private final ArrayList<Character> operadores;
    /**
     * 
     */
    private final ArrayList<Character> delimitadores;
    /**
     * 
     */
    private final ArrayList<String> comentarios;

    /**
     * 
     */
    public EstruturaLexica() {
        
        this.palavrasReservadas = new ArrayList<>();
        this.letra = new ArrayList<>();
        this.digito = new ArrayList<>();
        this.simbolo = new ArrayList<>();
        this.operadores = new ArrayList<>();
        this.delimitadores = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        
        //
        this.palavrasReservadas.add("class");
        this.palavrasReservadas.add("const");
        this.palavrasReservadas.add("else");
        this.palavrasReservadas.add("if");
        this.palavrasReservadas.add("new");
        this.palavrasReservadas.add("read");
        this.palavrasReservadas.add("write");
        this.palavrasReservadas.add("return");
        this.palavrasReservadas.add("void");
        this.palavrasReservadas.add("while");
        this.palavrasReservadas.add("int");
        this.palavrasReservadas.add("float");
        this.palavrasReservadas.add("bool");
        this.palavrasReservadas.add("string");
        this.palavrasReservadas.add("char");
        this.palavrasReservadas.add("true");
        this.palavrasReservadas.add("false");
        this.palavrasReservadas.add("main");
        
        //
        for (char i = 'a'; i <='z' ; i++) {
            this.letra.add((char) i);
        }
        for (char i = 'A'; i <='Z' ; i++) {
            this.letra.add((char) i);
        }
        
        //
        for (char i = '0'; i <='9' ; i++) {
            this.digito.add(i);
        }
        
        //
        for (int i = 32; i <=126 ; i++) {
            this.simbolo.add((char) i);
        }
        
        //
        this.operadores.add('.');
        this.operadores.add('+');
        this.operadores.add('-');
        this.operadores.add('*');
        this.operadores.add('/');
        this.operadores.add('+');
        this.operadores.add('-');
        this.operadores.add('!');
        this.operadores.add('>');
        this.operadores.add('<');
        this.operadores.add('&');
        this.operadores.add('|');
        this.operadores.add('=');
        
        //
        this.delimitadores.add(';');
        this.delimitadores.add(',');
        this.delimitadores.add('(');
        this.delimitadores.add(')');
        this.delimitadores.add('{');
        this.delimitadores.add('}');
        this.delimitadores.add('[');
        this.delimitadores.add(']');
        
        //
        this.comentarios.add("*/");
        this.comentarios.add("/*");
        this.comentarios.add("//");
    }
    
    /**
     * 
     * @param pReservada
     * @return 
     */
    public boolean ehPalavraReservada(String pReservada) {
                
        return this.palavrasReservadas.contains(pReservada);
    }

    /**
     * 
     * @param simb
     * @return 
     */
    public boolean ehSimbolo(char simb) {
                
        return this.simbolo.contains(simb);
    }

    /**
     * 
     * @param ope
     * @return 
     */
    public boolean ehOperador(char ope) {
        
        return this.operadores.contains(ope);
    }
    
    /**
     * 
     * @param delim
     * @return 
     */
    public boolean ehDelimitador(char delim) {
        
        return this.delimitadores.contains(delim);
    }
   
}