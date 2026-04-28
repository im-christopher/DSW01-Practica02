import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { A11yAnnouncerService } from '../shared/a11y-announcer.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  loading = false;

  readonly loginForm = this.fb.nonNullable.group({
    username: ['admin', Validators.required],
    password: ['admin123', Validators.required]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    readonly announcer: A11yAnnouncerService
  ) {}

  submit(): void {
    this.announcer.clear();

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.announcer.announceError('Captura usuario y contrasena para continuar.');
      return;
    }

    this.loading = true;
    const { username, password } = this.loginForm.getRawValue();

    this.authService.login(username, password).subscribe({
      next: () => {
        this.announcer.announceStatus('Acceso concedido. Redirigiendo a empleados.');
        void this.router.navigate(['/empleados']);
      },
      error: (error: HttpErrorResponse) => {
        const message = error.status === 401
          ? 'Credenciales invalidas. Verifica usuario o contrasena.'
          : 'No fue posible validar el acceso en este momento.';
        this.announcer.announceError(message);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
