package org.trabalho;

import java.util.ArrayList;
import java.util.List;

class Servidor extends Thread {
    private final List<Operacao> filaRequisicoes = new ArrayList<>();
    private List<Trabalhadora> trabalhadoras;

    private final List<Conta> contas;

    private int contadorRequisicoes = 0;

    private int quantidadeMostrarBalanco;

    private volatile boolean running = true;

    public Servidor(List<Conta> contas, int quantidadeMostrarBalanco) {
        this.contas = contas;
        this.quantidadeMostrarBalanco = quantidadeMostrarBalanco;
    }

    public boolean encerrar() {
        if(!finalizouTodasOperacoes()){
            return false;
        }else{
            // Encerra as trabalhadoras
            for (Trabalhadora trabalhadora : trabalhadoras) {
                trabalhadora.encerrar();
            }
            for (Trabalhadora trabalhadora : trabalhadoras) {
                try {
                    trabalhadora.join(5000); // Espera até 5 segundos por trabalhadora
                    if (trabalhadora.isAlive()) {
                        System.out.println("Trabalhadora " + trabalhadora.getId() + " não encerrou no tempo esperado!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            running = false;
            System.out.println("Servidor encerrando...");
            return true;
        }

    }

    public synchronized boolean finalizouTodasOperacoes() {
        return filaRequisicoes.isEmpty() && trabalhadoras.stream().allMatch(t -> t.estaLivre());
    }

    public void setTrabalhadoras(List<Trabalhadora> trabalhadoras) {
        this.trabalhadoras = trabalhadoras;
    }

    public synchronized void adicionarRequisicao(Operacao operacao) {
        //System.out.println("Adiciona na lista operacao de " + operacao.getTipo());
        if(operacao.getTipo() != Operacao.Tipo.EXIBIR_BALANCO) {
            contadorRequisicoes++;
        }
        filaRequisicoes.add(operacao);
        if (contadorRequisicoes == quantidadeMostrarBalanco) {
            // Sincroniza para exibir o balanço sem alterações
            contadorRequisicoes = 0; // Reinicia o contador
            Operacao operacaoBalanco = new Operacao(Operacao.Tipo.EXIBIR_BALANCO);
            this.adicionarRequisicao(operacaoBalanco);

        }
        notifyAll();
    }

    private synchronized Operacao pegarRequisicao() throws InterruptedException {
        while (filaRequisicoes.isEmpty()) {
            wait();
        }
        return filaRequisicoes.remove(0);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Enviar para uma trabalhadora livre
                for (Trabalhadora trabalhadora : trabalhadoras) {
                    if (trabalhadora.estaLivre()) {
                        Operacao requisicao = pegarRequisicao();
                        //System.out.println("Servidor: Requisição obtida - Tipo: " + requisicao.getTipo());
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