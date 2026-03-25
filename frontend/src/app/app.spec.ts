import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render login title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Iniciar sesion');
  });

  it('should block login when username or password are blank', async () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance as any;

    app.authForm.setValue({ username: '', password: '' });

    await app.login();

    expect(app.errorMessage).toContain('obligatorios');
    expect(app.authForm.controls.username.invalid).toBe(true);
    expect(app.authForm.controls.password.invalid).toBe(true);
  });

  it('should block login when username contains spaces', async () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance as any;

    app.authForm.setValue({ username: 'ad min', password: 'admin123' });

    await app.login();

    expect(app.errorMessage).toContain('no deben contener espacios');
    expect(app.authForm.controls.username.invalid).toBe(true);
  });

  it('should block login when password contains spaces', async () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance as any;

    app.authForm.setValue({ username: 'admin', password: 'admin 123' });

    await app.login();

    expect(app.errorMessage).toContain('no deben contener espacios');
    expect(app.authForm.controls.password.invalid).toBe(true);
  });

  it('should keep login validation constraints for username with spaces', async () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance as any;

    app.authForm.setValue({ username: 'empleado demo', password: 'empleado123' });

    await app.login();

    expect(app.errorMessage).toContain('no deben contener espacios');
    expect(app.isAuthenticated).toBe(false);
  });
});
