import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { EmpleadoAuthFacade } from '../../core/auth/empleado-auth.facade';
import { extractApiErrorMessage } from '../../core/http/api-error.util';
import { initialEmpleadoLoginState } from './empleado-login.state';

@Component({
  selector: 'app-empleado-login-container',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './empleado-login.container.html',
  styleUrl: './empleado-login.container.scss',
})
export class EmpleadoLoginContainerComponent {
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly empleadoAuthFacade = inject(EmpleadoAuthFacade);

  protected readonly loginForm = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.pattern(/^\S+$/)]],
  });

  protected state = { ...initialEmpleadoLoginState };

  protected async login(): Promise<void> {
    if (this.loginForm.invalid || this.state.viewState === 'loading') {
      this.loginForm.markAllAsTouched();
      this.state = {
        ...this.state,
        viewState: 'error',
        errorMessage: 'Correo y contrasena son obligatorios y validos.',
      };
      return;
    }

    this.state = {
      ...this.state,
      viewState: 'loading',
      errorMessage: '',
    };

    try {
      const { correo, password } = this.loginForm.getRawValue();
      const profile = await this.empleadoAuthFacade.login(correo, password);
      this.state = {
        viewState: 'authenticated',
        profile,
        errorMessage: '',
      };
    } catch (error) {
      const actorMismatch = error instanceof Error && error.message === 'ACTOR_NO_EMPLEADO';
      this.state = {
        ...this.state,
        profile: null,
        viewState: 'error',
        errorMessage: actorMismatch
          ? 'Tu cuenta no tiene permisos para el portal de empleado.'
          : extractApiErrorMessage(error, 'Credenciales invalidas.'),
      };
    } finally {
      this.requestRender();
    }
  }

  protected logout(): void {
    this.empleadoAuthFacade.logout();
    this.loginForm.reset({ correo: '', password: '' });
    this.state = { ...initialEmpleadoLoginState };
    this.requestRender();
  }

  private requestRender(): void {
    queueMicrotask(() => this.cdr.detectChanges());
  }
}
