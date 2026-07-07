import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, effect, inject, signal } from '@angular/core';
import { Task } from '../modules/task/task';
import { Dependencies } from "../modules/dependencies/dependencies";
import { TemplateService } from '../../template';

@Component({
  selector: 'app-canvas',
  imports: [CommonModule, Task, Dependencies  ],
  templateUrl: './canvas.html',
  styleUrl: './canvas.css',
})
export class Canvas{
  newid = 7;
  isTaskModalOpen = false;
  isTelaDependencias = false;
  tarefa_click: any = null;
  mensagemErroLoop= signal<string | null>(null);


  // Simulando as tarefas conectadas conforme a sua imagem
  tarefas:any[] = [];
  nomeTemplate: string = '';

  // Matriz onde cada índice é uma coluna (nível de execução)
  colunas: any[][] = [];
  templateService = inject(TemplateService);
  private cdr = inject(ChangeDetectorRef);  

  constructor() {
    effect(() => {
      const templateSelecionado = this.templateService.templateAtivo();

      if (templateSelecionado) {
        console.log("1. Canvas recebeu o template:", templateSelecionado.nome);
        this.nomeTemplate = templateSelecionado.nome;
        
        // 2. CLONE PROFUNDO: Corta a ligação com a Sidebar para você poder 
        // mexer nas dependências no Canvas sem quebrar os dados originais.
        this.tarefas = JSON.parse(JSON.stringify(templateSelecionado.tarefas || []));
        
        // 3. Roda o algoritmo dos grafos
        this.organizarPorDependencia();

        console.log("2. Matriz gerada para a tela:", this.colunas);

        // 4. O TIRO DE MISERICÓRDIA: Grita para o HTML se atualizar!
        this.cdr.detectChanges();

      } else {
        this.tarefas = [];
        this.colunas = [];
        this.cdr.detectChanges();
      }
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
    const maxNivel = Math.max(...Array.from(niveis.values()));
    
    // Distribui as tarefas dentro da matriz de colunas
    this.colunas = [];
    for (let i = 0; i <= maxNivel; i++) {
      this.colunas.push(this.tarefas.filter(t => niveis.get(t.id) === i));
    }
  }

  abrirModalNovaTarefa(tarefa_: any): void {
    this.tarefa_click = tarefa_;
    this.isTaskModalOpen = !this.isTaskModalOpen;
    console.log('Abrindo modal para tarefa...');
  }
  fecharModalTarefa(): void{
    this.isTaskModalOpen = !this.isTaskModalOpen;
    this.tarefa_click = null;
    console.log('Fechando modal ...');
  }
  receberTarefaMod(modified: any){
    console.log("1. Pacote recebido do Modal:", modified);

    const index = this.tarefas.findIndex(t=>t.id === modified.id);

    console.log("2. Id da Tarefa a modifical:", modified.id);

    if(index !== -1){
      this.tarefas[index] = modified
      console.log("4. passei por aqui");
    }
    else{
      const newtask = {id: this.newid, titulo: modified.titulo, status: 'EM_ANDAMENTO', descricao: modified.descricao, dependenciasIds: [] };
      this.newid +=1;
      this.tarefas.push(newtask);
    }

    this.organizarPorDependencia();
    console.log("3. tarefa depois da att:", this.tarefas[index]);
    this.isTaskModalOpen=!this.isTaskModalOpen;
    this.tarefa_click = null;
  }

  abrirTelaDependencias(){
    this.isTelaDependencias=!this.isTelaDependencias;
    console.log('aviso lista recebido ...');
  }

  fecharListaDependencias(){
    this.isTelaDependencias = !this.isTelaDependencias;
    //this.tarefa_click = null;
    console.log('Fechando lista ...');
  }

  adicionarDependencia(modified: any){
    try{
      console.log("1. Pacote recebido de add dependencias:", modified);

      const index = this.tarefas.findIndex(t=>t.id === modified.id);

      console.log("2. Id da Tarefa a adicionar:", modified.id);

      if(index !== -1){
        this.tarefas[index] = modified
        console.log("4. passei por aqui");
      }

      this.organizarPorDependencia();
      console.log("3. tarefa depois da att:", this.tarefas[index]);
      //this.isTaskModalOpen=!this.isTaskModalOpen;
      this.tarefa_click = {...modified};
    }
    catch{
      console.log("Loop de dependências ...");
      this.mostrarAviso("Essa tarefa depende da Atual!");

    }
  }

  removerDependencia(modified: any){
    console.log("1. Pacote recebido de remove dependencias:", modified);

    const index = this.tarefas.findIndex(t=>t.id === modified.id);

    console.log("2. Id da Tarefa a adicionar:", modified.id);

    if(index !== -1){
      this.tarefas[index] = modified
      console.log("4. passei por aqui");
    }

    this.organizarPorDependencia();
    console.log("3. tarefa depois da att:", this.tarefas[index]);
    //this.isTaskModalOpen=!this.isTaskModalOpen;
    this.tarefa_click = {...modified};
  }

  mostrarAviso(mensagem: string){
    this.mensagemErroLoop.set(mensagem);

    setTimeout(() => {
      this.mensagemErroLoop.set(null);
    }, 2000);
  }
}
