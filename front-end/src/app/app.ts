import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from "./components/sidebar/sidebar";
import { Canvas } from "./components/canvas/canvas";

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, Sidebar, Canvas],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('dependency_heaven');
  selectedTemplateId: number | null = null;
  
  onTemplateSelecionado(id: number) {
    this.selectedTemplateId = id;
  }
}
