package org.trabalho;

class Trabalhadora extends Thread {
    private boolean livre = true;
    private Runnable tarefaAtual;

    public synchronized boolean estaLivre() {
        return livre;
    }

    public synchronized void executar(Runnable tarefa) {
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
            if (tarefaAtual != null) {
                tarefaAtual.run();
                tarefaAtual = null;
            }
            // Volta a ficar livre
            synchronized (this) {
                livre = true;
            }
        }
    }
}