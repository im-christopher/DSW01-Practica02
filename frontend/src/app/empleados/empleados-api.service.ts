import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Empleado,
  EmpleadoCreateRequest,
  EmpleadoPageResponse,
  EmpleadoUpdateRequest
} from '../models';
import { SessionService } from '../core/session.service';

@Injectable({ providedIn: 'root' })
export class EmpleadosApiService {
  private readonly apiUrl = `${environment.apiBaseUrl}/empleados`;

  constructor(
    private readonly http: HttpClient,
    private readonly sessionService: SessionService
  ) {}

  probeAccess(): Observable<EmpleadoPageResponse> {
    return this.http.get<EmpleadoPageResponse>(this.apiUrl, {
      params: new HttpParams().set('page', 0).set('size', 1),
      headers: this.buildAuth()
    });
  }

  listar(opts: {
    page: number;
    size: number;
    nombre?: string;
    clave?: string;
    sort: 'asc' | 'desc';
    includeInactive?: boolean;
  }): Observable<EmpleadoPageResponse> {
    let params = new HttpParams()
      .set('page', opts.page)
      .set('size', opts.size)
      .set('sort', opts.sort)
      .set('includeInactive', Boolean(opts.includeInactive));

    if (opts.nombre?.trim()) {
      params = params.set('nombre', opts.nombre.trim());
    }

    if (opts.clave?.trim()) {
      params = params.set('clave', opts.clave.trim());
    }

    return this.http.get<EmpleadoPageResponse>(this.apiUrl, {
      params,
      headers: this.buildAuth()
    });
  }

  obtener(clave: string): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.apiUrl}/${clave}`, {
      headers: this.buildAuth()
    });
  }

  crear(body: EmpleadoCreateRequest): Observable<Empleado> {
    return this.http.post<Empleado>(this.apiUrl, body, {
      headers: this.buildAuth()
    });
  }

  actualizar(clave: string, body: EmpleadoUpdateRequest): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/${clave}`, body, {
      headers: this.buildAuth()
    });
  }

  eliminar(clave: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${clave}`, {
      headers: this.buildAuth()
    });
  }

  private buildAuth(): HttpHeaders {
    const credentials = this.sessionService.getCredentials();
    if (!credentials) {
      return new HttpHeaders();
    }

    const token = btoa(`${credentials.username}:${credentials.password}`);
    return new HttpHeaders({ Authorization: `Basic ${token}` });
  }
}
