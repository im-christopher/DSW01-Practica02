import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { LoginPageComponent } from './auth/login-page.component';

export const appRoutes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: LoginPageComponent },
  {
    path: 'empleados',
    canActivate: [authGuard],
    loadChildren: () => import('./empleados/empleados.routes').then((m) => m.EMPLEADOS_ROUTES)
  },
  { path: '**', redirectTo: 'login' }
];
