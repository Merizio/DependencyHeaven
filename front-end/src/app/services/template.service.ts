import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Template {
  id: number;
  nome: string;
}

export interface Tarefa {
  id: number;
  indiceLocal: number;
  titulo: string;
  descricao?: string;
  estado: string;
  membros: { nome: string }[];
  dependenciasIds: number[];
}

export interface TemplateDetalhado {
  id: number;
  nome: string;
  tarefas: Tarefa[];
}

@Injectable({
  providedIn: 'root'
})
export class TemplateService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/templates';

  listarTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(this.apiUrl);
  }

  criarTemplate(nome: string, usuarioId: number): Observable<Template> {
    return this.http.post<Template>(this.apiUrl, { nome, usuarioId });
  }

  buscarTemplate(id: number): Observable<TemplateDetalhado> {
    return this.http.get<TemplateDetalhado>(`${this.apiUrl}/${id}`);
  }
}
