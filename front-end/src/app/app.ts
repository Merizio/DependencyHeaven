import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from './components/sidebar/sidebar';
import { Canvas } from './components/canvas/canvas';
import { Login } from './components/login/login';
import { AuthService, LoginResponse } from './services/auth.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, Sidebar, Canvas, Login],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('dependency_heaven');
  selectedTemplateId: number | null = null;
  usuario: LoginResponse | null = null;

  private authService = inject(AuthService);

  ngOnInit(): void {
    this.usuario = this.authService.getUsuario();
  }

  onTemplateSelecionado(id: number) {
    this.selectedTemplateId = id;
  }

  onLogado(usuario: LoginResponse) {
    this.usuario = usuario;
  }

  logout() {
    this.authService.logout();
    this.usuario = null;
    this.selectedTemplateId = null;
  }
}
