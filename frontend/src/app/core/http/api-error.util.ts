import { HttpErrorResponse } from '@angular/common/http';

import { ApiError } from '../models/auth.models';

export function extractApiErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof HttpErrorResponse) {
    const payload = error.error as ApiError | null;

    if (payload && typeof payload.message === 'string' && payload.message.length > 0) {
      return payload.message;
    }

    if (typeof error.message === 'string' && error.message.length > 0) {
      return error.message;
    }
  }

  return fallback;
}
