package org.trabalho;

import java.util.List;

class Trabalhadora extends Thread {
    private boolean livre = true;
    private Operacao tarefaAtual;

    private int id;

    private final Servidor servidor;

    private final List<Conta> contas;

    private final int intervaloOperacao;

    public Trabalhadora(int id, Servidor servidor, List<Conta> contas, int intervaloOperacao){
        this.id = id;
        this.servidor = servidor;
        this.contas = contas;
        this.intervaloOperacao = intervaloOperacao;
    }

    public synchronized boolean estaLivre() {
        return livre;
    }

    public synchronized void executar(Operacao tarefa) {
        this.tarefaAtual = tarefa;
        this.livre = false;
        notifyAll();
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (livre) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Executa a tarefa
            this.processarOperacao(this.tarefaAtual);
            /*try {
                Thread.sleep(this.intervaloOperacao);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            servidor.notificarOperacaoConcluida(this.tarefaAtual.getTipo());

            // Volta a ficar livre
            synchronized (this) {
                livre = true;
            }
        }
    }

    public synchronized void processarOperacao(Operacao operacao) {
        System.out.println("Thread " + this.id + " executando... Operação " + operacao.getTipo()); //Operação " + operacao.getTipo() + " contas: " + tarefaAtual.getContaOrigemId() + " " + tarefaAtual.getContaDestinoId()
        switch (operacao.getTipo()) {
            case EXIBIR_BALANCO:
                this.mostrarBalanco();
                break;
            case DEPOSITO:
                Conta contaDeposito = this.contas.get(tarefaAtual.getContaOrigemId());
                float novoSaldo = contaDeposito.getSaldo() + tarefaAtual.getValor();
                System.out.println("D(" + tarefaAtual.getValor() + ") Conta " + tarefaAtual.getContaOrigemId() + " - valor antes: " + contaDeposito.getSaldo() + "/valor depois: " + novoSaldo);
                contaDeposito.depositar(tarefaAtual.getValor());
                break;
            case TRANSFERENCIA:
                Conta contaOrigem = contas.get(tarefaAtual.getContaOrigemId());
                Conta contaDestino = contas.get(tarefaAtual.getContaDestinoId());
                System.out.println("T(" + tarefaAtual.getValor() + ") Conta " + tarefaAtual.getContaOrigemId() + "(" + contaOrigem.getSaldo() + ") para Conta " + tarefaAtual.getContaDestinoId() + "(" + contaDestino.getSaldo() + ")");
                contaOrigem.transferir(tarefaAtual.getValor());
                contaDestino.receberTransferencia(tarefaAtual.getValor());
                System.out.println("Conta " + tarefaAtual.getContaOrigemId() + " - Saldo atualizado: " + contaOrigem.getSaldo());
                System.out.println("Conta " + tarefaAtual.getContaDestinoId() + " - Saldo atualizado: " + contaDestino.getSaldo());
                break;
        }
    }

    private synchronized void mostrarBalanco(){
        System.out.println("=== Balanço de todas as contas ===");
        for (Conta conta : contas) {
            System.out.println(conta);
        }
        System.out.println("==================================");
    }
}