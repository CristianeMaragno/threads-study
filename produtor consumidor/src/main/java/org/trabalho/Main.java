package org.trabalho;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Parâmetros iniciais
        int numClientes = Integer.parseInt(args[0]); // Número de clientes args[0]
        int numTrabalhadoras = Integer.parseInt(args[1]); // Número de threads trabalhadoras args[1]
        int intervalo = Integer.parseInt(args[2]); // Intervalo para cada operação
        int limiteRequisicoes = Integer.parseInt(args[3]); // Limite de requisições por cliente
        int numContas = Integer.parseInt(args[4]); // Número de contas a serem criadas
        int quantidadeMostrarBalanco = Integer.parseInt(args[5]); //Número de requisições para mostrar balanço geral

        // Populando a lista de contas
        List<Conta> contas = new ArrayList<>();
        for (int i = 0; i < numContas; i++) {
            contas.add(new Conta(i, 1000));
        }

        // Iniciando o servidor
        Servidor servidor = new Servidor(contas, quantidadeMostrarBalanco);
        servidor.start();

        // Criando threads trabalhadoras
        List<Trabalhadora> trabalhadoras = new ArrayList<>();
        for (int i = 0; i < numTrabalhadoras; i++) {
            Trabalhadora trabalhadora = new Trabalhadora(i, servidor, contas, intervalo);
            trabalhadora.start();
            trabalhadoras.add(trabalhadora);
        }

        servidor.setTrabalhadoras(trabalhadoras);

        // Criando e iniciando os clientes
        for (int i = 0; i < numClientes; i++) {
            Cliente cliente = new Cliente(servidor, contas, intervalo, limiteRequisicoes);
            cliente.start();
        }
    }
}