// Compilar com gcc -pthread barbearia.c -o barbearia

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>

sem_t cliente;
sem_t barbeiro;
sem_t cliente_pronto;
sem_t barbeiro_pronto;
pthread_mutex_t mutex;

int clientes = 0;
int cadeiras; // vai ser informado na inicializacao do programa

int desiste(int id){
  printf("cliente %d desistiu e foi embora!\n",id);
  fflush(stdout);
  pthread_exit(0);
}

void cortando_cabelo(int id){
  printf("cabelo do cliente %d sendo cortado\n",id);
  fflush(stdout);
  sleep(rand() % 6);
}

void cortar_cabelo(){
  sleep(rand() % 8);
  printf("barbeiro finalizou o corte de cabelo do cliente\n");
  fflush(stdout);
}


void *thread_cliente(void *arg){
  int t_id = *((int *)arg);

  sleep(rand() % 10);

  pthread_mutex_lock(&mutex);
  printf("cliente %d entrou na barbearia\n",t_id);
  fflush(stdout);
  if(clientes == cadeiras){
    pthread_mutex_unlock(&mutex);
    desiste(t_id);
  } 
  clientes++;
  pthread_mutex_unlock(&mutex);
  
  sem_post(&cliente);
  sem_wait(&barbeiro);
  cortando_cabelo(t_id);
  
  sem_post(&cliente_pronto);
  sem_wait(&barbeiro_pronto);

  pthread_mutex_lock(&mutex);
  clientes--;
  printf("cliente %d saiu da barbearia\n",t_id);  
  fflush(stdout);
  pthread_mutex_unlock(&mutex);

  pthread_exit(0);
}

void *thread_barbeiro(void *arg){
  printf("barbeiro chegou!\n");
  fflush(stdout);
  while(1){
    sem_wait(&cliente);
    sem_post(&barbeiro);
    cortar_cabelo();  
    sem_wait(&cliente_pronto);
    sem_post(&barbeiro_pronto);
  }
  pthread_exit(0);
}


int main (int argc, char **argv){

  if(argc != 3){
    printf("precisa informar o número de threads (1 barbeiro + n-1 clientes) e cadeiras\n%s num_threads cadeiras\n",argv[0]);
    return 1;
  }

  int n_threads = atoi(argv[1]);
  int ids[n_threads];
  cadeiras = atoi(argv[2]);
  int i = 0;
  pthread_t threads[n_threads];

  srand(time(NULL));

  sem_init(&cliente, 0, 0);
  sem_init(&barbeiro, 0, 0);
  sem_init(&cliente_pronto, 0, 0);
  sem_init(&barbeiro_pronto, 0, 0);
  pthread_mutex_init(&mutex,NULL);

  pthread_create(&threads[i],NULL,thread_barbeiro,NULL);  

  for(i = 1; i < n_threads; i++){
    ids[i] = i-1;
    pthread_create(&threads[i],NULL,thread_cliente,&ids[i]);
  }

  for(i = 1; i < n_threads; i++){
    pthread_join(threads[i],NULL);
  }

  printf("Nao ha mais clientes\n");
  pthread_cancel(threads[0]); // o barbeiro

  sem_destroy(&cliente);
  sem_destroy(&barbeiro);
  sem_destroy(&cliente_pronto);
  sem_destroy(&barbeiro_pronto);
  pthread_mutex_destroy(&mutex);

  return 0;
}