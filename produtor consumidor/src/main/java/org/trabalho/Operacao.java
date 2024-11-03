package org.trabalho;

public class Operacao {
    public enum Tipo {
        EXIBIR_BALANCO,
        DEPOSITO,
        TRANSFERENCIA
    }

    private Tipo tipo;
    private int contaOrigemId;
    private int contaDestinoId;
    private int valor;

    public Operacao(Tipo tipo, int contaOrigemId, int contaDestinoId, int valor) {
        this.tipo = tipo;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.valor = valor;
    }

    public Operacao(Tipo tipo){
        this.tipo = tipo;
    }

    public Tipo getTipo() { return tipo; }

    public int getContaOrigemId() { return contaOrigemId; }

    public int getContaDestinoId() { return contaDestinoId; }

    public int getValor() { return valor; }
}