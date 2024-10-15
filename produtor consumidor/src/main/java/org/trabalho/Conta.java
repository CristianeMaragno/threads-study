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
        this.saldo += valor;
    }

    public synchronized void transferir(Conta destino, int valor) {
        this.saldo -= valor;
        destino.depositar(valor);
    }

    @Override
    public String toString() {
        return "Conta " + id + " - Saldo: " + saldo;
    }
}
