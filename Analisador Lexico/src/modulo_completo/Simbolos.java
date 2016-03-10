/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_completo;

import java.util.ArrayList;

/**
 *
 * @author lucas
 */
public class Simbolos {
    public static final int INT = 0;
    public static final int CHAR = 1;
    public static final int STRING = 2;
    public static final int FLOAT = 3;
    public static final int BOOL = 4;
    public static final int OBJECT = 5;
    
    public static final int VAR = 10;
    public static final int CONST= 11;
    public static final int MET = 12;
    public static final int CLASS = 13;
    public static final int VET = 14;
    public static final int MAIN = 15;
    
    
    
    private final String nome;
    private Object valor;
    private final int categoria;
    private final int tipo;
    private final ArrayList<Simbolos> filhos;

    public Simbolos(int categoria, int tipo, String nome) {
        this.categoria = categoria;
        this.tipo = tipo;
        this.nome = nome;
        filhos = new ArrayList<>();
    }
    
    
    public void addFilho(Simbolos filho){
        filhos.add(filho);
    }
    
    
}
