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
                Operacao.Tipo[] tiposPermitidos = {Operacao.Tipo.DEPOSITO, Operacao.Tipo.TRANSFERENCIA};
                Operacao.Tipo tipo = tiposPermitidos[random.nextInt(tiposPermitidos.length)];

                Conta contaOrigem = contas.get(random.nextInt(contas.size()));
                Conta contaDestino;
                //Para garantir que a conta de destino é sempre diferente
                do {
                    contaDestino = contas.get(random.nextInt(contas.size()));
                } while (contaDestino.equals(contaOrigem));

                int valor = random.nextInt(100);
                //Para possibilitar valores negativos quando a operação é de depósito
                if(tipo == Operacao.Tipo.DEPOSITO){
                    valor = valor - 50;
                }

                Operacao operacao = new Operacao(tipo, contaOrigem.getId(), contaDestino.getId(), valor);
                servidor.adicionarRequisicao(operacao);

                // Esperar o intervalo antes da próxima requisição
                Thread.sleep(intervaloRequisicao);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}