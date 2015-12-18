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
     * Localização do arquivo lido. Esta localização será a mesma para o arquivo
     * de saída.
     */
    private String localFile;

    /**
     * 
     * @return 
     */
    public ArrayList<String> lerCodigos() {

        ArrayList<String> codes = new ArrayList<>();
        File raiz = new File("src/testes/in/");
        System.out.println(raiz.getAbsolutePath());
        
        for(File f: raiz.listFiles()) {
            codes.add(f.getName());
        }
        
        return codes;
    }

    /**
     * Retorna a lista de strings obtidas a partir de um arquivo com o código
     * fonte.
     *
     * @param localFile Localização do arquivo do código que será lido
     *
     * @return Lista com as strings contidas no arquivo com o código
     *
     * @throws FileNotFoundException Erro com a leitura do arquivo com o código
     */
    public ArrayList<String> lerCodigoFonte(String localFile) throws FileNotFoundException {

        Scanner scanner = new Scanner(new FileReader("src/testes/in/" + localFile)).useDelimiter("\n");
        this.localFile = localFile;
        ArrayList<String> codigo = new ArrayList();
        while (scanner.hasNext()) {
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
     */
    public void escreverSaidaLexico(ArrayList<Token> tokens, ArrayList<String> erros) {
        FileWriter arq;
        try {
            arq = new FileWriter("src/testes/out/" + this.localFile + ".out", false);

            PrintWriter gravar = new PrintWriter(arq);

            for (Token token : tokens) {
                gravar.println(token.getTipo() + "#" + token.getValor() + "#" + token.getLinha() + ":" + token.getColuna());
            }
            if (erros.isEmpty()) {
                gravar.printf("\nParabens, código compilado com successo\n");
            } else {

                for (String erro : erros) {
                    gravar.printf(erro);
                }
            }
            arq.close();
        } catch (IOException ex) {
            System.out.println("Arquivo de saida não foi gerado com sucesso.");
        }
    }
}
