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
    
    public void analise(ArrayList<Token> tokens) {
        this.tokens = tokens;
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
        if (proximo != null) {
            erros.add("Erro na linha " + proximo.getLinha() + ". " + erro);
        } else {
            erros.add(erro);
        }
    }
    
    private void terminal(String esperado) {
        if (proximo == null ? esperado == null : proximo.getValor().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Token " + esperado + " esperado.");
        }
    }
    
    private void Tipo(String esperado) {
        if (proximo == null ? esperado == null : proximo.getTipo().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Erro na linha " + proximo.getLinha() + ". Token do tipo" + proximo.getTipo() + "esperado.");
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
        if (proximo != null) {
            switch (proximo.getValor()) {
                case "class":
                    reconheceClasse();
                    reconheceClasses();
                    break;
                default:
                    break;
            }
        } else {
            erroSintatico("Fim de arquivo inesperado");
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
                if (proximo.getValor().equals("true")) {
                    terminal("true");
                } else if (proximo.getValor().equals("false")) {
                    terminal("false");
                } else {
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
        System.out.println("listaVariaveis");
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                Tipo("id");
                reconheceListaVariavel();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                reconheceListaVariavel();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                reconheceListaVariavel();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                reconheceListaVariavel();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                reconheceListaVariavel();
                break;
            default:
                break;
        }
    }
    
    private void reconheceIdDeclaracao() {
        System.out.println("idDeclaração");
        switch (proximo.getValor()) {
            case "void":
                terminal("void");
                Tipo("id");
                terminal("(");
                reconheceDeclParametros();
                terminal(")");
                terminal("{");
                reconheceConteudoMetodo();
                terminal("}");
                break;
            case "char":
                terminal("char");
                Tipo("id");
                reconheceCompId();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                reconheceCompId();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                reconheceCompId();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                reconheceCompId();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                reconheceCompId();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    Tipo("id");
                    reconheceCompId();
                    break;
                } else {
                    erroSintatico("espera um tipo");
                }
                break;
        }
    }
    
    private void reconheceCompId() {
        System.out.println("compID");
        switch (proximo.getValor()) {
            case "[":
                terminal("[");
                reconheceIndice();
                terminal("]");
                reconheceListaVetor();
                break;
            case "(":
                terminal("(");
                reconheceDeclParametros();
                terminal(")");
                terminal("{");
                reconheceConteudoMetodo();
                terminal("return");
                reconheceRetorno();
                terminal("}");
                break;
            case ",":
                reconheceListaVariavel();
                break;
            case ";":
                terminal(";");
                break;
            default:
                erroSintatico("Erro na declaração de variavel");
                break;
        }
    }
    
    private void reconheceListaVariavel() {
        System.out.println("lista variavel");
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                Tipo("id");
                reconheceListaVariavel();
                break;
            case ";":
                terminal(";");
                break;
            default:
                erroSintatico("Erro na declaração de variavel");
                break;
        }
    }
    
    private void reconheceListaVetor() {
        System.out.println("lista vetor");
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                Tipo("id");
                terminal("[");
                reconheceIndice();
                terminal("]");
                reconheceListaVetor();
                break;
            case ";":
                terminal(";");
                break;
            default:
                erroSintatico("Erro na declaração de vetores");
                break;
            
        }
    }
    
    private void reconheceIndice() {
        System.out.println("indiceDecl");
        switch (proximo.getTipo()) {
            case "id":
                Tipo("id");
                break;
            case "numero":
                Tipo("numero");
                break;
            default:
                erroSintatico("Erro no indice do vetor");
                break;
        }
    }
    /*
    private void reconheceIndice() {
        System.out.println("indice");
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                reconheceIndice();
                terminal(")");
                break;
            default:
                switch (proximo.getTipo()) {
                    case "numero":
                        Tipo("numero");
                        reconheceCompIndice();
                        break;
                    case "id":
                        reconheceIdIndice();
                        break;
                    case "operador":
                        reconheceIdIndice();
                        break;
                    default:
                        erroSintatico("Erro no indice");
                }
        }
    }
    
    private void reconheceCompIndice() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                reconheceExpAritmetica();
                break;
            case "-":
                terminal("");
                reconheceExpAritmetica();
                break;
            case "*":
                terminal("*");
                reconheceExpAritmetica();
                break;
            case "/":
                terminal("/");
                reconheceExpAritmetica();
                break;
            default:
                break;
        }
    }
    
    private void reconheceIdIndice() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                Tipo("id");
                reconheceCompIndice(); //igual a operador indice
                break;
            case "--":
                terminal("--");
                Tipo("id");
                reconheceCompIndice();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    reconheceAcessoIndice();
                    reconheceCompIndice();
                }
                erroSintatico("Erro no indice do vetor");
                break;
        }
    }
    
    private void reconheceAcessoIndice() {
        switch (proximo.getValor()) {
            case "[":
                terminal("[");
                reconheceIndice();
                terminal("]");
                break;
            case "(":
                terminal("(");
                reconheceParametros();
                terminal(")");
                break;
            case ".":
                terminal(".");
                Tipo("id");
                reconheceChamadaMetodo();
                break;
            case "++":
                terminal("++");
                break;
            case "--":
                terminal("--");
                break;
            default:
                erroSintatico("Erro no indice do Vetor");
                break;
        }
    }
    */
    private void reconheceDeclParametros() {
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                Tipo("id");
                reconheceVarVet();
                reconheceListaParametros();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                reconheceVarVet();
                reconheceListaParametros();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                reconheceVarVet();
                reconheceListaParametros();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                reconheceVarVet();
                reconheceListaParametros();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                reconheceVarVet();
                reconheceListaParametros();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    Tipo("id");
                    reconheceVarVet();
                    reconheceListaParametros();
                }
                break;
        }
    }
    
    private void reconheceConteudoMetodo() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return;
    }
    
    private void reconheceRetorno() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void reconheceExpAritmetica() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void reconheceParametros() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void reconheceChamadaMetodo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void reconheceVarVet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void reconheceListaParametros() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
