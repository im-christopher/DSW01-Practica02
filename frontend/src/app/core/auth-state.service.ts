import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthStateService {
  private readonly authenticatedSignal = signal(false);
  private readonly usernameSignal = signal('');

  readonly authenticated = this.authenticatedSignal.asReadonly();
  readonly username = this.usernameSignal.asReadonly();

  setAuthenticated(username: string): void {
    this.usernameSignal.set(username);
    this.authenticatedSignal.set(true);
  }

  clear(): void {
    this.usernameSignal.set('');
    this.authenticatedSignal.set(false);
  }
}
