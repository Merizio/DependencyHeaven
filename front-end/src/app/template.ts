import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {
  
  templateAtivo = signal<any>(null);

  selecionarTemplate(template: any) {
    this.templateAtivo.set(template);
  }
}