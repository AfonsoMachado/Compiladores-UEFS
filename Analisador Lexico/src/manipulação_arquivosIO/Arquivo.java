package manipulação_arquivosIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import modulo_analisadorLexico.Token;

/**
 * Classe destinada a manipulação com o arquivo de código fonte e saída, ou
 * seja, responsável por: gerar a lista com os caracteres provindos do código
 * fonte e gerar o arquivo de saída com os erros e os tokens.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 */
public class Arquivo {

    /**
     * Nome do arquivo lido.
     */
    private String localFile;

    /**
     * Busca e retorna todos os códigos fonte presentes na pasta
     * <i>/src/testes/in/</i>.
     *
     * @return Lista com os nomes dos códigos fonte presentes na pasta
     * <i>/src/testes/in/</i>
     */
    public ArrayList<String> lerCodigos() {

        File raiz = new File("src/testes/in/"); // Pasta com os códigos de entrada.
        ArrayList<String> codigos = new ArrayList<>(); // Nomes dos arquivos com os códigos de entrada.
        for (File f : raiz.listFiles()) { // Inserindo caminho dos códigos.
            codigos.add(f.getName());
        }

        /*if (codes.isEmpty()) { // Pasta de códigos de entrada vazia.
            JOptionPane.showMessageDialog(null, "Sem Códigos para Compilar");
            System.exit(0);
        }*/

        return codigos;
    }

    /**
     * Retorna a lista de strings obtidas a partir de um arquivo com o código
     * fonte.
     *
     * @param localFile Nome do arquivo do código que será lido
     *
     * @return Lista com as strings contidas no arquivo com o código
     *
     * @throws FileNotFoundException Erro com a leitura do arquivo com o código
     */
    public ArrayList<String> lerCodigoFonte(String localFile) throws FileNotFoundException {

        Scanner scanner = new Scanner(new FileReader("src/testes/in/" + localFile)).useDelimiter("\n"); // Lendo o arquivo do código.
        this.localFile = localFile; // Guarda o nome do arquivo de entrada para que o arquivo de saída tenha o "mesmo" nome.
        ArrayList<String> codigo = new ArrayList(); // Código obtido;
        while (scanner.hasNext()) { // Capturando as linhas do código.
            codigo.add(scanner.next());
        }

        return codigo;
    }

    /**
     * Gera o arquivo de saída após a análise do código fonte. Neste arquivo de
     * saída conterá todos os tokens encontrados no código fonte e os erros
     * encontrados (se houver).
     *
     * @param tokens Lista de tokens obtidos após a análise do código fonte
     * @param erros Erros obtidos após a análise do código fonte
     * 
     * @throws IOException Arquivo de saida não foi gerado com sucesso
     */
    public void escreverSaidaLexico(ArrayList<Token> tokens, ArrayList<String> erros) throws IOException {

        FileWriter arq = new FileWriter("src/testes/out/" + this.localFile + ".out", false); // Cria o arquivo de saída relacionado ao seu respectivo arquivo de entrada ("mesmo" nome). 

        PrintWriter gravar = new PrintWriter(arq);
        for (Token token : tokens) { // Insere os tokens no arquivo de saída.
            gravar.println(token.getTipo() + "#" + token.getValor() + "#" + token.getLinha() + ":" + token.getColuna());
        }
        if (erros.isEmpty()) { // Se não houver erros léxicos, imprime sucesso.
            gravar.printf("\nParabens, código compilado com successo\n");
        } else { // Se houver erros léxicos, os insere no arquivo de saída.
            for (String erro : erros) {
                gravar.printf(erro);
            }
        }
        arq.close();
    }
}