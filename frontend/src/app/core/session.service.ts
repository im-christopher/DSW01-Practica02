import { Injectable } from '@angular/core';

export interface SessionCredentials {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class SessionService {
  private credentials: SessionCredentials | null = null;

  setCredentials(username: string, password: string): void {
    this.credentials = { username, password };
  }

  getCredentials(): SessionCredentials | null {
    return this.credentials;
  }

  clear(): void {
    this.credentials = null;
  }
}
