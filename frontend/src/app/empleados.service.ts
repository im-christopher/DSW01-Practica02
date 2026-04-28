import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Empleado,
  EmpleadoCreateRequest,
  EmpleadoPageResponse,
  EmpleadoUpdateRequest
} from './models';

@Injectable({ providedIn: 'root' })
export class EmpleadosService {
  private readonly apiUrl = '/api/v1/empleados';

  constructor(private readonly http: HttpClient) {}

  listar(opts: {
    page: number;
    size: number;
    nombre?: string;
    clave?: string;
    sort: 'asc' | 'desc';
    username: string;
    password: string;
  }): Observable<EmpleadoPageResponse> {
    let params = new HttpParams()
      .set('page', opts.page)
      .set('size', opts.size)
      .set('sort', opts.sort);

    if (opts.nombre?.trim()) {
      params = params.set('nombre', opts.nombre.trim());
    }

    if (opts.clave?.trim()) {
      params = params.set('clave', opts.clave.trim());
    }

    return this.http.get<EmpleadoPageResponse>(this.apiUrl, {
      params,
      headers: this.buildAuth(opts.username, opts.password)
    });
  }

  crear(
    body: EmpleadoCreateRequest,
    username: string,
    password: string
  ): Observable<Empleado> {
    return this.http.post<Empleado>(this.apiUrl, body, {
      headers: this.buildAuth(username, password)
    });
  }

  actualizar(
    clave: string,
    body: EmpleadoUpdateRequest,
    username: string,
    password: string
  ): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/${clave}`, body, {
      headers: this.buildAuth(username, password)
    });
  }

  eliminar(clave: string, username: string, password: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${clave}`, {
      headers: this.buildAuth(username, password)
    });
  }

  private buildAuth(username: string, password: string): HttpHeaders {
    const token = btoa(`${username}:${password}`);
    return new HttpHeaders({
      Authorization: `Basic ${token}`
    });
  }
}
