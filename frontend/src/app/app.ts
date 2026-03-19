import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

import { AuthSessionService } from './core/auth/auth-session.service';
import { extractApiErrorMessage } from './core/http/api-error.util';
import { CredencialesLogin, PerfilAuth } from './core/models/auth.models';

interface ApiPage<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

interface Empleado {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string | null;
}

interface Departamento {
  clave: string;
  nombre: string;
}

@Component({
  selector: 'app-root',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly authSessionService = inject(AuthSessionService);
  private readonly authCredentialPattern = /^\S+$/;

  protected readonly authForm = this.fb.nonNullable.group({
    username: ['admin', [Validators.required, Validators.pattern(this.authCredentialPattern)]],
    password: ['admin123', [Validators.required, Validators.pattern(this.authCredentialPattern)]],
  });

  protected readonly empleadoForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.maxLength(100)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    contrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
    departamentoClave: [''],
  });

  protected readonly departamentoForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
  });

  protected perfil: PerfilAuth | null = null;
  protected empleados: Empleado[] = [];
  protected departamentos: Departamento[] = [];

  protected empleadosPage = 0;
  protected empleadosSize = 5;
  protected empleadosTotalPages = 0;
  protected departamentosPage = 0;
  protected departamentosSize = 5;
  protected departamentosTotalPages = 0;

  protected loadingAuth = false;
  protected loadingEmpleados = false;
  protected loadingDepartamentos = false;
  protected savingEmpleado = false;
  protected savingDepartamento = false;

  protected empleadoEditingClave: string | null = null;
  protected departamentoEditingClave: string | null = null;
  protected view: 'empleados' | 'departamentos' = 'empleados';
  protected message = '';
  protected errorMessage = '';

  protected get isAuthenticated(): boolean {
    return this.perfil !== null;
  }

  protected get isEditingEmpleado(): boolean {
    return this.empleadoEditingClave !== null;
  }

  protected get isEditingDepartamento(): boolean {
    return this.departamentoEditingClave !== null;
  }

  protected async login(): Promise<void> {
    if (this.loadingAuth) {
      return;
    }

    if (this.authForm.invalid) {
      this.authForm.markAllAsTouched();
      this.errorMessage = 'Usuario y contrasena son obligatorios y no deben contener espacios.';
      return;
    }

    this.resetFeedback();
    this.loadingAuth = true;

    try {
      const credentials: CredencialesLogin = this.authForm.getRawValue();
      this.authSessionService.setCredentials(credentials);

      this.perfil = await firstValueFrom(
        this.http.get<PerfilAuth>('/api/v1/empleados/auth/me', {
          headers: this.authHeaders(),
        })
      );

      this.authSessionService.setProfile(this.perfil);

      await Promise.all([this.loadEmpleados(0), this.loadDepartamentos(0)]);
      this.message = `Sesion iniciada como ${this.perfil.nombre}`;
    } catch (error) {
      this.perfil = null;
      this.authSessionService.clear();
      this.errorMessage = this.formatError(error, 'No fue posible iniciar sesion.');
    } finally {
      this.loadingAuth = false;
    }
  }

  protected logout(): void {
    this.authSessionService.clear();
    this.perfil = null;
    this.empleados = [];
    this.departamentos = [];
    this.empleadoEditingClave = null;
    this.departamentoEditingClave = null;
    this.message = 'Sesion cerrada.';
    this.errorMessage = '';
  }

  protected async loadEmpleados(page: number): Promise<void> {
    if (!this.isAuthenticated) {
      return;
    }

    this.loadingEmpleados = true;
    this.errorMessage = '';

    try {
      const response = await firstValueFrom(
        this.http.get<ApiPage<Empleado>>('/api/v1/empleados', {
          headers: this.authHeaders(),
          params: {
            page: String(page),
            size: String(this.empleadosSize),
          },
        })
      );

      this.empleados = response.content;
      this.empleadosPage = response.page;
      this.empleadosTotalPages = response.totalPages;
    } catch (error) {
      this.errorMessage = this.formatError(error, 'No fue posible cargar empleados.');
    } finally {
      this.loadingEmpleados = false;
    }
  }

  protected async loadDepartamentos(page: number): Promise<void> {
    if (!this.isAuthenticated) {
      return;
    }

    this.loadingDepartamentos = true;
    this.errorMessage = '';

    try {
      const response = await firstValueFrom(
        this.http.get<ApiPage<Departamento>>('/api/v1/departamentos', {
          headers: this.authHeaders(),
          params: {
            page: String(page),
            size: String(this.departamentosSize),
          },
        })
      );

      this.departamentos = response.content;
      this.departamentosPage = response.page;
      this.departamentosTotalPages = response.totalPages;
    } catch (error) {
      this.errorMessage = this.formatError(error, 'No fue posible cargar departamentos.');
    } finally {
      this.loadingDepartamentos = false;
    }
  }

  protected startCreateEmpleado(): void {
    this.empleadoEditingClave = null;
    this.empleadoForm.reset({
      nombre: '',
      direccion: '',
      telefono: '',
      correo: '',
      contrasena: '',
      departamentoClave: '',
    });
    this.empleadoForm.controls.correo.setValidators([
      Validators.required,
      Validators.email,
      Validators.maxLength(150),
    ]);
    this.empleadoForm.controls.correo.enable();
    this.empleadoForm.controls.correo.updateValueAndValidity();
    this.empleadoForm.controls.contrasena.setValidators([
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(72),
    ]);
    this.empleadoForm.controls.contrasena.updateValueAndValidity();
  }

  protected startEditEmpleado(empleado: Empleado): void {
    this.empleadoEditingClave = empleado.clave;
    this.view = 'empleados';
    this.empleadoForm.reset({
      nombre: empleado.nombre,
      direccion: empleado.direccion,
      telefono: empleado.telefono,
      correo: '',
      contrasena: '',
      departamentoClave: empleado.departamentoClave ?? '',
    });

    this.empleadoForm.controls.correo.clearValidators();
    this.empleadoForm.controls.correo.disable();
    this.empleadoForm.controls.correo.updateValueAndValidity();
    this.empleadoForm.controls.contrasena.setValidators([
      Validators.minLength(8),
      Validators.maxLength(72),
    ]);
    this.empleadoForm.controls.contrasena.updateValueAndValidity();
  }

  protected cancelEmpleadoEdition(): void {
    this.startCreateEmpleado();
  }

  protected async submitEmpleado(): Promise<void> {
    if (!this.isAuthenticated || this.empleadoForm.invalid || this.savingEmpleado) {
      return;
    }

    this.resetFeedback();
    this.savingEmpleado = true;

    try {
      const value = this.empleadoForm.getRawValue();

      if (this.empleadoEditingClave) {
        const payload = {
          nombre: value.nombre,
          direccion: value.direccion,
          telefono: value.telefono,
          contrasena: value.contrasena.trim() || undefined,
        };

        await firstValueFrom(
          this.http.put<Empleado>(`/api/v1/empleados/${this.empleadoEditingClave}`, payload, {
            headers: this.authHeaders(),
          })
        );

        if (value.departamentoClave.trim()) {
          await firstValueFrom(
            this.http.patch<Empleado>(
              `/api/v1/empleados/${this.empleadoEditingClave}/departamento`,
              { departamentoClave: value.departamentoClave.trim() },
              { headers: this.authHeaders() }
            )
          );
        }

        this.message = `Empleado ${this.empleadoEditingClave} actualizado.`;
      } else {
        const payload = {
          nombre: value.nombre,
          direccion: value.direccion,
          telefono: value.telefono,
          correo: value.correo,
          contrasena: value.contrasena,
          departamentoClave: value.departamentoClave.trim() || undefined,
        };

        await firstValueFrom(
          this.http.post<Empleado>('/api/v1/empleados', payload, {
            headers: this.authHeaders(),
          })
        );

        this.message = 'Empleado creado correctamente.';
      }

      this.startCreateEmpleado();
      await this.loadEmpleados(this.empleadosPage);
      await this.loadDepartamentos(this.departamentosPage);
    } catch (error) {
      this.errorMessage = this.formatError(error, 'No fue posible guardar el empleado.');
    } finally {
      this.savingEmpleado = false;
    }
  }

  protected async deleteEmpleado(clave: string): Promise<void> {
    if (!this.isAuthenticated) {
      return;
    }

    this.resetFeedback();

    try {
      await firstValueFrom(
        this.http.delete<void>(`/api/v1/empleados/${clave}`, {
          headers: this.authHeaders(),
        })
      );
      this.message = `Empleado ${clave} eliminado.`;
      await this.loadEmpleados(this.empleadosPage);
    } catch (error) {
      this.errorMessage = this.formatError(error, 'No fue posible eliminar el empleado.');
    }
  }

  protected startCreateDepartamento(): void {
    this.departamentoEditingClave = null;
    this.departamentoForm.reset({ nombre: '' });
  }

  protected startEditDepartamento(departamento: Departamento): void {
    this.departamentoEditingClave = departamento.clave;
    this.view = 'departamentos';
    this.departamentoForm.reset({ nombre: departamento.nombre });
  }

  protected cancelDepartamentoEdition(): void {
    this.startCreateDepartamento();
  }

  protected async submitDepartamento(): Promise<void> {
    if (!this.isAuthenticated || this.departamentoForm.invalid || this.savingDepartamento) {
      return;
    }

    this.resetFeedback();
    this.savingDepartamento = true;

    try {
      const payload = this.departamentoForm.getRawValue();

      if (this.departamentoEditingClave) {
        await firstValueFrom(
          this.http.put<Departamento>(
            `/api/v1/departamentos/${this.departamentoEditingClave}`,
            payload,
            { headers: this.authHeaders() }
          )
        );
        this.message = `Departamento ${this.departamentoEditingClave} actualizado.`;
      } else {
        await firstValueFrom(
          this.http.post<Departamento>('/api/v1/departamentos', payload, {
            headers: this.authHeaders(),
          })
        );
        this.message = 'Departamento creado correctamente.';
      }

      this.startCreateDepartamento();
      await this.loadDepartamentos(this.departamentosPage);
    } catch (error) {
      this.errorMessage = this.formatError(error, 'No fue posible guardar el departamento.');
    } finally {
      this.savingDepartamento = false;
    }
  }

  protected async deleteDepartamento(clave: string): Promise<void> {
    if (!this.isAuthenticated) {
      return;
    }

    this.resetFeedback();

    try {
      await firstValueFrom(
        this.http.delete<void>(`/api/v1/departamentos/${clave}`, {
          headers: this.authHeaders(),
        })
      );
      this.message = `Departamento ${clave} eliminado.`;
      await this.loadDepartamentos(this.departamentosPage);
    } catch (error) {
      this.errorMessage = this.formatError(error, 'No fue posible eliminar el departamento.');
    }
  }

  protected canPrev(page: number): boolean {
    return page > 0;
  }

  protected canNext(page: number, totalPages: number): boolean {
    return page + 1 < totalPages;
  }

  private authHeaders(): HttpHeaders {
    return this.authSessionService.getAuthHeaders();
  }

  private resetFeedback(): void {
    this.message = '';
    this.errorMessage = '';
  }

  private formatError(error: unknown, fallback: string): string {
    return extractApiErrorMessage(error, fallback);
  }
}
