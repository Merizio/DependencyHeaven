import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-task',
  imports: [],
  templateUrl: './task.html',
  styleUrl: './task.css',
})
export class Task {
  tarefa = input<any>(null);
  avisoFechar = output<void>();
  avisoLista = output<void>();

  taskMod = output<any>();
  salvarAlteracoes(novoTitulo: string, novaDescricao: string){
    const atualizado = {
      ...this.tarefa(),
      titulo: novoTitulo,
      descricao: novaDescricao
    };

    this.taskMod.emit(atualizado);
  }

  finalizarTarefa(){
    const atualizado = {
      ...this.tarefa(),
      status: 'FINALIZADO'
    }

    this.taskMod.emit(atualizado);
  }

  closeTask(){
    this.avisoFechar.emit();
  }

  acionarDependencias(){
    this.avisoLista.emit();
    console.log('Chamando lista ...');
  }
}
