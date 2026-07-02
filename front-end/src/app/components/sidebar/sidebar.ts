import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  // Variáveis que o HTML vai ler
  nomeUsuario: string = 'Usuário';
  
  // No futuro, isso virá do banco de dados (Spring Boot)
  templates = [
    { id: 1, nome: 'Template 1', ativo: true },
    { id: 2, nome: 'Template 2', ativo: false },
    { id: 3, nome: 'Exemplo', ativo: false }
  ];
}
