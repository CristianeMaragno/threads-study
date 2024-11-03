package org.trabalho;

public class Conta {

    private int id;

    private float saldo;

    public Conta(int id, float saldo) {
        this.id = id;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public synchronized float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public synchronized void depositar(int valor) {
        float novoSaldo = this.saldo + valor;
        this.saldo = novoSaldo;
    }

    public synchronized void transferir(int valor) {
        float novoSaldo = this.saldo - valor;
        this.saldo = novoSaldo;
    }

    public synchronized void receberTransferencia(int valor) {
        float novoSaldo = this.saldo + valor;
        this.saldo = novoSaldo;
    }

    @Override
    public synchronized String toString() {
        return "Conta " + id + " - Saldo: " + saldo;
    }
}
