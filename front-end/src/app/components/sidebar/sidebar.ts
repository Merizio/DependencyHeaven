import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { TemplateService } from '../../template';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  // Variáveis que o HTML vai ler
  nomeUsuario: string = 'Mrzio';

  public templateService = inject(TemplateService);

  //mock de tarefas para testes
  tarefas_template_1 = [
    { id: 1, titulo: 'Task 1', status: 'FINALIZADO', descricao: "para fazer, precisa disso, que apesar disso, consigo resolver aquilo", dependenciasIds: [] },
    { id: 2, titulo: 'Task 2', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [1] },
    { id: 3, titulo: 'Task 3', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [1] },
    { id: 4, titulo: 'Task 5', status: 'BLOQUEADO', descricao: "para fazer, precisa disso", dependenciasIds: [2, 3] },
    { id: 5, titulo: 'Task 6', status: 'BLOQUEADO', descricao: "para fazer, precisa disso", dependenciasIds: [4] },
    { id: 6, titulo: 'Task 7', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [] }
  ];
  tarefas_template_2 = [
    { id: 1, titulo: 'Começar', status: 'FINALIZADO', descricao: "para fazer, precisa disso, que apesar disso, consigo resolver aquilo", dependenciasIds: [] },
    { id: 2, titulo: 'Meio do Caminho', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [1] },
    { id: 3, titulo: 'Uma depois dessa', status: 'BLOQUEADO', descricao: "para fazer, precisa disso", dependenciasIds: [1] },
    { id: 4, titulo: 'Finalizar', status: 'EM_ANDAMENTO', descricao: "para fazer, precisa disso", dependenciasIds: [2, 3] }
  ];
  
  
  // No futuro, isso virá do banco de dados (Spring Boot)
  templates = [
    { id: 1, nome: 'Template 1', tarefas: this.tarefas_template_1},
    { id: 2, nome: 'Template 2', tarefas: [] },
    { id: 3, nome: 'Exemplo', tarefas: this.tarefas_template_2 }
  ];

  aoClicarTemplate(template: any){
    this.templateService.selecionarTemplate(template);
  }
}
