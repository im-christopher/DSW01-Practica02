import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class A11yAnnouncerService {
  private readonly statusSignal = signal('');
  private readonly errorSignal = signal('');

  readonly status = this.statusSignal.asReadonly();
  readonly error = this.errorSignal.asReadonly();

  announceStatus(message: string): void {
    this.errorSignal.set('');
    this.statusSignal.set(message);
  }

  announceError(message: string): void {
    this.statusSignal.set('');
    this.errorSignal.set(message);
  }

  clear(): void {
    this.statusSignal.set('');
    this.errorSignal.set('');
  }
}
