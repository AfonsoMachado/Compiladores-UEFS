/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorLexico;

/**
 *
 * @author UCHIHA
 */
public class Token {

    /**
     *
     */
    private String tipo;

    /**
     *
     */
    private String valor;

    /**
     *
     */
    private int linha;

    /**
     *
     * @param tipo
     * @param valor
     * @param linha
     */
    public Token(String id, String token, int linha) {
        this.tipo = token;
        this.valor = id;
        this.linha = linha;
    }

    /**
     *
     * @return
     */
    public String getTipo() {
        return tipo;
    }

    /**
     *
     * @return
     */
    public String getValor() {
        return valor;
    }

    /**
     *
     * @return
     */
    public int getLinha() {
        return linha;
    }
}
