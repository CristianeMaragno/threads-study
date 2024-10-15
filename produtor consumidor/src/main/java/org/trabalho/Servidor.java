package org.trabalho;

import java.util.ArrayList;
import java.util.List;

class Servidor extends Thread {
    private final List<Runnable> filaRequisicoes = new ArrayList<>();
    private final List<Trabalhadora> trabalhadoras;

    private final List<Conta> contas;

    private int contadorRequisicoes = 0;

    private int quantidadeMostrarBalanco;

    public Servidor(List<Trabalhadora> trabalhadoras, List<Conta> contas, int quantidadeMostrarBalanco) {
        this.trabalhadoras = trabalhadoras;
        this.contas = contas;
        this.quantidadeMostrarBalanco = quantidadeMostrarBalanco;
    }

    public synchronized void adicionarRequisicao(Runnable requisicao) {
        filaRequisicoes.add(requisicao);
        notifyAll();
    }

    private synchronized Runnable pegarRequisicao() throws InterruptedException {
        while (filaRequisicoes.isEmpty()) {
            wait();
        }
        return filaRequisicoes.remove(0);
    }

    private void exibirBalançoContas() {
        System.out.println("=== Balanço de todas as contas ===");
        for (Conta conta : contas) {
            System.out.println(conta);
        }
        System.out.println("==================================");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable requisicao = pegarRequisicao();
                // Enviar para uma trabalhadora livre
                for (Trabalhadora trabalhadora : trabalhadoras) {
                    if (trabalhadora.estaLivre()) {
                        trabalhadora.executar(requisicao);
                        break;
                    }
                }

                contadorRequisicoes++;

                // A cada x requisições processadas, exibe o balanço de todas as contas
                if (contadorRequisicoes >= this.quantidadeMostrarBalanco) {
                    exibirBalançoContas();
                    contadorRequisicoes = 0; // Reinicia o contador
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}