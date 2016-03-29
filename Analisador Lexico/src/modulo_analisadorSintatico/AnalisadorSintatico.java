/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSintatico;

import java.util.ArrayList;
import modulo_analisadorLexico.Token;

/**
 *
 * @author lucas
 */
public class AnalisadorSintatico {

    private Token proximo;
    private ArrayList<Token> tokens;
    private ArrayList<String> erros;
    private int i = 0;

    public void analise(ArrayList<Token> codigoFonte) {
        tokens = codigoFonte;
        proximo = proximo();
        erros = new ArrayList<>();
        reconheceArquivo();
    }

    public ArrayList<String> getErros() {
        return erros;
    }

    private Token proximo() {
        if (i < tokens.size()) {
            return tokens.get(i++);
        } else {
            return null;
        }
    }

    private void erroSintatico(String erro) {
        erros.add(erro);
    }

    private void terminal(String esperado) {
        if (proximo == null ? esperado == null : proximo.getValor().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Erro na linha " + proximo.getLinha() + ". Token " + proximo.getValor() + "esperado.");
        }
    }

    private void reconheceArquivo() {
        reconheceConstantes();
        reconheceDeclaracaoVariavel();
        reconhecePreMain();
    }

    private void reconhecePreMain() {
        System.out.println("pre-main");
        if(proximo!=null){
        switch (proximo.getValor()) {
            case "void":
                reconheceMain();
                reconheceClasses();
                break;
            case "class":
                reconheceClasse();
                reconhecePreMain();
                break;
            default:
                erroSintatico("");
        }
        }
        else {
            erroSintatico("Fim de arquivo inesperado");
        }
    }

    private void reconheceDeclaracaoVariavel() {
        /*
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
         */
    }

    private void reconheceClasse() {
        System.out.println("classe");
        System.out.println(proximo.getValor());
        switch (proximo.getValor()) {
            case "class":
                terminal("class");
                Tipo("id");
                reconheceExpressaoHerenca();
                terminal("{");
                reconheceConteudoClasse();
                terminal("}");
                break;
            default:
                break;
        }
    }

    private void reconheceConteudoMetodo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reconheceMain() {
        terminal("void");
        terminal("main");
        terminal("(");
        terminal(")");
        terminal("{");
        reconheceConteudoMetodo();
        terminal("}");
    }

    private void reconheceConstantes() {
        System.out.println("constantes");
        switch (proximo.getValor()) {
            case "const":
                reconheceConst();
                reconheceConstantes();
                break;
            default:
                break;
        }
    }

    private void reconheceClasses() {
        switch (proximo.getValor()) {
            case "class":
                reconheceClasse();
                reconheceClasses();
                break;
            default:
                break;
        }
    }

    private void reconheceConst() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void Tipo(String esperado) {
        if (proximo == null ? esperado == null : proximo.getTipo().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Erro na linha " + proximo.getLinha() + ". Token do tipo" + proximo.getTipo() + "esperado.");
        }
    }

    private void reconheceExpressaoHerenca() {
        System.out.println("ExpressaoHerenca");
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                Tipo("id");
                break;
            default:
                break;

        }

    }

    private void reconheceConteudoClasse() {
        return;
    }

}
