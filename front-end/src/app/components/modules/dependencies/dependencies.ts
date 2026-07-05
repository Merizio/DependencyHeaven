import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-dependencies',
  imports: [],
  templateUrl: './dependencies.html',
  styleUrl: './dependencies.css',
})

export class Dependencies {
  tarefa = input<any>(null);
  avisoFecharLista = output<void>();
  lista = input<any>(null);

  listaAdd = computed(() => {
    const t = this.tarefa();
    const l = this.lista();
    if (!t || !l) return []; 
    const dependenciasAtuais = t.dependenciasIds || [];

    console.log("add funcionou...");

    return l.filter((task: any) => task.id !== t.id && !dependenciasAtuais.includes(task.id));
  });

  listaRem = computed(() => {
    const t = this.tarefa();
    const l = this.lista();
    if (!t || !l) return [];
    const dependenciasAtuais = t.dependenciasIds || [];
    return l.filter((task: any) => task.id !== t.id && dependenciasAtuais.includes(task.id));
  });

  closeTask(){
    this.avisoFecharLista.emit();
  }

  taskAdd = output<any>();
  taskRem = output<any>();

  adicionarDependencia(task: any){
    const dependenciasAntigas = this.tarefa().dependenciasIds || [];

    const atualizado = {
      ...this.tarefa(),
      dependenciasIds: [...dependenciasAntigas, task.id]
    }

    this.taskAdd.emit(atualizado);
  }

  removerDependencia(task: any){
    const dependenciasNovas = this.tarefa().dependenciasIds.filter((i:any)=> i !== task.id) || [];

    const atualizado = {
      ...this.tarefa(),
      dependenciasIds: dependenciasNovas
    }

    this.taskRem.emit(atualizado);
  }

}
