export interface ApiError {
  code: string;
  message: string;
  details: string[];
}

export interface CredencialesLogin {
  username: string;
  password: string;
}

export interface PerfilAuth {
  clave: string;
  correo: string;
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string | null;
  activo: boolean;
}
