import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { RouteScheduleDetailComponent } from './route-schedule-detail.component';

describe('RouteSchedule Management Detail Component', () => {
  let comp: RouteScheduleDetailComponent;
  let fixture: ComponentFixture<RouteScheduleDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteScheduleDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./route-schedule-detail.component').then(m => m.RouteScheduleDetailComponent),
              resolve: { routeSchedule: () => of({ id: 21096 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RouteScheduleDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteScheduleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load routeSchedule on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RouteScheduleDetailComponent);

      // THEN
      expect(instance.routeSchedule()).toEqual(expect.objectContaining({ id: 21096 }));
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
