import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { EmpleadosApiService } from '../empleados/empleados-api.service';
import { AuthStateService } from './auth-state.service';
import { SessionService } from './session.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(
    private readonly empleadosApi: EmpleadosApiService,
    private readonly sessionService: SessionService,
    private readonly authStateService: AuthStateService,
    private readonly router: Router
  ) {}

  login(username: string, password: string): Observable<void> {
    this.sessionService.setCredentials(username, password);

    return this.empleadosApi.probeAccess().pipe(
      map(() => {
        this.authStateService.setAuthenticated(username);
      }),
      catchError((error: HttpErrorResponse) => {
        this.sessionService.clear();
        this.authStateService.clear();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.sessionService.clear();
    this.authStateService.clear();
    void this.router.navigate(['/login']);
  }
}
