package org.trabalho;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Parâmetros iniciais
        int numClientes = 5; // Número de clientes
        int numTrabalhadoras = 1; // Número de threads trabalhadoras
        int intervalo = 1000; // Intervalo para cada operação
        int limiteRequisicoes = 3; // Limite de requisições por cliente
        int numContas = 10; // Número de contas a serem criadas
        int quantidadeMostrarBalanco = 5; //Número de requisições para mostrar balanço geral

        // Populando a lista de contas
        List<Conta> contas = new ArrayList<>();
        for (int i = 0; i < numContas; i++) {
            contas.add(new Conta(i, 1000));
        }

        // Criando threads trabalhadoras
        List<Trabalhadora> trabalhadoras = new ArrayList<>();
        for (int i = 0; i < numTrabalhadoras; i++) {
            Trabalhadora trabalhadora = new Trabalhadora();
            trabalhadora.start();
            trabalhadoras.add(trabalhadora);
        }

        // Iniciando o servidor
        Servidor servidor = new Servidor(trabalhadoras, contas, quantidadeMostrarBalanco);
        servidor.start();

        // Criando e iniciando os clientes
        for (int i = 0; i < numClientes; i++) {
            Cliente cliente = new Cliente(servidor, contas, intervalo, limiteRequisicoes);
            cliente.start();
        }
    }
}