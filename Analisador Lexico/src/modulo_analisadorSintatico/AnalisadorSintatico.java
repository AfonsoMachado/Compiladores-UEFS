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
        switch (proximo) {
            case "void":
                terminal("void");
                terminal("main");
                terminal("(");
                terminal(")");
                terminal("{");
                reconheceConteudoMetodo();
                terminal("}");
                break;
            default:
                erroSintatico();
        }
    }

    private String proximo() {
        return ""  + tokens.get(0);
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

}
