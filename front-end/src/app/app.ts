import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from "./components/sidebar/sidebar";
import { Canvas } from "./components/canvas/canvas";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Sidebar, Canvas],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('dependency_heaven');
}
