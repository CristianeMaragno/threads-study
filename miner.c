#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define NUM_THREADS 2

typedef struct data_t{
	char *data;
	int num_zeros;
    int thread_id;
} datathread;

// djb2 hash (referencia: http://www.cse.yorku.ca/~oz/hash.html)
unsigned long hash_djb2(unsigned char *str) {
    unsigned long hash = 5381;
    int c;
    while (c = *str++)
        hash = ((hash << 5) + hash) + c; // hash * 33 + c
    return hash;
}

// Converte a hash para string com representacao hexadecimal (para legibilide)
void htos(unsigned long hash, char output[65]) {
    sprintf(output, "%lx", hash);
}

// Verifica se a hash termina com num_zeros numeros zeros
int check_hash_zeros(char *hash_str, int num_zeros) {
    int len = strlen(hash_str);
    for (int i = len - num_zeros; i < len; i++) {
        if (hash_str[i] != '0') {
            return 0;
        }
    }
    return 1;
}

void* PoW(void *arg) {
    datathread *d;
    d = (datathread *)arg;

    unsigned long hash;
    char hash_str[65];
    long long nonce = d->thread_id;
    char buffer[256];
    
    // Minerador alterar o valor de nonce ate encontrar a hash que satisfaca o desafio
    while (1) {
        // Concatena o dado do bloco com o nonce
        sprintf(buffer, "%s%lld", d->data, nonce);
        
        // computa a hash do string concatenado (bloco + nonce)
        hash = hash_djb2((unsigned char *)buffer);
        
        // Para visualizar a hash em formato hexadecimal
        htos(hash, hash_str);
        
        if (check_hash_zeros(hash_str, d->num_zeros)) {
            printf("Sucesso! Valor do nonce: %lld\n", nonce);
            printf("Hash: %s\n", hash_str);
            break;
        }
        nonce = nonce + NUM_THREADS;
    }
}

int main(int argc, char **argv) {
    char data[] = "Exemplo de dados do bloco";  // Representa o conteudo do bloco
    int num_zeros; // Numero de zeros requeridos no final do hash

    datathread *d;
    d = malloc(sizeof(datathread));
    d->data = data;
    d->num_zeros = num_zeros;

    if(argc!=2){
	    printf("Digite %s num_zeros\n", argv[0]);
	    exit(0);
    }
    num_zeros = atoi(argv[1]);

    pthread_t threads[NUM_THREADS];
    int thread_ids[NUM_THREADS]; 
    int quantidade_threads = NUM_THREADS;
    for (int i = 0; i < quantidade_threads; i++) {
        thread_ids[i] = i; // Atribui um ID para cada thread
        d->thread_id = &thread_ids[i]; //problema!
        if (pthread_create(&threads[i], NULL, PoW, (void *)d)) {
        fprintf(stderr, "Erro ao criar thread %d\n", i + 1);
        return 1;
        }
    }
    // Aguardar a finalização de todas as threads
    for (int i = 0; i < quantidade_threads; i++) {
        pthread_join(threads[i], NULL);
    }

    //pthreadcancel
    
    return 0;
}
