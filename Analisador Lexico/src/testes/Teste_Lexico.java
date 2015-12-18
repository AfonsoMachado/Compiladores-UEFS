package testes;

public class Teste_Lexico {

    public static void main(String[] args) {
        int i = 10, j;

        j = --i - 5; // op(--) id(i) op(-) nro(5)   
        System.out.println("teste 1 -> i = " + i + " j = " + j);

        i = 10;
        j = i-- - 5; // id(i) op(--) op(-) nro(5)   
        System.out.println("teste 2 -> i = " + i + " j = " + j);

        i = 10;
        j = i-- - 5; // id(i) op(--) op(-) nro(5)
        System.out.println("teste 3 -> i = " + i + " j = " + j);

        i = 10;
        j = i - -   5; // id(i) op(-) nro(-5)   
        System.out.println("teste 4 -> i = " + i + " j = " + j);

        i = 10;
        j = i- -5; // id(i) op(-) nro(-5)   
        System.out.println("teste 5 -> i = " + i + " j = " + j);

        i = 10;
        j = i - -5 - 1; // id(i) op(-) nro(-5) op(-) nro(1)  
        System.out.println("teste 6 -> i = " + i + " j = " + j);

        i = 10;
        j = i - - 5 - 1; // id(i) op(-) nro(-5) op(-) nro(1)  
        System.out.println("teste 7 -> i = " + i + " j = " + j);

        i = 10;
        j = i - 1; // id(i) op(-) nro(1)
        System.out.println("teste 8 -> i = " + i + " j = " + j);

        i = 10;
        j = i-- - - 5; // id(i) op(--) op(-) nro(-5)  
        System.out.println("teste 9 -> i = " + i + " j = " + j);   
        
        
        //j = i --5; // id(i) op(--) nro(5)   
        
    }
}
