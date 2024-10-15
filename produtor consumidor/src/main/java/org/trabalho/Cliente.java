package org.trabalho;

import java.util.List;
import java.util.Random;

class Cliente extends Thread {
    private final Servidor servidor;
    private final List<Conta> contas;
    private final Random random = new Random();
    private final int intervaloRequisicao;
    private final int limiteRequisicoes;

    public Cliente(Servidor servidor, List<Conta> contas, int intervaloRequisicao, int limiteRequisicoes) {
        this.servidor = servidor;
        this.contas = contas;
        this.intervaloRequisicao = intervaloRequisicao;
        this.limiteRequisicoes = limiteRequisicoes;
    }

    @Override
    public void run() {
        for (int i = 0; i < limiteRequisicoes; i++) {
            try {
                // Gerar uma operação aleatória
                int operacao = random.nextInt(3);
                if (operacao == 0) {
                    // Exibir balanço geral
                    Conta conta = contas.get(random.nextInt(contas.size()));
                    servidor.adicionarRequisicao(() -> conta.toString());
                } else if (operacao == 1) {
                    // Fazer depósito
                    Conta conta = contas.get(random.nextInt(contas.size()));
                    int valor = random.nextInt(100) - 50; // Depósito positivo ou negativo
                    servidor.adicionarRequisicao(() -> conta.depositar(valor));
                } else {
                    // Transferência entre contas
                    Conta conta1 = contas.get(random.nextInt(contas.size()));
                    Conta conta2 = contas.get(random.nextInt(contas.size()));
                    int valor = random.nextInt(100);
                    servidor.adicionarRequisicao(() -> conta1.transferir(conta2, valor));
                }
                // Esperar o intervalo antes da próxima requisição
                Thread.sleep(intervaloRequisicao);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}