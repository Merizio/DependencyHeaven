import { CommonModule } from '@angular/common';
import { Component, OnInit, OnChanges, SimpleChanges, Input, inject, ChangeDetectorRef } from '@angular/core';
import { TemplateService, Tarefa } from '../../services/template.service';
import { TarefaService } from '../../services/tarefa.service';

@Component({
  selector: 'app-canvas',
  imports: [CommonModule],
  templateUrl: './canvas.html',
  styleUrl: './canvas.css',
})
export class Canvas implements OnInit, OnChanges {
  @Input() templateId!: number;
  templateNome: string = '';
  tarefas: Tarefa[] = [];
  colunas: any[][] = [];

  private templateService = inject(TemplateService);
  private tarefaService = inject(TarefaService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    // Inicializa vazio ou carrega se templateId já existir
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
        this.cdr.detectChanges(); // Força a atualização imediata da tela
      },
      error: (err) => console.error('Erro ao buscar tarefas do template', err)
    });
  }

  organizarPorDependencia() {
    const niveis = new Map<number, number>();

    // Função interna para calcular a profundidade do nó usando busca em profundidade
    const calcularNivel = (tarefa: any): number => {
      // Se já calculou, retorna do cache
      if (niveis.has(tarefa.id)) return niveis.get(tarefa.id)!;
      
      // Se não tem dependências, é a raiz (Nível 0)
      if (!tarefa.dependenciasIds || tarefa.dependenciasIds.length === 0) {
        niveis.set(tarefa.id, 0);
        return 0;
      }

      // O nível da tarefa é (maior nível entre suas dependências) + 1
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

    // Aplica o cálculo para todas as tarefas
    this.tarefas.forEach(t => calcularNivel(t));

    // Descobre quantas colunas teremos no total
    let maxNivel = -1;
    if (niveis.size > 0) {
      maxNivel = Math.max(...Array.from(niveis.values()));
    }
    
    // Distribui as tarefas dentro da matriz de colunas
    const novasColunas = [];
    for (let i = 0; i <= maxNivel; i++) {
      novasColunas.push(this.tarefas.filter(t => niveis.get(t.id) === i));
    }
    this.colunas = novasColunas;
  }

  abrirModalNovaTarefa(): void {
    const titulo = prompt('Digite o título da nova tarefa:');
    if (titulo && titulo.trim().length > 0) {
      const descricao = prompt('Digite a descrição (opcional):') || '';
      // Enviamos um membro mock para garantir que a tarefa possa ir para EM_ANDAMENTO depois
      this.tarefaService.criarTarefa(this.templateId, titulo, descricao, [{nome: 'João'}]).subscribe({
        next: (novaTarefa) => {
          this.tarefas = [...this.tarefas, novaTarefa];
          this.organizarPorDependencia();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao criar tarefa', err)
      });
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
    const depId = prompt(`A tarefa "${tarefa.titulo}" dependerá de qual Tarefa ID?`);
    if (depId && !isNaN(Number(depId))) {
      this.tarefaService.adicionarDependencia(tarefa.id, Number(depId)).subscribe({
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
    if (confirm('Tem certeza que deseja remover esta dependência?')) {
      this.tarefaService.removerDependencia(tarefa.id, dependenciaId).subscribe({
        next: () => this.carregarTarefas(),
        error: (err) => alert('Erro ao remover dependência: ' + (err.error?.erro || err.message))
      });
    }
  }
}
