import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../core/auth.service';
import { A11yAnnouncerService } from '../shared/a11y-announcer.service';
import { ApiErrorResponse, Empleado, EmpleadoPageResponse } from '../models';
import { ConflictDialogComponent } from './conflict-dialog.component';
import { EmpleadoFormComponent, EmpleadoFormPayload } from './empleado-form.component';
import { EmpleadosApiService } from './empleados-api.service';

@Component({
  selector: 'app-empleados-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, EmpleadoFormComponent, ConflictDialogComponent],
  templateUrl: './empleados-page.component.html',
  styleUrl: './empleados-page.component.css'
})
export class EmpleadosPageComponent implements OnInit {
  empleados: Empleado[] = [];
  selectedEmpleado: Empleado | null = null;
  loading = false;
  submitting = false;
  deletingKey: string | null = null;

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  showConflict = false;
  conflictCurrent: Empleado | null = null;

  readonly filtersForm = this.fb.nonNullable.group({
    nombre: [''],
    clave: ['', [Validators.pattern(/^$|^E-\d{3}$/)]],
    sort: ['asc' as 'asc' | 'desc'],
    includeInactive: [false]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly empleadosApi: EmpleadosApiService,
    private readonly authService: AuthService,
    readonly announcer: A11yAnnouncerService
  ) {}

  ngOnInit(): void {
    this.cargarEmpleados(0);
  }

  cargarEmpleados(page = this.page): void {
    this.announcer.clear();
    this.loading = true;
    this.page = page;

    const { nombre, clave, sort, includeInactive } = this.filtersForm.getRawValue();

    this.empleadosApi
      .listar({ page: this.page, size: this.size, nombre, clave, sort, includeInactive })
      .subscribe({
        next: (res: EmpleadoPageResponse) => {
          this.empleados = res.content;
          this.totalPages = res.totalPages;
          this.totalElements = res.totalElements;
          this.announcer.announceStatus(`Listado actualizado: ${res.totalElements} registros.`);
        },
        error: (err: HttpErrorResponse) => this.announcer.announceError(this.readableError(err)),
        complete: () => {
          this.loading = false;
        }
      });
  }

  aplicarFiltros(): void {
    this.cargarEmpleados(0);
  }

  limpiarFiltros(): void {
    this.filtersForm.reset({ nombre: '', clave: '', sort: 'asc', includeInactive: false });
    this.cargarEmpleados(0);
  }

  editar(empleado: Empleado): void {
    this.selectedEmpleado = empleado;
    this.announcer.announceStatus(`Edicion preparada para ${empleado.clave}.`);
  }

  clearSelection(): void {
    this.selectedEmpleado = null;
  }

  guardar(payload: EmpleadoFormPayload): void {
    this.announcer.clear();
    this.submitting = true;

    const request$ = payload.clave
      ? this.empleadosApi.actualizar(payload.clave, {
          nombre: payload.nombre,
          direccion: payload.direccion,
          telefono: payload.telefono,
          version: payload.version
        })
      : this.empleadosApi.crear({
          nombre: payload.nombre,
          direccion: payload.direccion,
          telefono: payload.telefono
        });

    request$.subscribe({
      next: () => {
        this.clearSelection();
        this.cargarEmpleados(this.page);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 409 && payload.clave) {
          this.handleConflict(payload.clave);
          return;
        }
        this.announcer.announceError(this.readableError(err));
      },
      complete: () => {
        this.submitting = false;
      }
    });
  }

  eliminar(clave: string): void {
    this.announcer.clear();

    if (!confirm(`Se marcara como inactivo el empleado ${clave}. Deseas continuar?`)) {
      return;
    }

    this.deletingKey = clave;
    this.empleadosApi.eliminar(clave).subscribe({
      next: () => {
        this.clearSelection();
        this.cargarEmpleados(this.page);
      },
      error: (err: HttpErrorResponse) => {
        this.announcer.announceError(this.readableError(err));
      },
      complete: () => {
        this.deletingKey = null;
      }
    });
  }

  previousPage(): void {
    if (this.page > 0) {
      this.cargarEmpleados(this.page - 1);
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.cargarEmpleados(this.page + 1);
    }
  }

  logout(): void {
    this.authService.logout();
    this.announcer.announceStatus('Sesion cerrada correctamente.');
  }

  loadLatestOnConflict(): void {
    if (!this.conflictCurrent) {
      this.showConflict = false;
      return;
    }

    this.selectedEmpleado = this.conflictCurrent;
    this.showConflict = false;
    this.announcer.announceStatus('Se cargaron los datos mas recientes. Revisa y vuelve a guardar.');
  }

  closeConflict(): void {
    this.showConflict = false;
    this.conflictCurrent = null;
  }

  private handleConflict(clave: string): void {
    this.empleadosApi.obtener(clave).subscribe({
      next: (current) => {
        this.conflictCurrent = current;
        this.showConflict = true;
      },
      error: () => {
        this.announcer.announceError('Se detecto conflicto de version y no se pudo recargar el registro.');
      }
    });
  }

  private readableError(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'No fue posible conectar con el backend.';
    }
    if (err.status === 401) {
      return 'Tu sesion no es valida. Inicia sesion de nuevo.';
    }

    const apiError: ApiErrorResponse | undefined = err.error;
    if (apiError?.message) {
      return apiError.message;
    }

    return `Error ${err.status}: no fue posible completar la operacion.`;
  }
}
