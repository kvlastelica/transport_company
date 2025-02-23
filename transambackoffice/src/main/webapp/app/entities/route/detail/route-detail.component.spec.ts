import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { RouteDetailComponent } from './route-detail.component';

describe('Route Management Detail Component', () => {
  let comp: RouteDetailComponent;
  let fixture: ComponentFixture<RouteDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./route-detail.component').then(m => m.RouteDetailComponent),
              resolve: { route: () => of({ id: 9151 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RouteDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load route on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RouteDetailComponent);

      // THEN
      expect(instance.route()).toEqual(expect.objectContaining({ id: 9151 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
