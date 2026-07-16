import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginResponse } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  @Output() logado = new EventEmitter<LoginResponse>();

  email = 'teste@example.com';
  senha = '123456';
  carregando = false;
  erro = '';

  private authService = inject(AuthService);

  entrar(): void {
    this.erro = '';

    if (!this.email.trim() || !this.senha.trim()) {
      this.erro = 'Informe e-mail e senha.';
      return;
    }

    this.carregando = true;
    this.authService.login(this.email, this.senha).subscribe({
      next: (usuario) => {
        this.carregando = false;
        this.logado.emit(usuario);
      },
      error: () => {
        this.carregando = false;
        this.erro = 'E-mail ou senha inválidos.';
      }
    });
  }
}
