import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa } from './template.service';

@Injectable({
  providedIn: 'root'
})
export class TarefaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  criarTarefa(templateId: number, titulo: string, descricao?: string, membros: { nome: string }[] = []): Observable<Tarefa> {
    return this.http.post<Tarefa>(`${this.apiUrl}/templates/${templateId}/tarefas`, { titulo, descricao, membros });
  }

  atualizarTarefa(id: number, titulo: string, descricao?: string, membros: { nome: string }[] = []): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.apiUrl}/tarefas/${id}`, { titulo, descricao, membros });
  }

  alterarEstado(id: number, estado: string): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.apiUrl}/tarefas/${id}/estado`, { estado });
  }

  adicionarDependencia(id: number, dependenciaId: number): Observable<Tarefa> {
    return this.http.post<Tarefa>(`${this.apiUrl}/tarefas/${id}/dependencias`, { dependenciaId });
  }

  removerDependencia(tarefaId: number, dependenciaId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/tarefas/${tarefaId}/dependencias/${dependenciaId}`);
  }

  deleteTarefa(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/tarefas/${id}`);
  }
}
