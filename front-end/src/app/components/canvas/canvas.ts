import { CommonModule } from '@angular/common';
import { Component, OnInit, OnChanges, SimpleChanges, Input, inject, ChangeDetectorRef } from '@angular/core';
import { TemplateService, Tarefa } from '../../services/template.service';
import { TarefaService } from '../../services/tarefa.service';
import { Task } from '../modules/task/task';
import { Dependencies } from '../modules/dependencies/dependencies';

@Component({
  selector: 'app-canvas',
  imports: [CommonModule, Task, Dependencies],
  templateUrl: './canvas.html',
  styleUrl: './canvas.css',
})
export class Canvas implements OnInit, OnChanges {
  @Input() templateId!: number;
  templateNome: string = '';
  tarefas: Tarefa[] = [];
  isTaskModalOpen = false;
  isTelaDependencias = false;
  tarefa_click: Tarefa | null = null;
  colunas: any[][] = [];

  private templateService = inject(TemplateService);
  private tarefaService = inject(TarefaService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    if (this.templateId) {
      this.carregarTarefas();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['templateId'] && this.templateId) {
      this.carregarTarefas();
    }
  }

  carregarTarefas() {
    this.templateService.buscarTemplate(this.templateId).subscribe({
      next: (data) => {
        this.templateNome = data.nome;
        this.tarefas = data.tarefas;
        this.organizarPorDependencia();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao buscar tarefas do template', err)
    });
  }

  organizarPorDependencia() {
    const niveis = new Map<number, number>();

    const calcularNivel = (tarefa: Tarefa): number => {
      if (niveis.has(tarefa.id)) return niveis.get(tarefa.id)!;

      if (!tarefa.dependenciasIds || tarefa.dependenciasIds.length === 0) {
        niveis.set(tarefa.id, 0);
        return 0;
      }

      let nivelMaximo = -1;
      for (const depId of tarefa.dependenciasIds) {
        const depTarefa = this.tarefas.find(t => t.id === depId);
        if (depTarefa) {
          const nivelDep = calcularNivel(depTarefa);
          if (nivelDep > nivelMaximo) nivelMaximo = nivelDep;
        }
      }

      const meuNivel = nivelMaximo + 1;
      niveis.set(tarefa.id, meuNivel);
      return meuNivel;
    };

    this.tarefas.forEach(t => calcularNivel(t));

    let maxNivel = -1;
    if (niveis.size > 0) {
      maxNivel = Math.max(...Array.from(niveis.values()));
    }

    const novasColunas = [];
    for (let i = 0; i <= maxNivel; i++) {
      novasColunas.push(this.tarefas.filter(t => niveis.get(t.id) === i));
    }
    this.colunas = novasColunas;
  }

  abrirModalNovaTarefa(tarefa_: Tarefa | null = null): void {
    this.tarefa_click = tarefa_;
    this.isTaskModalOpen = !this.isTaskModalOpen;
  }

  fecharModalTarefa(): void {
    this.isTaskModalOpen = !this.isTaskModalOpen;
    this.tarefa_click = null;
  }

  receberTarefaMod(modified: Tarefa): void {
    const index = this.tarefas.findIndex(t => t.id === modified.id);

    if (index !== -1) {
      this.tarefas[index] = modified;
      this.organizarPorDependencia();
      this.cdr.detectChanges();
    } else if (modified.titulo?.trim()) {
      const descricao = modified.descricao || '';
      this.tarefaService.criarTarefa(this.templateId, modified.titulo, descricao, [{ nome: 'João' }]).subscribe({
        next: (novaTarefa) => {
          this.tarefas = [...this.tarefas, novaTarefa];
          this.organizarPorDependencia();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao criar tarefa', err)
      });
    }

    this.isTaskModalOpen = false;
    this.tarefa_click = null;
  }

  abrirTelaDependencias(): void {
    this.isTelaDependencias = !this.isTelaDependencias;
  }

  fecharListaDependencias(): void {
    this.isTelaDependencias = !this.isTelaDependencias;
  }

  adicionarDependenciaModal(modified: Tarefa): void {
    const index = this.tarefas.findIndex(t => t.id === modified.id);
    if (index !== -1) {
      this.tarefas[index] = modified;
      this.organizarPorDependencia();
      this.cdr.detectChanges();
      this.tarefa_click = { ...modified };
    }
  }

  removerDependenciaModal(modified: Tarefa): void {
    const index = this.tarefas.findIndex(t => t.id === modified.id);
    if (index !== -1) {
      this.tarefas[index] = modified;
      this.organizarPorDependencia();
      this.cdr.detectChanges();
      this.tarefa_click = { ...modified };
    }
  }

  avancarEstado(tarefa: Tarefa) {
    let novoEstado = '';
    if (tarefa.estado === 'PENDENTE') novoEstado = 'EM_ANDAMENTO';
    else if (tarefa.estado === 'EM_ANDAMENTO') novoEstado = 'FINALIZADO';
    else if (tarefa.estado === 'FINALIZADO') novoEstado = 'PENDENTE';

    if (novoEstado) {
      this.tarefaService.alterarEstado(tarefa.id, novoEstado).subscribe({
        next: () => this.carregarTarefas(),
        error: (err) => alert('Erro ao alterar estado: ' + (err.error?.erro || err.message))
      });
    }
  }

  adicionarDependencia(tarefa: Tarefa) {
    const inputId = prompt(`A tarefa "${tarefa.titulo}" dependerá de qual Tarefa (Índice)?`);
    if (inputId && !isNaN(Number(inputId))) {
      const indice = Number(inputId);
      const alvo = this.tarefas.find(t => t.indiceLocal === indice);

      if (!alvo) {
        alert(`Nenhuma tarefa com índice #${indice} encontrada neste template.`);
        return;
      }

      this.tarefaService.adicionarDependencia(tarefa.id, alvo.id).subscribe({
        next: () => this.carregarTarefas(),
        error: (err) => alert('Erro ao adicionar dependência: ' + (err.error?.erro || err.message))
      });
    }
  }

  getNomeTarefa(id: number): string {
    const tarefa = this.tarefas.find(t => t.id === id);
    return tarefa ? `#${tarefa.indiceLocal} - ${tarefa.titulo}` : `#${id}`;
  }

  removerDependencia(tarefa: Tarefa, dependenciaId: number) {
    if (confirm('Deseja realmente remover esta dependência?')) {
      this.tarefaService.removerDependencia(tarefa.id, dependenciaId).subscribe({
        next: () => this.carregarTarefas(),
        error: (err) => alert('Erro ao remover dependência: ' + (err.error?.erro || err.message))
      });
    }
  }

  deleteTarefa(tarefa: Tarefa) {
    if (confirm(`Deseja realmente excluir a tarefa "${tarefa.titulo}" e todas as suas ligações? Essa ação não pode ser desfeita.`)) {
      this.tarefaService.deleteTarefa(tarefa.id).subscribe({
        next: () => this.carregarTarefas(),
        error: (err) => alert('Erro ao excluir tarefa: ' + (err.error?.erro || err.message))
      });
    }
  }
}
