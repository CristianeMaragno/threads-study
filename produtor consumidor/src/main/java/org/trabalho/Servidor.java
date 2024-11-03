package org.trabalho;

import java.util.ArrayList;
import java.util.List;

class Servidor extends Thread {
    private final List<Operacao> filaRequisicoes = new ArrayList<>();
    private List<Trabalhadora> trabalhadoras;

    private final List<Conta> contas;

    private int contadorRequisicoes = 0;

    private int quantidadeMostrarBalanco;

    public Servidor(List<Conta> contas, int quantidadeMostrarBalanco) {
        this.contas = contas;
        this.quantidadeMostrarBalanco = quantidadeMostrarBalanco;
    }

    public void setTrabalhadoras(List<Trabalhadora> trabalhadoras) {
        this.trabalhadoras = trabalhadoras;
    }

    public synchronized void adicionarRequisicao(Operacao operacao) {
        filaRequisicoes.add(operacao);
        notifyAll();
    }

    private synchronized Operacao pegarRequisicao() throws InterruptedException {
        while (filaRequisicoes.isEmpty()) {
            wait();
        }
        return filaRequisicoes.remove(0);
    }

    public synchronized void notificarOperacaoConcluida(Operacao.Tipo tipo) {
        if(tipo != Operacao.Tipo.EXIBIR_BALANCO){
            contadorRequisicoes++;

            if (contadorRequisicoes >= quantidadeMostrarBalanco) {
                // Sincroniza para exibir o balanço sem alterações
                Operacao operacao = new Operacao(Operacao.Tipo.EXIBIR_BALANCO);
                this.adicionarRequisicao(operacao);
                contadorRequisicoes = 0; // Reinicia o contador
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Operacao requisicao = pegarRequisicao();
                // Enviar para uma trabalhadora livre
                for (Trabalhadora trabalhadora : trabalhadoras) {
                    if (trabalhadora.estaLivre()) {
                        trabalhadora.executar(requisicao);
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}