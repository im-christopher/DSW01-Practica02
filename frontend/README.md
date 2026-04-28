# Frontend Angular 21

UI con flujo de 2 pantallas para autenticacion administrativa y CRUD de empleados.

## Requisitos

- Node.js 20+
- npm 10+
- Backend ejecutandose en `http://localhost:8080`

## Ejecutar

```bash
cd frontend
npm install
npm run start -- --port 4201
```

La aplicacion se sirve en `http://localhost:4201`.

## Rutas

- `/login`: pantalla de autenticacion administrativa.
- `/empleados`: pantalla protegida de gestion de empleados.

Si no hay sesion activa en memoria, `/empleados` redirige a `/login`.

## Autenticacion

La UI usa HTTP Basic contra backend en `/api/v1/empleados` para validar acceso.

Credenciales por defecto de desarrollo:

- usuario: `admin`
- contrasena: `admin123`

La sesion se mantiene solo en memoria de la app y se pierde al recargar/cerrar pestana.

## Accesibilidad y usabilidad incluidas

- Enlace "Saltar al contenido" para teclado.
- Indicador de foco visible en todos los controles.
- Contraste de color alto en botones, tablas y mensajes.
- Mensajes de estado y error con `aria-live`.
- Etiquetas claras y mensajes de validacion accionables.
- Dialogo explicito para conflicto de version (HTTP 409) con recarga de datos actuales.
