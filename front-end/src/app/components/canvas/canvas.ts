import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-canvas',
  imports: [CommonModule],
  templateUrl: './canvas.html',
  styleUrl: './canvas.css',
})
export class Canvas implements OnInit {
  // Simulando as tarefas conectadas conforme a sua imagem
  tarefas = [
    { id: 1, titulo: 'Task 1', status: 'FINALIZADO', descricao: "para fazer, precisa disso, que apesar disso, consigo resolver aquilo", dependenciasIds: [] },
    { id: 2, titulo: 'Task 2', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [1] },
    { id: 3, titulo: 'Task 3', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [1] },
    { id: 4, titulo: 'Task 5', status: 'BLOQUEADO', descricao: "para fazer, precisa disso", dependenciasIds: [2, 3] },
    { id: 5, titulo: 'Task 6', status: 'BLOQUEADO', descricao: "para fazer, precisa disso", dependenciasIds: [4] },
    { id: 6, titulo: 'Task 7', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [] }
  ];

  // Matriz onde cada índice é uma coluna (nível de execução)
  colunas: any[][] = [];

  ngOnInit() {
    this.organizarPorDependencia();
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
    const maxNivel = Math.max(...Array.from(niveis.values()));
    
    // Distribui as tarefas dentro da matriz de colunas
    this.colunas = [];
    for (let i = 0; i <= maxNivel; i++) {
      this.colunas.push(this.tarefas.filter(t => niveis.get(t.id) === i));
    }
  }

  abrirModalNovaTarefa(): void {
    console.log('Abrindo modal para criar nova tarefa...');
  }
}
