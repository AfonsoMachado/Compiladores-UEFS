/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSintatico;

import java.util.ArrayList;

/**
 *
 * @author lucas
 */
public class AnalisadorSintatico {

    private String proximo;
    private ArrayList<String> tokens;

    public void analise(ArrayList<String> codigoFonte) {
        tokens = codigoFonte;
        proximo = proximo();
        reconheceArquivo();
    }

    private String proximo() {
        return "" + tokens.get(0);
    }

    private void erroSintatico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void terminal(String esperado) {
        if (proximo == null ? esperado == null : proximo.equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico();
        }
    }

    private void reconheceConteudoMetodo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reconheceMain() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reconheceConst() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reconheceArquivo() {
        switch (proximo) {
            case "const":
                reconheceConst();
                break;
            case "char":
                reconheceDeclaracaoVariavel();
                break;
            case "int":
                reconheceDeclaracaoVariavel();
                break;
            case "bool":
                reconheceDeclaracaoVariavel();
                break;
            case "string":
                reconheceDeclaracaoVariavel();
                break;
            case "float":
                reconheceDeclaracaoVariavel();
                break;
            case "main":
                reconheceMain();
                break;
            default:
                erroSintatico();
        }
    }

    private void reconheceDeclaracaoVariavel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
