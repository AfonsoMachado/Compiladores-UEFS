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
        erros.add("Erro na linha " + proximo.getLinha() + ". " + erro);
    }

    private void terminal(String esperado) {
        if (proximo == null ? esperado == null : proximo.getValor().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Token " + esperado + " esperado.");
        }
    }

    private void reconheceArquivo() {
        reconheceConstantes();
        reconheceVariaveis();
        reconhecePreMain();
    }

    private void reconhecePreMain() {
        System.out.println("pre-main");
        if (proximo != null) {
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
                    erroSintatico("Espera uma classe ou o método main");
            }
        } else {
            erroSintatico("Fim de arquivo inesperado");
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

    private void reconheceVariaveis() {
        System.out.println("variaveis");
        switch (proximo.getValor()) {
            case "char":
                reconheceDeclaracaoVariavel();
                reconheceVariaveis();
                break;
            case "int":
                reconheceDeclaracaoVariavel();
                reconheceVariaveis();
                break;
            case "bool":
                reconheceDeclaracaoVariavel();
                reconheceVariaveis();
                break;
            case "string":
                reconheceDeclaracaoVariavel();
                reconheceVariaveis();
                break;
            case "float":
                reconheceDeclaracaoVariavel();
                reconheceVariaveis();
                break;
            default:
                break;
        }
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
                erroSintatico("Classe com erro.");
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
        System.out.println("ConteudoClasse");
        switch (proximo.getValor()) {
            case "const":
                reconheceConst();
                reconheceConteudoClasse();
                break;
            case "void":
                reconheceIdDeclaracao();
                reconheceConteudoClasse();
                break;
            default:
                if (proximo.getTipo().equals("palavra_reservada") || proximo.getTipo().equals("id")) {
                    reconheceIdDeclaracao();
                    reconheceConteudoClasse();
                    break;
                }
                break;
        }
    }

    private void reconheceConst() {
        System.out.println("Const");
        switch (proximo.getValor()) {
            case "const":
                terminal("const");
                terminal("{");
                reconheceBlocoConstantes();
                terminal("}");
            default:
                erroSintatico("Esperava um bloco de contantes");
                break;
        }
    }

    private void reconheceBlocoConstantes() {
        System.out.println("blococonstantes");
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                reconheceListaConst();
                break;
            case "int":
                terminal("int");
                reconheceListaConst();
                break;
            case "bool":
                terminal("bool");
                reconheceListaConst();
                break;
            case "string":
                terminal("string");
                reconheceListaConst();
                break;
            case "float":
                terminal("float");
                reconheceListaConst();
                break;
            default:
                break;
        }
    }

    private void reconheceListaConst() {
        Tipo("id");
        terminal("=");
        reconheceAtribuicaoConstante();
        reconheceAuxiliarDeclaracao();

    }

    private void reconheceAtribuicaoConstante() {
        switch (proximo.getTipo()) {
            case "numero":
                Tipo("numero");
                break;
            case "cadeia_constante":
                Tipo("cadeia_constante");
                break;
            case "caracter_constante":
                Tipo("caracter_constante");
                break;
            default:
                if(proximo.getValor().equals("true")){
                    terminal("true");
                }else if(proximo.getValor().equals("false")){
                    terminal("false");
                }else {
                    erroSintatico("Atribuição invalida");
                }
                break;
        }
    }

    private void reconheceAuxiliarDeclaracao() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                reconheceListaConst();
                break;
            case ";":
                terminal(";");
                reconheceBlocoConstantes();
            default:
                erroSintatico(", ou ; esperados");
                break;

        }
    }

    private void reconheceDeclaracaoVariavel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reconheceConteudoMetodo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void Tipo(String esperado) {
        if (proximo == null ? esperado == null : proximo.getTipo().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Erro na linha " + proximo.getLinha() + ". Token do tipo" + proximo.getTipo() + "esperado.");
        }
    }

    private void reconheceIdDeclaracao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
