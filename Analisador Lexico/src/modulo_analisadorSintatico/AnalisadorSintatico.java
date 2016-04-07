/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSintatico;

import java.util.ArrayList;
import modulo_analisadorLexico.Token;
import modulo_completo.Compilador;
import modulo_completo.Simbolos;

/**
 *  Classe responsável pela análise sintatica dos códigos fontes. 
 * @author Lucas Carneiro
 * @author Oto Lopes
 * 
 * @see Token
 * @see Compilador
 */
public class AnalisadorSintatico {

    private Token proximo;              //token atual em análise
    private ArrayList<Token> tokens;    //lista com os tokens recebidos
    private ArrayList<String> erros;    //lista com os erros encontrados na análise.
    private int contTokens = 0;         //contador que aponta para o proximo token da lista
    private Compilador comp;            //compilador com a lista de simbolos
    private Simbolos classEscopo;       //salvar a classe atual
    private Simbolos escopo;            //salvar o método atual 
    private Simbolos atual;             //simbolo atual

    public AnalisadorSintatico(Compilador comp) {
        this.comp = comp;
    }

    /**
     *
     * @param tokens
     */
    public void analise(ArrayList<Token> tokens) {
        this.tokens = tokens;
        proximo = proximo();
        erros = new ArrayList<>();
        recArquivo();
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getErros() {
        return erros;
    }

    private Token proximo() {
        if (contTokens < tokens.size()) {
            return tokens.get(contTokens++);
        } else {
            return new Token("EOF", "EOF", 0, 0);
        }
    }

    private void erroSintatico(String erro) {
        if (!proximo.getValor().equals("EOF")) {
            erros.add("Erro na linha " + proximo.getLinha() + " coluna " + proximo.getColuna() + ". " + erro + "\n");
        } else {
            erros.add(erro);
        }
    }

    private void terminal(String esperado) {
        if ((!proximo.getValor().equals("EOF")) && proximo.getValor().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Token " + esperado + " esperado.");
        }
    }

    private void Tipo(String esperado) {
        if (!proximo.getValor().equals("EOF") && proximo.getTipo().equals(esperado)) {
            proximo = proximo();
        } else {
            erroSintatico("Token do tipo " + esperado + " esperado.");
        }
    }

    private void recArquivo() {
        recConstantes();
        recVariaveis();
        recPreMain();
    }

    private void recPreMain() {
        System.out.println("pre-main");
        if (!proximo.getValor().equals("EOF")) {
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
                    while (!proximo.getValor().equals("void") && !proximo.getValor().equals("class")) {
                        erroSintatico("Espera uma classe ou o método main");
                        proximo = proximo();
                    }
                    recPreMain();
                    break;
            }
        } else {
            erroSintatico("Fim de arquivo inesperado");
        }
    }

    private void recClasses() {
        switch (proximo.getValor()) {
            case "class":
                recClasse();
                recClasses();
                break;
            default:
                break;
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
        System.out.println("main");
        escopo = new Simbolos(Simbolos.MAIN, Simbolos.VOID, "main", null);
        terminal("void");
        terminal("main");
        terminal("(");
        terminal(")");
        terminal("{");
        recConteudoMetodo();
        terminal("}");
        comp.addSimbolo(escopo);
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
                break;
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
                System.out.println(proximo.getValor());
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
                break;
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
            case "caractere_constante":
                Tipo("caractere_constante");
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
                break;
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
                erroSintatico("Erro na declaração de variavel, esperava ; ou ,");
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
                while (!proximo.getValor().equals(",") && !proximo.getValor().equals(";") && !proximo.getTipo().equals("palavra_reservada")) {
                    erroSintatico("Erro na declaração de variavel");
                    proximo = proximo();
                }
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
        System.out.println("conteudoMetodo");
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                if (proximo.getValor().equals("return")) {
                    break;
                }
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
                recIndice();
                terminal("]");
                recListaVetor();
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
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recAtribuicao();
                terminal(")");
                recOperacao();
                break;
            case "++":
                recIdAcesso();
                break;
            case "--":
                recIdAcesso();
                break;
            case "true":
                terminal("true");
                recOpLogico();
                break;
            case "-":
                terminal("-");
                recNegativo();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        recIdAcesso();
                        break;
                    case "numero":
                        Tipo("numero");
                        recOperadorNumero();
                        break;
                    case "cadeia_constante":
                        Tipo("cadeia_constante");
                        break;
                    case "caractere_constante":
                        Tipo("caractere_constante");
                        break;
                    default:
                        erroSintatico("Atribuição errada");
                        break;
                }
        }

    }

    private void recOperadorNumero() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "+":
                terminal("+");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "/":
                terminal("/");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "==":
                terminal("==");
                recExpAritmetica();
                recOpLogico();
                break;
            case "!=":
                terminal("!=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                break;
        }
    }

    private void recNegativo() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recNegativo();
                terminal(")");
                break;
            case "++":
                recIdAcesso();
                break;
            case "--":
                recIdAcesso();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "numero":
                        Tipo("numero");
                        recOperadorNumero();
                        break;
                    case "id":
                        recIdAcesso();
                        break;
                    default:
                        erroSintatico("Expressão mal formada");
                        break;

                }

        }
    }

    private void recExpRelacionalOpcional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                break;
        }
    }

    private void recOpLogico() {
        switch (proximo.getValor()) {
            case "==":
                terminal("==");
                recExplogica();
                break;
            case "!=":
                terminal("!=");
                recExplogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recRetorno() {
        recAtribuicao();
        terminal(";");
    }

    private void recIdAcesso() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                Tipo("id");
                recOperacao();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recOperacao();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        Tipo("id");
                        recAcesso();
                        recOperacao();
                        break;
                    default:
                        erroSintatico("Atribuição com erro");
                }

        }
    }

    private void recAcesso() {
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
                break;
        }
    }

    private void recOperacao() {
        switch (proximo.getValor()) {
            case ">":
                recOperador();
                break;
            case "<":
                recOperador();
                break;
            case ">=":
                recOperador();
                break;
            case "<=":
                recOperador();
                break;
            case "+":
                recOperador();
                break;
            case "-":
                recOperador();
                break;
            case "*":
                recOperador();
                break;
            case "/":
                recOperador();
                break;
            case "==":
                recOperador();
                break;
            case "!=":
                recOperador();
                break;
            case "&&":
                recOperador();
                break;
            case "||":
                recOperador();
                break;
            default:
                break;
        }
    }

    private void recChamadaMetodo() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                break;
            default:
                break;

        }
    }

    private void recOperador() {
        switch (proximo.getValor()) {

            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "+":
                terminal("+");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "/":
                terminal("/");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "==":
                terminal("==");
                recAtribuicao();
                break;
            case "!=":
                terminal("!=");
                recAtribuicao();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                erroSintatico("Falta operador");
                break;
        }
    }

    private void recParametros() {
        switch (proximo.getValor()) {
            case "(":
                recAtribuicao();
                recNovoParametro();
                break;
            case "true":
                recAtribuicao();
                recNovoParametro();
                break;
            case "false":
                recAtribuicao();
                recNovoParametro();
                break;
            case "-":
                recAtribuicao();
                recNovoParametro();
                break;
            case "++":
                recAtribuicao();
                recNovoParametro();
                break;
            case "--":
                recAtribuicao();
                recNovoParametro();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "numero":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "cadeia_constante":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "caractere_constante":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void recNovoParametro() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recParametros();
                break;
            default:
                break;
        }
    }

    private void recInicializaObjeto() {
        terminal("new");
        Tipo("id");
        terminal(";");
    }

    private void recWhile() {
        terminal("while");
        terminal("(");
        recExp();
        terminal(")");
        terminal("{");
        recConteudoEstrutura();
        terminal("}");
    }

    private void recRead() {
        terminal("read");
        terminal("(");
        Tipo("id");
        recListaRead();
        terminal(")");
        terminal(";");
    }

    private void recListaRead() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                Tipo("id");
                recListaRead();
                break;
            default:
                break;
        }
    }

    private void recWrite() {
        terminal("write");
        terminal("(");
        recParametrosWrite();
        terminal(")");
        terminal(";");
    }

    private void recParametrosWrite() {
        recImprimiveis();
        recNovoParametroWrite();
    }

    private void recNovoParametroWrite() {

        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recParametrosWrite();
                break;
            default:
                break;
        }
    }

    private void recImprimiveis() {
        switch (proximo.getTipo()) {
            case "id":
                Tipo("id");
                recOpWrite();
                break;
            case "numero":
                Tipo("numero");
                recOpWrite();
                break;
            case "cadeia_constante":
                Tipo("cadeia_constante");
                break;
            case "caractere_constante":
                Tipo("caractere_constante");
                break;
            default:
                switch (proximo.getValor()) {
                    case "(":
                        terminal("(");
                        recImprimiveis();
                        terminal(")");
                        break;
                    default:
                            erroSintatico("Parametro incompativel com método write");
                        break;
                }

        }
    }

    private void recOpWrite() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recExpAritmetica();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                break;
            case "/":
                terminal("");
                recExpAritmetica();
                break;
            default:
                break;
        }
    }

    private void recIf() {
        terminal("if");
        terminal("(");
        recExp();
        terminal(")");
        terminal("{");
        recConteudoEstrutura();
        terminal("}");
        recComplementoIf();
    }

    private void recComplementoIf() {
        switch (proximo.getValor()) {
            case "else":
                terminal("else");
                terminal("{");
                recConteudoEstrutura();
                terminal("}");
                break;
            default:
                break;
        }
    }

    private void recConteudoEstrutura() {
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                recComandoEstrutura();
                recConteudoEstrutura();
                break;
            case "id":
                recComandoEstrutura();
                recConteudoEstrutura();
                break;
            default:
                break;
        }
    }

    private void recComandoEstrutura() {
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
                    recIdComando();

                } else {
                    erroSintatico("comando com erro.");
                }
                break;

        }
    }

    private void recExp() {
        switch (proximo.getValor()) {
            case "true":
                terminal("true");
                recComplementoLogico();
                break;
            case "false":
                terminal("false");
                recComplementoLogico();
                break;
            case "++":
                terminal("++");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico1();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico1();
                break;
            case "(":
                terminal("(");
                recExp();
                terminal(")");
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        Tipo("id");
                        recIdExpArit();
                        recComplementoAritmetico1();
                        break;
                    case "numero":
                        Tipo("numero");
                        recComplementoAritmetico();
                        recOpRelacional();
                        break;
                    default:
                        erroSintatico("Expressão invalida");
                        break;
                }

        }
    }

    private void recComplementoAritmetico1() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            default:
                recOpIdLogico();
                break;

        }
    }

    private void recExplogica() {
        switch (proximo.getValor()) {
            case "true":
                terminal("true");
                recComplementoLogico();
                break;
            case "false":
                terminal("false");
                recComplementoLogico();
                break;
            case "++":
                terminal("++");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico();
                recOpIdLogico();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico();
                recOpIdLogico();
                break;
            case "(":
                terminal("(");
                recExplogica();
                terminal(")");
                recComplementoExpLogica();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        Tipo("id");
                        recIdExpArit();
                        recComplementoAritmetico();
                        recOpIdLogico();
                        break;
                    case "numero":
                        Tipo("numero");
                        recComplementoAritmetico();
                        recCoOpRelacional();
                        break;
                    default:
                        erroSintatico("Expressão invalida");
                        break;
                }

        }
    }

    private void recCoOpRelacional() {
        switch (proximo.getValor()) {
            case ">":
                recOpRelacional();
                break;
            case "<":
                recOpRelacional();
                break;
            case ">=":
                recOpRelacional();
                break;
            case "<=":
                recOpRelacional();
                break;
            case "==":
                recOpRelacional();
                break;
            case "!=":
                recOpRelacional();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recComplementoExpLogica() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "<":
                terminal("<");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case ">=":
                terminal(">=");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "<=":
                terminal("<=");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "==":
                terminal("==");
                recExplogica();
                break;
            case "!=":
                terminal("!=");
                recExplogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            case "+":
                terminal("+");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            default:
                break;
        }
    }

    private void recIdExp() {
        switch (proximo.getValor()) {
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
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                break;
            default:
                break;
        }
    }

    private void recOpIdLogico() {
        switch (proximo.getValor()) {
            case ">":
                recOpIdRelacional();
                break;
            case "<":
                recOpIdRelacional();
                break;
            case ">=":
                recOpIdRelacional();
                break;
            case "<=":
                recOpIdRelacional();
                break;
            case "==":
                recOpIdRelacional();
                break;
            case "!=":
                recOpIdRelacional();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recComplementoLogico() {
        switch (proximo.getValor()) {
            case "==":
                terminal("==");
                recExplogica();
                break;
            case "!=":
                terminal("!=");
                recExplogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recOpRelacional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "==":
                terminal("==");
                recExpAritmetica();
                recOpLogico();
                break;
            case "!=":
                terminal("!=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                erroSintatico("falta operador");
                break;
        }
    }

    private void recOpIdRelacional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "==":
                terminal("==");
                recExplogica();
                break;
            case "!=":
                terminal("!=");
                recExplogica();
                break;
            default:
                erroSintatico("falta operador");
                break;
        }
    }

    private void recExpAritmetica() {
        switch (proximo.getValor()) {
            case "++":
                recFatorAritmetico();
                break;
            case "--":
                recFatorAritmetico();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "(":
                recFatorAritmetico();
                break;
            default:
                if (proximo.getTipo().equals("id") || proximo.getTipo().equals("numero")) {
                    recFatorAritmetico();
                    break;
                }
                erroSintatico("Expressão com erro");
                break;
        }

    }

    private void recFatorAritmetico() {
        switch (proximo.getValor()) {
            case "++":
                recIdAritmetico();
                recComplementoAritmetico();
                break;
            case "--":
                recIdAritmetico();
                recComplementoAritmetico();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "(":
                recFatorAritmetico();
                recComplementoAritmetico();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    recIdAritmetico();
                    recComplementoAritmetico();
                    break;
                } else if (proximo.getTipo().equals("numero")) {
                    Tipo("numero");
                    recComplementoAritmetico();
                }
                erroSintatico("Expressão com erro");
                break;
        }
    }

    private void recIdAritmetico() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                Tipo("id");
                break;
            case "--":
                terminal("--");
                Tipo("id");
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    recIdExpArit();
                }
                erroSintatico("Espera um identificador");
                break;
        }
    }

    private void recIdExpArit() {
        switch (proximo.getValor()) {
            case "(":
                recIdExp();
                break;
            case ".":
                recIdExp();
                break;
            case "[":
                recIdExp();
                break;
            case "++":
                terminal("++");
                break;
            case "--":
                terminal("--");
                break;
            default:
                break;
        }
    }

    private void recComplementoAritmetico() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                recFatorAritmetico();
                break;
            case "--":
                terminal("--");
                recFatorAritmetico();
                break;
            default:
                break;
        }
    }

}
