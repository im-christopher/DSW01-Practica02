import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Empleado } from '../models';

export interface EmpleadoFormPayload {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  version: number;
}

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './empleado-form.component.html',
  styleUrl: './empleado-form.component.css'
})
export class EmpleadoFormComponent implements OnChanges {
  @Input() empleado: Empleado | null = null;
  @Input() submitting = false;

  @Output() save = new EventEmitter<EmpleadoFormPayload>();
  @Output() clear = new EventEmitter<void>();

  readonly form = this.fb.nonNullable.group({
    clave: [''],
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.minLength(10)]],
    version: [0]
  });

  constructor(private readonly fb: FormBuilder) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['empleado']) {
      if (!this.empleado) {
        this.reset();
        return;
      }
      this.form.setValue({
        clave: this.empleado.clave,
        nombre: this.empleado.nombre,
        direccion: this.empleado.direccion,
        telefono: this.empleado.telefono,
        version: this.empleado.version
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.save.emit(this.form.getRawValue());
  }

  reset(): void {
    this.form.reset({
      clave: '',
      nombre: '',
      direccion: '',
      telefono: '',
      version: 0
    });
    this.clear.emit();
  }
}
