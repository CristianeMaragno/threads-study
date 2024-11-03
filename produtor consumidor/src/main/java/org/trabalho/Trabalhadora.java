package org.trabalho;

import java.util.List;

class Trabalhadora extends Thread {
    private boolean livre = true;
    private Operacao tarefaAtual;

    private final int id;

    private final Servidor servidor;

    private final List<Conta> contas;

    private final int intervaloOperacao;

    private volatile boolean running = true;

    public Trabalhadora(int id, Servidor servidor, List<Conta> contas, int intervaloOperacao){
        this.id = id;
        this.servidor = servidor;
        this.contas = contas;
        this.intervaloOperacao = intervaloOperacao;
    }

    public long getId() {
        return this.id;
    }

    public void encerrar() {
        running = false;
        interrupt(); // Interrompe a thread se estiver esperando
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
        try {
            while (running || tarefaAtual != null) {
                synchronized (this) {
                    while (livre) {
                        try {
                            wait(100); // Espera por 100ms e verifica running novamente
                        } catch (InterruptedException e) {
                            running = false;
                            return;
                        }
                    }
                    if (!running) break;
                }
                // Executa a tarefa
                //System.out.println("Trabalhadora " + id + ": Iniciando processamento");
                Operacao currentTask = this.tarefaAtual;
                processarOperacao(currentTask);

                // Volta a ficar livre
                synchronized (this) {
                    livre = true;
                }
            }
        } finally {
            System.out.println("Trabalhadora " + id + " encerrada.");
        }
    }

    public void processarOperacao(Operacao operacao) {
        synchronized (contas) {
            switch (operacao.getTipo()) {
                case EXIBIR_BALANCO:
                    System.out.println("=== Balanço de todas as contas ===");
                    for (Conta conta : contas) {
                        System.out.println(conta);
                    }
                    System.out.println("==================================");
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

            try {
                //System.out.println("Trabalhadora " + id + ": Iniciando sleep");
                Thread.sleep(this.intervaloOperacao);
                //System.out.println("Trabalhadora " + id + ": Finalizou sleep");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}