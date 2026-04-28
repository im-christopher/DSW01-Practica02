export interface Empleado {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  version: number;
  activo: boolean;
}

export interface EmpleadoPageResponse {
  content: Empleado[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface EmpleadoCreateRequest {
  nombre: string;
  direccion: string;
  telefono: string;
}

export interface EmpleadoUpdateRequest {
  nombre: string;
  direccion: string;
  telefono: string;
  version: number;
}

export interface ApiErrorResponse {
  code: string;
  message: string;
}
