import { PerfilAuth } from '../../core/models/auth.models';

export type EmpleadoLoginViewState = 'idle' | 'loading' | 'authenticated' | 'error';

export interface EmpleadoLoginState {
  viewState: EmpleadoLoginViewState;
  profile: PerfilAuth | null;
  errorMessage: string;
}

export const initialEmpleadoLoginState: EmpleadoLoginState = {
  viewState: 'idle',
  profile: null,
  errorMessage: '',
};
