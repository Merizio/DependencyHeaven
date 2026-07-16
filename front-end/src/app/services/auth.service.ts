import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  id: number;
  nome: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/auth';
  private storageKey = 'dependency-heaven-user';

  login(email: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, senha }).pipe(
      tap((usuario) => localStorage.setItem(this.storageKey, JSON.stringify(usuario)))
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }

  getUsuario(): LoginResponse | null {
    const raw = localStorage.getItem(this.storageKey);
    return raw ? JSON.parse(raw) as LoginResponse : null;
  }

  isLogado(): boolean {
    return this.getUsuario() !== null;
  }
}
