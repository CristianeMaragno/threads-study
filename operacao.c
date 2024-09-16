#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <pthread.h>
#include <unistd.h>

#define IMPRIME

#define MAX_NUMBERS 500000000
//#define MAX_NUMBERS 10
#define MAX_VALUE 1000

#define NUM_THREADS 20

float numbers[MAX_NUMBERS];
unsigned int i;

void* init_numbers(void* arg){
  int thread_id = *(int*)arg; // Identificador da thread
  int batch = MAX_NUMBERS / NUM_THREADS;
  int start = batch * thread_id;
  int end = batch * thread_id + (batch - 1);
  printf("Thread %d: Iniciando -> Processando %d até %d\n", thread_id, start, end);

  for(i = 0; i < batch; i++)
    numbers[i + (batch * thread_id)] = ((float)rand()/(float)RAND_MAX) * MAX_VALUE;
  return NULL;
}

void* calculate_numbers(void* arg){
  int thread_id = *(int*)arg; // Identificador da thread
  int batch = MAX_NUMBERS / NUM_THREADS;
  int start = batch * thread_id;
  int end = batch * thread_id + (batch - 1);
  printf("Thread %d: Iniciando -> Processando %d até %d\n", thread_id, start, end);

  for(i = 0; i < batch; i++){
    int identifier = i + (batch * thread_id);
    numbers[identifier] =  numbers[identifier]*0.2 + numbers[identifier]/0.3;   
  } 
  return NULL;
}

int show_numbers(){
  for (i = 0; i < MAX_NUMBERS; i++)
    printf("number[%u] = %f\n",i,numbers[i]);
  return 0;
}

int main (int argc, char **argv){
  struct timeval t1, t2; 
  pthread_t threads[NUM_THREADS];
  int thread_ids[NUM_THREADS]; 
  int quantidade_threads = NUM_THREADS;

  srand(time(NULL));
  gettimeofday(&t1, NULL);

  // Criação das threads
  for (int i = 0; i < quantidade_threads; i++) {
    thread_ids[i] = i; // Atribui um ID para cada thread
    if (pthread_create(&threads[i], NULL, init_numbers, &thread_ids[i])) {
      fprintf(stderr, "Erro ao criar thread %d\n", i + 1);
      return 1;
    }
  }

  // Aguardar a finalização de todas as threads
  for (int i = 0; i < quantidade_threads; i++) {
    pthread_join(threads[i], NULL);
  }

  gettimeofday(&t2, NULL);
  double t_populate = (t2.tv_sec - t1.tv_sec) + ((t2.tv_usec - t1.tv_usec)/1000000.0);
  printf("Tempo de popular vetor: %f\n", t_populate);
  
 
  /*#ifdef IMPRIME
    //show_numbers();
  #endif
  */

  printf("----------\n");

  gettimeofday(&t1, NULL);
 
   // Criação das threads
  for (int i = 0; i < quantidade_threads; i++) {
    thread_ids[i] = i; // Atribui um ID para cada thread
    if (pthread_create(&threads[i], NULL, calculate_numbers, &thread_ids[i])) {
      fprintf(stderr, "Erro ao criar thread %d\n", i + 1);
      return 1;
    }
  }

  // Aguardar a finalização de todas as threads
  for (int i = 0; i < quantidade_threads; i++) {
    pthread_join(threads[i], NULL);
  }

  gettimeofday(&t2, NULL);
  double t_total = (t2.tv_sec - t1.tv_sec) + ((t2.tv_usec - t1.tv_usec)/1000000.0);
  printf("Tempo de operações: %f\n", t_total);

  return 0;
}