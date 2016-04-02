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
        recArquivo();
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
            erros.add("Erro na linha " + proximo.getLinha() + ". " + erro + "\n");
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

    private void recArquivo() {
        recConstantes();
        recVariaveis();
        recPreMain();
    }

    private void recPreMain() {
        System.out.println("pre-main");
        if (proximo != null) {
            switch (proximo.getValor()) {
                case "void":
                    recMain();
                    recClasses();
                    break;
                case "class":
                    recClasse();
                    recPreMain();
                    break;
                default:
                    erroSintatico("Espera uma classe ou o método main");
            }
        } else {
            erroSintatico("Fim de arquivo inesperado");
        }
    }

    private void recClasses() {
        if (proximo != null) {
            switch (proximo.getValor()) {
                case "class":
                    recClasse();
                    recClasses();
                    break;
                default:
                    break;
            }
        } else {
            erroSintatico("Fim de arquivo inesperado");
        }
    }

    private void recConstantes() {
        System.out.println("constantes");
        switch (proximo.getValor()) {
            case "const":
                recConst();
                recConstantes();
                break;
            default:
                break;
        }
    }

    private void recVariaveis() {
        System.out.println("variaveis");
        switch (proximo.getValor()) {
            case "char":
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "int":
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "bool":
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "string":
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "float":
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            default:
                break;
        }
    }

    private void recMain() {
        terminal("void");
        terminal("main");
        terminal("(");
        terminal(")");
        terminal("{");
        recConteudoMetodo();
        terminal("}");
    }

    private void recClasse() {
        System.out.println("classe");
        System.out.println(proximo.getValor());
        switch (proximo.getValor()) {
            case "class":
                terminal("class");
                Tipo("id");
                recExpressaoHerenca();
                terminal("{");
                recConteudoClasse();
                terminal("}");
                break;
            default:
                erroSintatico("Classe com erro.");
        }
    }

    private void recExpressaoHerenca() {
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

    private void recConteudoClasse() {
        System.out.println("ConteudoClasse");
        switch (proximo.getValor()) {
            case "const":
                recConst();
                recConteudoClasse();
                break;
            case "void":
                recIdDeclaracao();
                recConteudoClasse();
                break;
            default:
                if (proximo.getTipo().equals("palavra_reservada") || proximo.getTipo().equals("id")) {
                    recIdDeclaracao();
                    recConteudoClasse();
                    break;
                }
                break;
        }
    }

    private void recConst() {
        System.out.println("Const");
        switch (proximo.getValor()) {
            case "const":
                terminal("const");
                terminal("{");
                recBlocoConstantes();
                terminal("}");
            default:
                erroSintatico("Esperava um bloco de contantes");
                break;
        }
    }

    private void recBlocoConstantes() {
        System.out.println("blococonstantes");
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                recListaConst();
                break;
            case "int":
                terminal("int");
                recListaConst();
                break;
            case "bool":
                terminal("bool");
                recListaConst();
                break;
            case "string":
                terminal("string");
                recListaConst();
                break;
            case "float":
                terminal("float");
                recListaConst();
                break;
            default:
                break;
        }
    }

    private void recListaConst() {
        Tipo("id");
        terminal("=");
        recAtribuicaoConstante();
        recAuxiliarDeclaracao();

    }

    private void recAtribuicaoConstante() {
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

    private void recAuxiliarDeclaracao() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recListaConst();
                break;
            case ";":
                terminal(";");
                recBlocoConstantes();
            default:
                erroSintatico(", ou ; esperados");
                break;

        }
    }

    private void recDeclaracaoVariavel() {
        System.out.println("listaVariaveis");
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                Tipo("id");
                recListaVariavel();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                recListaVariavel();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                recListaVariavel();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                recListaVariavel();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                recListaVariavel();
                break;
            default:
                break;
        }
    }

    private void recIdDeclaracao() {
        System.out.println("idDeclaração");
        switch (proximo.getValor()) {
            case "void":
                terminal("void");
                Tipo("id");
                terminal("(");
                recDeclParametros();
                terminal(")");
                terminal("{");
                recConteudoMetodo();
                terminal("}");
                break;
            case "char":
                terminal("char");
                Tipo("id");
                recCompId();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                recCompId();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                recCompId();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                recCompId();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                recCompId();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    Tipo("id");
                    recCompId();
                    break;
                } else {
                    erroSintatico("espera um tipo");
                }
                break;
        }
    }

    private void recCompId() {
        System.out.println("compID");
        switch (proximo.getValor()) {
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            case "(":
                terminal("(");
                recDeclParametros();
                terminal(")");
                terminal("{");
                recConteudoMetodo();
                terminal("return");
                recRetorno();
                terminal("}");
                break;
            case ",":
                recListaVariavel();
                break;
            case ";":
                terminal(";");
                break;
            default:
                erroSintatico("Erro na declaração de variavel");
                break;
        }
    }

    private void recListaVariavel() {
        System.out.println("lista variavel");
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                Tipo("id");
                recListaVariavel();
                break;
            case ";":
                terminal(";");
                break;
            default:
                erroSintatico("Erro na declaração de variavel");
                break;
        }
    }

    private void recListaVetor() {
        System.out.println("lista vetor");
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                Tipo("id");
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            case ";":
                terminal(";");
                break;
            default:
                erroSintatico("Erro na declaração de vetores");
                break;

        }
    }

    private void recIndice() {
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
     private void recIndice() {
     System.out.println("indice");
     switch (proximo.getValor()) {
     case "(":
     terminal("(");
     recIndice();
     terminal(")");
     break;
     default:
     switch (proximo.getTipo()) {
     case "numero":
     Tipo("numero");
     recCompIndice();
     break;
     case "id":
     recIdIndice();
     break;
     case "operador":
     recIdIndice();
     break;
     default:
     erroSintatico("Erro no indice");
     }
     }
     }
    
     private void recCompIndice() {
     switch (proximo.getValor()) {
     case "+":
     terminal("+");
     recExpAritmetica();
     break;
     case "-":
     terminal("");
     recExpAritmetica();
     break;
     case "*":
     terminal("*");
     recExpAritmetica();
     break;
     case "/":
     terminal("/");
     recExpAritmetica();
     break;
     default:
     break;
     }
     }
    
     private void recIdIndice() {
     switch (proximo.getValor()) {
     case "++":
     terminal("++");
     Tipo("id");
     recCompIndice(); //igual a operador indice
     break;
     case "--":
     terminal("--");
     Tipo("id");
     recCompIndice();
     break;
     default:
     if (proximo.getTipo().equals("id")) {
     Tipo("id");
     recAcessoIndice();
     recCompIndice();
     }
     erroSintatico("Erro no indice do vetor");
     break;
     }
     }
    
     private void recAcessoIndice() {
     switch (proximo.getValor()) {
     case "[":
     terminal("[");
     recIndice();
     terminal("]");
     break;
     case "(":
     terminal("(");
     recParametros();
     terminal(")");
     break;
     case ".":
     terminal(".");
     Tipo("id");
     recChamadaMetodo();
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

    private void recDeclParametros() {
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    Tipo("id");
                    recVarVet();
                    recListaParametros();
                }
                break;
        }
    }

    private void recListaParametros() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recTipo();
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            default:
                break;
        }
    }

    private void recTipo() {
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                break;
            case "int":
                terminal("int");
                break;
            case "bool":
                terminal("bool");
                break;
            case "string":
                terminal("string");
                break;
            case "float":
                terminal("float");
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                } else {
                    erroSintatico("Espera um tipo");
                }
                break;
        }
    }

    private void recVarVet() {
        switch (proximo.getValor()) {
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                break;
            default:
                break;
        }
    }

    private void recConteudoMetodo() {
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                recComando();
                recConteudoMetodo();
                break;
            case "id":
                recComando();
                recConteudoMetodo();
                break;
            default:
                break;
        }
    }

    private void recComando() {
        switch (proximo.getValor()) {
            case "read":
                recRead();
                break;
            case "write":
                recWrite();
                break;
            case "new":
                recInicializaObjeto();
                recInicializaObjeto();
                break;
            case "if":
                recIf();
                break;
            case "while":
                recWhile();
                break;
            case "char":
                terminal("char");
                Tipo("id");
                recIdDecl();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                recIdDecl();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                recIdDecl();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                recIdDecl();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                recIdDecl();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    if (proximo.getTipo().equals("id")) {
                        Tipo("id");
                        recIdDecl();
                    } else {
                        recIdComando();
                    }

                } else {
                    erroSintatico("comando com erro.");
                }
                break;

        }
    }

    private void recIdDecl() {
        switch (proximo.getValor()) {
            case ",":
                recListaVariavel();
                break;
            case "[":
                terminal("[");
                recListaVetor();
                terminal("]");
                break;
            default:
                erroSintatico("Erro na declaração de variavel");
                break;
        }
    }

    private void recIdComando() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                terminal(";");
                break;
            case ".":
                terminal(".");
                Tipo("id");
                recAcessoObjeto();
                terminal(";");
                break;
            case "=":
                terminal("=");
                recAtribuicao();
                terminal(";");
                break;
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                terminal("=");
                recAtribuicao();
                terminal(";");
                break;
            default:
                erroSintatico("Erro de Comando");
                break;
        }
    }

    private void recAcessoObjeto() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                break;
            case "=":
                terminal("=");
                recAtribuicao();
                break;
            default:
                erroSintatico("Erro de acesso a objeto");
                break;
        }
    }

    private void recAtribuicao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOperadorNumero() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recNegativo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void recExpRelacionalOpcional() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void recOpLogico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    private void recRetorno() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    private void recIdAcesso() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    private void recAcesso() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    private void recOperacao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void recChamadaMetodo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void recOperador() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recParametros() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recNovoParametro() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recInicializaObjeto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recWhile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recRead() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recListaRead() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recWrite() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recParametrosWrite() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recNovoParametroWrite() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recImprimiveis() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOpWrite() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recIf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recComplementoIf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recConteudoEstrutura() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recComandoEstrutura() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recExp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recComplementoAritmetico1() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recExplogica() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recCoOpRelacional() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recComplementoExpLogica() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recIdExp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOpIdLogico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recComplementoLogico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOperadorLogico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOperadorIgualdade() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOpRelacional() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOpIdRelacional() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recOperadorRelacional() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recExpAritmetica() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recFatorAritmetico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recIdAritmetico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recIdExpArit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void recComplementoAritmetico() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
