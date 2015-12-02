/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author UCHIHA
 */
public class Token {
    
    /**
     * 
     */
    private String token;
    
    /**
     * 
     */
    private String id;
    
    /**
     * 
     */
    private int linha;

    /**
     * 
     * @param token
     * @param id
     * @param linha 
     */
    public Token(String token, String id, int linha) {
        this.token = token;
        this.id = id;
        this.linha = linha;
    }
    
    /**
     * 
     * @return 
     */
    public String getToken() {
        return token;
    }

    /**
     * 
     * @return 
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @return 
     */
    public int getLinha() {
        return linha;
    }
}
