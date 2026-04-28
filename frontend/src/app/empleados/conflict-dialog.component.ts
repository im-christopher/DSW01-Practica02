import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Empleado } from '../models';

@Component({
  selector: 'app-conflict-dialog',
  standalone: true,
  imports: [CommonModule],
  styles: [
    `
      .dialog-backdrop {
        position: fixed;
        inset: 0;
        background: rgba(21, 32, 36, 0.5);
        display: grid;
        place-items: center;
        padding: 1rem;
        z-index: 120;
      }

      .dialog-card {
        width: min(480px, 100%);
        background: #ffffff;
        border: 1px solid #d3dfdc;
        border-radius: 0.9rem;
        padding: 1.1rem;
        box-shadow: 0 14px 30px rgba(16, 31, 36, 0.22);
      }

      h2 {
        margin: 0 0 0.7rem;
      }

      p {
        margin: 0 0 0.55rem;
      }

      .action-group {
        margin-top: 0.85rem;
        display: flex;
        gap: 0.6rem;
        flex-wrap: wrap;
      }

      .btn {
        min-height: 2.65rem;
        border: 0;
        border-radius: 0.7rem;
        padding: 0.45rem 0.95rem;
        cursor: pointer;
        font-weight: 700;
      }

      .btn-primary {
        background: linear-gradient(135deg, #127662 0%, #0f8a70 100%);
        color: #fff;
      }

      .btn-secondary {
        background: #e8f1ee;
        color: #24444f;
      }
    `
  ],
  template: `
    <div class="dialog-backdrop" *ngIf="visible" role="dialog" aria-modal="true" aria-labelledby="conflict-title">
      <section class="dialog-card">
        <h2 id="conflict-title">Conflicto de version detectado</h2>
        <p>El registro fue modificado por otro proceso.</p>
        <p *ngIf="current">
          Version actual: {{ current.version }} · Nombre: {{ current.nombre }}
        </p>
        <div class="action-group">
          <button type="button" class="btn btn-primary" (click)="reloadLatest.emit()">Usar datos actuales</button>
          <button type="button" class="btn btn-secondary" (click)="dismiss.emit()">Cerrar</button>
        </div>
      </section>
    </div>
  `
})
export class ConflictDialogComponent {
  @Input() visible = false;
  @Input() current: Empleado | null = null;

  @Output() reloadLatest = new EventEmitter<void>();
  @Output() dismiss = new EventEmitter<void>();
}
