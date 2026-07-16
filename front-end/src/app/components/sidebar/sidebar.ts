import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, Output, EventEmitter, Input } from '@angular/core';
import { TemplateService, Template } from '../../services/template.service';
import { LoginResponse } from '../../services/auth.service';

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
  @Input() usuario: LoginResponse | null = null;
  @Output() templateSelecionado = new EventEmitter<number>();
  @Output() logoutSolicitado = new EventEmitter<void>();

  templates: TemplateItem[] = [];
  carregando = false;

  private templateService = inject(TemplateService);

  ngOnInit() {
    this.carregarTemplates();
  }

  carregarTemplates() {
    this.carregando = true;
    this.templateService.listarTemplates().subscribe({
      next: (data) => {
        this.templates = data.map(t => ({ ...t, ativo: false }));
        this.carregando = false;
        if (this.templates.length > 0) {
          this.selecionarTemplate(this.templates[0]);
        }
      },
      error: (err) => {
        this.carregando = false;
        console.error('Erro ao buscar templates', err);
      }
    });
  }

  selecionarTemplate(template: TemplateItem) {
    this.templates.forEach(t => t.ativo = false);
    template.ativo = true;
    this.templateSelecionado.emit(template.id);
  }

  criarTemplate() {
    const nome = prompt('Digite o nome do novo template:');
    const usuarioId = this.usuario?.id ?? 1;

    if (nome && nome.trim().length > 0) {
      this.templateService.criarTemplate(nome.trim(), usuarioId).subscribe({
        next: (novoTemplate) => {
          const t: TemplateItem = { ...novoTemplate, ativo: false };
          this.templates.push(t);
          this.selecionarTemplate(t);
        },
        error: (err) => console.error('Erro ao criar template', err)
      });
    }
  }

  deleteTemplate(template: TemplateItem, event: Event) {
    event.stopPropagation();
    if (confirm(`Deseja realmente excluir o projeto "${template.nome}"? Todas as tarefas serão perdidas permanentemente.`)) {
      this.templateService.deleteTemplate(template.id).subscribe({
        next: () => this.carregarTemplates(),
        error: (err) => alert('Erro ao excluir template: ' + (err.error?.erro || err.message))
      });
    }
  }

  sair(): void {
    this.logoutSolicitado.emit();
  }
}
