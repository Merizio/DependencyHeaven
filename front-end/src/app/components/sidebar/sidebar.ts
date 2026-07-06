import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, Output, EventEmitter } from '@angular/core';
import { TemplateService, Template } from '../../services/template.service';

export interface TemplateItem extends Template {
  ativo?: boolean;
}

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  nomeUsuario: string = 'Usuário';
  templates: TemplateItem[] = [];
  @Output() templateSelecionado = new EventEmitter<number>();
  
  private templateService = inject(TemplateService);

  ngOnInit() {
    this.carregarTemplates();
  }

  carregarTemplates() {
    this.templateService.listarTemplates().subscribe({
      next: (data) => {
        this.templates = data.map(t => ({ ...t, ativo: false }));
        if (this.templates.length > 0) {
          this.selecionarTemplate(this.templates[0]);
        }
      },
      error: (err) => console.error('Erro ao buscar templates', err)
    });
  }

  selecionarTemplate(template: TemplateItem) {
    this.templates.forEach(t => t.ativo = false);
    template.ativo = true;
    this.templateSelecionado.emit(template.id);
  }

  criarTemplate() {
    const nome = prompt('Digite o nome do novo template:');
    if (nome && nome.trim().length > 0) {
      // Usamos usuarioId = 1 para simular o usuário logado
      this.templateService.criarTemplate(nome, 1).subscribe({
        next: (novoTemplate) => {
          const t: TemplateItem = { ...novoTemplate, ativo: false };
          this.templates.push(t);
          this.selecionarTemplate(t);
        },
        error: (err) => console.error('Erro ao criar template', err)
      });
    }
  }
}
